package mboog.support.example;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author LiYi
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGeneratedCriteria<C> {

    protected List<Criterion> criteria;

    private boolean ignoreNull;

    private boolean ignoreEmpty;

    protected static final String PREFIX_OR = "or";

    protected static final String PREFIX_AND = "and";

    protected String prefix;

    protected String prefixInner;

    /**
     * Ignore value if it's null or empty.
     */
    public C ignoreNull() {
        return ignoreNull(true);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignore ignore
     * @return this
     */
    public C ignoreNull(boolean ignore) {
        return ignoreNull(ignore, ignore);
    }

    /**
     * Ignore value if it's null or empty.
     *
     * @param ignoreNull  value is null
     * @param ignoreEmpty value is empty
     * @return this
     */
    public C ignoreNull(boolean ignoreNull, boolean ignoreEmpty) {
        this.ignoreNull = ignoreNull;
        this.ignoreEmpty = ignoreEmpty;
        return (C) this;
    }


    public C reversePrefix() {
        prefixInner = PREFIX_OR.equals(prefixInner) ? PREFIX_AND : PREFIX_OR;
        return (C) this;
    }

    protected AbstractGeneratedCriteria() {
        super();
        this.criteria = new ArrayList<Criterion>();
    }

    public boolean isValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public String getPrefix() {
        return prefix;
    }

    protected void addCriterion(String condition) {
        if (condition == null) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criteria.add(new Criterion(condition).prefix(prefixInner));
    }

    protected void addCriterion(String condition, Object value) {
        addCriterion(condition, value, condition);
    }

    protected void addCriterion(String condition, Object value, String property) {
        if (value == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value for " + property + " cannot be null");
        }

        if (value instanceof String && value.toString().trim().equals("")) {
            if (ignoreEmpty) {
                return;
            }
        } else if (value instanceof Collection && ((Collection) value).isEmpty()) {
            if (ignoreEmpty) {
                return;
            }
            throw new RuntimeException("Value for " + property + " cannot be empty");
        }

        criteria.add(new Criterion(condition, value).prefix(prefixInner));
    }

    /**
     * @param condition
     * @param pattern   .*{}.*
     * @param value
     * @param property
     */
    protected void addCriterionPattern(String condition, String pattern, String value, String property) {
        if (pattern == null) {
            throw new RuntimeException("Format for " + property + " cannot be null");
        }
        if (!pattern.matches(".*\\{\\}.*")) {
            throw new RuntimeException("Format for " + property + " pattern error,  PATTERN: " + pattern);
        }
        if (value == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        if (ignoreEmpty && value.trim().equals("")) {
            return;
        }
        String formatValue = pattern.replace("{}", value);
        criteria.add(new Criterion(condition, formatValue).prefix(prefixInner));
    }

    protected void addCriterion(String condition, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        if (ignoreEmpty) {
            if (value1 instanceof String && value1.toString().trim().equals("")) {
                return;
            }
            if (value2 instanceof String && value2.toString().trim().equals("")) {
                return;
            }
        }
        criteria.add(new Criterion(condition, value1, value2).prefix(prefixInner));
    }

    protected void addCriterionForJDBCDate(String condition, Date value, String property) {
        if (value == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value for " + property + " cannot be null");
        }

        addCriterion(condition, new java.sql.Date(value.getTime()), property);
    }

    protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
        if (values == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value list for " + property + " cannot be null");
        }
        if (values.isEmpty()) {
            if (ignoreEmpty) {
                return;
            }
            throw new RuntimeException("Value list for " + property + " cannot be empty");
        }
        List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
        Iterator<Date> iter = values.iterator();
        while (iter.hasNext()) {
            dateList.add(new java.sql.Date(iter.next().getTime()));
        }
        addCriterion(condition, dateList, property);
    }

    protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
        if (value1 == null || value2 == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
    }

    protected void addCriterionForJDBCTime(String condition, Date value, String property) {
        if (value == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        addCriterion(condition, new java.sql.Time(value.getTime()), property);
    }

    protected void addCriterionForJDBCTime(String condition, List<Date> values, String property) {
        if (values == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Value list for " + property + " cannot be null");
        }
        if (values.isEmpty()) {
            if (ignoreEmpty) {
                return;
            }
            throw new RuntimeException("Value list for " + property + " cannot be empty");
        }

        List<java.sql.Time> timeList = new ArrayList<java.sql.Time>();
        Iterator<Date> iter = values.iterator();
        while (iter.hasNext()) {
            timeList.add(new java.sql.Time(iter.next().getTime()));
        }
        addCriterion(condition, timeList, property);
    }

    protected void addCriterionForJDBCTime(String condition, Date value1, Date value2, String property) {
        if (value1 == null || value2 == null) {
            if (ignoreNull) {
                return;
            }
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        addCriterion(condition, new java.sql.Time(value1.getTime()), new java.sql.Time(value2.getTime()), property);
    }

    /**
     * 添加自定义条件
     *
     * @param consumer
     * @return
     */
    public C addCriterions(Consumer<AddCriterion> consumer) {
        consumer.accept(new AddCriterion(this));
        return (C) this;
    }

}
