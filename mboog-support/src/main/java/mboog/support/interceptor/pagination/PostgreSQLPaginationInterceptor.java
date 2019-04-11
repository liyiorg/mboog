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
public class PostgreSQLPaginationInterceptor extends AbstractPaginationInterceptor {

    @Override
    protected String buildSelectPagingSql(String targetSql, int pageNo, int pageSize, boolean sqlImprovement) {

        return String.format("%s limit %d offset %d", targetSql, (pageNo - 1) * pageSize, pageSize);
    }

}
