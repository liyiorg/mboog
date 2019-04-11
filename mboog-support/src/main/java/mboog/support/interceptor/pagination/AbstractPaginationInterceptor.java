package mboog.support.interceptor.pagination;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

import mboog.support.util.FieldUtil;

/**
 * @author LiYi
 */
public abstract class AbstractPaginationInterceptor implements Interceptor {

    private static final Pattern PATTERN_SQL_BLANK = Pattern.compile("\\s+");

    private static final String BLANK = " ";

    private static final String FIELD_DELEGATE = "delegate";

    private static final String FIELD_ROWBOUNDS = "rowBounds";

    private static final String FIELD_MAPPEDSTATEMENT = "mappedStatement";

    private static final String FIELD_SQL = "sql";

    private static final Pattern PATTERN_IS_SELECT = Pattern.compile("\\s*SELECT\\s+.+\\s+FROM(\\s+|\\().*", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_CLEAR_ORDERBY = Pattern.compile("\\s*(SELECT\\s+.+\\s+FROM(\\s+|\\().*)(\\s+ORDER\\s+BY)\\s+.*", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_IS_HAVING = Pattern.compile(".*\\s+HAVING\\s+.*", Pattern.CASE_INSENSITIVE);

    private static Log log = LogFactory.getLog(AbstractPaginationInterceptor.class);

    public Object intercept(Invocation invocation) throws Throwable {

        Pagination.Limit limit = Pagination.get();
        if (limit != null) {
            Connection connection = (Connection) invocation.getArgs()[0];
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();

            StatementHandler handler = (StatementHandler) FieldUtil.readField(statementHandler, FIELD_DELEGATE);
            MappedStatement mappedStatement = (MappedStatement) FieldUtil.readField(handler, FIELD_MAPPEDSTATEMENT);
            BoundSql boundSql = handler.getBoundSql();
            String baseSql = boundSql.getSql();
            if (isSelectSql(baseSql)) {
                // replace all blank
                String targetSql = replaceSqlBlank(baseSql);
                // 获取分页 SQL
                String pagingSql = buildSelectPagingSql(targetSql, limit.getPageNo(), limit.getPageSize(), limit.isSqlImprovement());
                // 覆盖boundSql
                FieldUtil.writeDeclaredField(boundSql, FIELD_SQL, pagingSql);
                // 获取分页统计SQL
                String totalSql = buildSelectTotalSql(targetSql, limit.isSqlImprovement());
                log.debug("Pagination \nBASE_SQl:" + baseSql + "\nPAGE_SQl:" + pagingSql + "\nTOTAL_SQl:" + totalSql);
                // 获取分页统计数量
                Long total = queryTotal(totalSql, boundSql, mappedStatement, connection);
                // 设置分页数
                limit.setTotal(total);
                Pagination.set(limit);
            } else {
                log.debug("No select sql with:" + baseSql);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof RoutingStatementHandler) {
            try {
                if (Pagination.get() != null) {
                    Field delegateField = FieldUtil.getField(RoutingStatementHandler.class, FIELD_DELEGATE);
                    StatementHandler statementHandler = (StatementHandler) delegateField.get(target);
                    if (statementHandler instanceof BaseStatementHandler) {
                        Field rowboundsField = FieldUtil.getField(BaseStatementHandler.class, FIELD_ROWBOUNDS);
                        RowBounds rowBounds = (RowBounds) rowboundsField.get(statementHandler);
                        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
                            return Plugin.wrap(target, this);
                        }
                    }
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 查询总记录数
     *
     * @param totalSql        totalSql
     * @param boundSql        boundSql
     * @param mappedStatement mappedStatement
     * @param connection      connection
     * @return Long
     * @throws SQLException
     */
    private Long queryTotal(String totalSql, BoundSql boundSql, MappedStatement mappedStatement, Connection connection)
            throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Object parameterObject = boundSql.getParameterObject();
        BoundSql totalBoundSql = new BoundSql(mappedStatement.getConfiguration(), totalSql, parameterMappings,
                parameterObject);
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject,
                totalBoundSql);

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(totalSql);
            parameterHandler.setParameters(pstmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return null;
    }

    /**
     * 获取select count SQL
     *
     * @param orgSql         orgSql
     * @param sqlImprovement sqlImprovement
     * @return String
     */
    protected String buildSelectTotalSql(String orgSql, boolean sqlImprovement) {
        String newSql = orgSql;
        if (sqlImprovement) {
            try {
                // 清除order by语句
                Matcher matcher = PATTERN_CLEAR_ORDERBY.matcher(newSql);
                if (matcher.matches()) {
                    newSql = matcher.replaceAll("$1");
                }
                if (!PATTERN_IS_HAVING.matcher(newSql).matches()) {
                    String lowerCaseSql = newSql.toLowerCase();
                    String afterSql = selectFromAfterSQL(newSql, lowerCaseSql, 0);
                    if (afterSql != null) {
                        return String.format("select count(1)%s", afterSql);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return String.format("select count(1) from(%s)", newSql);
    }

    /**
     * 获取 SQL 语句 from 语法格式 后的语句
     *
     * @param baseSql      原始SQL
     * @param lowerCaseSql 小写SQL
     * @param index        检查开始位置
     * @return
     */
    private String selectFromAfterSQL(String baseSql, String lowerCaseSql, int index) {
        int i = lowerCaseSql.indexOf(" from", index);
        if (i != -1) {
            String temp = lowerCaseSql.substring(i);
            if (temp.startsWith(" from ") || temp.startsWith(" from(")) {
                return baseSql.substring(i);
            } else {
                return selectFromAfterSQL(baseSql, lowerCaseSql, i + 5);
            }
        }
        return null;
    }

    /**
     * 生成分页查询语句
     *
     * @param targetSql
     * @param pageNo         pageNo
     * @param pageSize       pageSize
     * @param sqlImprovement sqlImprovement
     * @return String
     */
    protected abstract String buildSelectPagingSql(String targetSql, int pageNo, int pageSize, boolean sqlImprovement);

    private String replaceSqlBlank(String originalSql) {
        Matcher matcher = PATTERN_SQL_BLANK.matcher(originalSql);
        return matcher.replaceAll(BLANK);
    }

    /**
     * select 语句判断
     *
     * @param sql sql
     * @return boolean
     */
    private boolean isSelectSql(String sql) {
        if (sql != null) {
            return PATTERN_IS_SELECT.matcher(sql).matches();
        }
        return false;
    }

}
