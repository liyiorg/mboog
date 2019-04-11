package mboog.support.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderByAppend<C extends Enum<C>> {

    private static final String DESC = "desc";

    private Map<String, String> map;

    private boolean ignoreNull;

    private boolean ignoreEmpty;

    public OrderByAppend() {
        this.map = new LinkedHashMap<>();
    }

    /**
     * Ignore value if it's null or empty.
     * @return this
     */
    public OrderByAppend ignoreNull() {
        return ignoreNull(true);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignore ignore
     * @return this
     */
    public OrderByAppend ignoreNull(boolean ignore) {
        return ignoreNull(ignore, ignore);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignoreNull  value is null
     * @param ignoreEmpty value is empty
     * @return this
     */
    public OrderByAppend ignoreNull(boolean ignoreNull, boolean ignoreEmpty) {
        this.ignoreNull = ignoreNull;
        this.ignoreEmpty = ignoreEmpty;
        return this;
    }


    private OrderByAppend append(String column, String tag) {
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
        map.put(column, tag);
        return this;
    }

    public OrderByAppend appendAsc(C column) {
        return append(((CInterface) column).aliasDelimitedName(), null);
    }

    public OrderByAppend appendAsc(String column) {
        return append(column, null);
    }


    public OrderByAppend appendDesc(C column) {
        return append(((CInterface) column).aliasDelimitedName(), DESC);
    }

    public OrderByAppend appendDesc(String column) {
        return append(column, DESC);
    }

    public String toOrderByString() {
        if (map.size() > 0) {
            List<String> list = new ArrayList<>();
            map.forEach((key, value) -> {
                if (value != null) {
                    list.add(key + " " + value);
                } else {
                    list.add(key);
                }
            });
            return String.join(",", list);
        }
        return null;
    }
}
