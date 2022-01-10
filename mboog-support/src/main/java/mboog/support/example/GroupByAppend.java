package mboog.support.example;

import java.util.*;

/**
 * @param <C>
 * @author LiYi
 */
public class GroupByAppend<C extends Enum<C>> {

    private Set<String> set;

    private boolean ignoreNull;

    private boolean ignoreEmpty;

    private String having;

    public GroupByAppend() {
        this.set = new LinkedHashSet<>();
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @return this
     */
    public GroupByAppend ignoreNull() {
        return ignoreNull(true);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignore ignore
     * @return this
     */
    public GroupByAppend ignoreNull(boolean ignore) {
        return ignoreNull(ignore, ignore);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignoreNull  value is null
     * @param ignoreEmpty value is empty
     * @return this
     */
    public GroupByAppend ignoreNull(boolean ignoreNull, boolean ignoreEmpty) {
        this.ignoreNull = ignoreNull;
        this.ignoreEmpty = ignoreEmpty;
        return this;
    }

    public GroupByAppend having(String having) {
        this.having = having;
        return this;
    }


    public GroupByAppend append(String column) {
        if (column == null) {
            if (ignoreNull) {
                return this;
            }
            throw new RuntimeException("Value for column cannot be null");
        } else if ("".equals(column.trim())) {
            if (ignoreEmpty) {
                return this;
            }
            throw new RuntimeException("Value for column cannot be empty");
        }
        set.add(column);
        return this;
    }

    public GroupByAppend append(C column) {
        return append(((CInterface) column).aliasDelimitedName());
    }

    public String toGroupByString() {
        if (set.size() > 0) {
            List<String> list = new ArrayList<>();
            set.forEach(key -> list.add(key));
            String sql = String.join(",", list);
            if (Objects.nonNull(having) && having.trim().length() > 0) {
                return String.format("%s having %s", sql, having);
            }
            return sql;
        }
        return null;
    }
}
