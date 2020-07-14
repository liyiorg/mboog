package mboog.support.example;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * @param <M>
 * @param <T>
 * @param <C>
 * @author LiYi
 */
@SuppressWarnings("unchecked")
public abstract class AbstractExample<M extends AbstractExample, T extends AbstractGeneratedCriteria<?>, C extends Enum<C>> implements ExampleData {

    protected Supplier<T> supplier;

    protected List<T> oredCriteria = new ArrayList<>();

    protected String orderByClause;

    protected boolean distinct;

    protected String databaseType;

    private Map<String, Object> dataMap;

    private boolean ignoreNull;

    private boolean ignoreEmpty;

    /**
     * Ignore value if it's null or empty.
     *
     * @return this
     */
    public M ignoreNull() {
        return ignoreNull(true);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignore ignore
     * @return this
     */
    public M ignoreNull(boolean ignore) {
        return ignoreNull(ignore, ignore);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignoreNull  value is null
     * @param ignoreEmpty value is empty
     * @return this
     */
    public M ignoreNull(boolean ignoreNull, boolean ignoreEmpty) {
        this.ignoreNull = ignoreNull;
        this.ignoreEmpty = ignoreEmpty;
        return (M) this;
    }

    /**
     * Inner Method <br>
     * Set data
     *
     * @param key key
     * @param t   value
     * @param <D> value type
     */
    @Override
    public <D> void dataSet(String key, D t) {
        if (dataMap == null) {
            dataMap = new HashMap<>(6);
        }
        dataMap.put(key, t);
    }

    /**
     * Inner Method <br>
     * Get data
     *
     * @param key key
     * @param <D> value type
     * @return value
     */
    @Override
    public <D> D dataGet(String key) {
        if (dataMap != null) {
            Object value = dataMap.get(key);
            if (Objects.nonNull(value)) {
                return (D) dataMap.get(key);
            }
        }
        return null;
    }

    /**
     * Inner Method <br>
     * Get oredCriteria List
     *
     * @return oredList
     */
    public List<T> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * Where join
     *
     * @return Criteria
     */
    public T or() {
        T criteria = createCriteriaInternal();
        criteria.prefix = AbstractGeneratedCriteria.PREFIX_OR;
        criteria.prefixInner = AbstractGeneratedCriteria.PREFIX_AND;
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * Where join Consumer
     *
     * @param consumer consumer
     * @return this
     */
    public M or(Consumer<T> consumer) {
        consumer.accept(or());
        return (M) this;
    }

    /**
     * Where join
     *
     * @return Criteria
     */
    public T and() {
        T criteria = createCriteriaInternal();
        criteria.prefix = AbstractGeneratedCriteria.PREFIX_AND;
        criteria.prefixInner = AbstractGeneratedCriteria.PREFIX_OR;
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * Where join Consumer
     *
     * @param consumer consumer
     * @return this
     */
    public M and(Consumer<T> consumer) {
        consumer.accept(and());
        return (M) this;
    }

    /**
     * Inner Method <br>
     * Create Criteria
     *
     * @return Criteria
     */
    public T createCriteriaInternal() {
        T criteria = supplier.get();
        criteria.ignoreNull(ignoreNull, ignoreEmpty);
        return criteria;
    }

    /**
     * Inner Method <br>
     * Set order by
     *
     * @param orderByClause
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public M orderBy(String column) {
        return orderBy(oba -> oba.appendAsc(column));
    }

    public M orderBy(C column) {
        return orderBy(oba -> oba.appendAsc(column));
    }

    public M orderByDesc(String column) {
        return orderBy(oba -> oba.appendDesc(column));
    }

    public M orderByDesc(C column) {
        return orderBy(oba -> oba.appendDesc(column));
    }

    public M orderBy(Consumer<OrderByAppend<C>> consumer) {
        OrderByAppend orderByAppend = new OrderByAppend<C>();
        orderByAppend.ignoreNull(this.ignoreNull, this.ignoreEmpty);
        consumer.accept(orderByAppend);
        this.orderByClause = orderByAppend.toOrderByString();
        return (M) this;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public M distinct(boolean distinct) {
        this.distinct = distinct;
        return (M) this;
    }

    public M distinct() {
        return distinct(true);
    }

    public boolean isDistinct() {
        return distinct;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        dataMap = null;
        ignoreNull = false;
        ignoreEmpty = false;
    }
}
