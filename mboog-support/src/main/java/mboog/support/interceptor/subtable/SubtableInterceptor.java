package mboog.support.interceptor.subtable;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.RowBounds;

import mboog.support.interceptor.subtable.Subtable.Sub;
import mboog.support.util.FieldUtil;

/**
 * @author LiYi
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SubtableInterceptor implements Interceptor {

    private static final Pattern PATTERN_SQL_BLANK = Pattern.compile("\\s+");

    private static final String BLANK = " ";

    private static final String FIELD_DELEGATE = "delegate";

    private static final String FIELD_ROWBOUNDS = "rowBounds";

    private static final String FIELD_SQL = "sql";

    private static final Pattern PATTERN;

    private static Log log = LogFactory.getLog(SubtableInterceptor.class);

    static {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(\\s*(");
        stringBuilder.append("INSERT(\\s+INTO)?|");
        stringBuilder.append("DELETE\\s+FROM|");
        stringBuilder.append("UPDATE|");
        stringBuilder.append("SELECT\\s+.+\\s+FROM|");
        stringBuilder.append("REPLACE(\\s+INTO)?|");                    //MySQL
        stringBuilder.append("MERGE\\s+INTO");                            //Oracle
        stringBuilder.append(")\\s+[\",`]?)(((\\w+\\.){0,2})\\w+)([\",`]?(\\s+.*)?)");    //[" PostgreSQL] [` MySQL]
        PATTERN = Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Sub subtable = Subtable.get();
        if (subtable != null) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) invocation.getTarget();
            StatementHandler handler = (StatementHandler) FieldUtil.readField(statementHandler, FIELD_DELEGATE);
            BoundSql boundSql = handler.getBoundSql();
            String baseSql = boundSql.getSql();
            // replace all blank
            String targetSql = replaceSqlBlank(baseSql);
            Matcher matcher = PATTERN.matcher(targetSql);
            if (matcher.matches()) {
                int groupCount = matcher.groupCount();
                String tempSql = null;
                switch (subtable.getType()) {
                    case 1:
                        tempSql = matcher.replaceFirst("$1$" + (groupCount - 4) + subtable.getName() + "$" + (groupCount - 1));
                        break;
                    case 2:
                        tempSql = matcher.replaceFirst("$1$" + (groupCount - 3) + subtable.getName() + "$" + (groupCount - 1));
                        break;
                    case 3:
                        tempSql = matcher.replaceFirst("$1" + subtable.getName() + "$" + (groupCount - 1));
                        break;
                }
                // set boundSql
                FieldUtil.writeDeclaredField(boundSql, FIELD_SQL, tempSql);
                log.debug("Subtable from [" + targetSql + "] to [" + tempSql + "]");
            }
        }
        return invocation.proceed();
    }

    private String replaceSqlBlank(String originalSql) {
        Matcher matcher = PATTERN_SQL_BLANK.matcher(originalSql);
        return matcher.replaceAll(BLANK);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof RoutingStatementHandler) {
            try {
                if (Subtable.get() != null) {
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

}
