package mboog.support.example;

/**
 * @author LiYi
 */
public interface PaginationAble<M> extends ExampleData {

    default void setLimitStart(Long limitStart) {
        dataSet(ExampleConstants.LIMIT_START, limitStart);
    }

    default Long getLimitStart() {
        return dataGet(ExampleConstants.LIMIT_START);
    }

    default void setLimitEnd(Long limitEnd) {
        dataSet(ExampleConstants.LIMIT_END, limitEnd);
    }

    default Long getLimitEnd() {
        return dataGet(ExampleConstants.LIMIT_END);
    }

    default M limit(Long limitStart, Long limitEnd) {
        setLimitStart(limitStart);
        setLimitEnd(limitEnd);
        return (M) this;
    }

    default M limit(Long limit) {
        return limit(0L, limit);
    }

    void setOrderByClause(String orderByClause);

    String getOrderByClause();

    String getDatabaseType();
}
