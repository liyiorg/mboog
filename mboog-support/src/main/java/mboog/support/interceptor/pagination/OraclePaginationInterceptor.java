package mboog.support.interceptor.pagination;

import java.sql.Connection;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Signature;

/**
 * @author LiYi
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class OraclePaginationInterceptor extends AbstractPaginationInterceptor {

    @Override
    protected String buildSelectPagingSql(String targetSql, int pageNo, int pageSize, boolean sqlImprovement) {
        StringBuilder sqlBuilder = new StringBuilder(targetSql);
        sqlBuilder.insert(0, "select * from ( select tt.*, rownum rowno from (");
        sqlBuilder.append(") ");
        sqlBuilder.append("tt where rownum < " + pageNo * pageSize).append(")");
        sqlBuilder.append("table_alias where table_alias.rowno >= " + ((pageNo - 1) * pageSize));
        return sqlBuilder.toString();
    }

}
