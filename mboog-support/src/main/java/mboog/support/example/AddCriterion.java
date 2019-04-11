package mboog.support.example;

import java.util.Date;
import java.util.List;

/**
 * @author LiYi
 */
public class AddCriterion {

    private AbstractGeneratedCriteria<?> mbgGeneratedCriteria;

    public AddCriterion(AbstractGeneratedCriteria<?> mbgGeneratedCriteria) {
        super();
        this.mbgGeneratedCriteria = mbgGeneratedCriteria;
    }

    public AddCriterion addCriterion(String condition) {
        mbgGeneratedCriteria.addCriterion(condition);
        return this;
    }

    public AddCriterion addCriterion(String condition, Object value) {
        mbgGeneratedCriteria.addCriterion(condition, value);
        return this;
    }

    public AddCriterion addCriterion(String condition, Object value, String property) {
        mbgGeneratedCriteria.addCriterion(condition, value, property);
        return this;
    }

    public AddCriterion addCriterion(String condition, Object value1, Object value2, String property) {
        mbgGeneratedCriteria.addCriterion(condition, value1, value2, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCDate(String condition, Date value, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCDate(condition, value, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCDate(String condition, List<Date> values, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCDate(condition, values, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCDate(condition, value1, value2, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCTime(String condition, Date value, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCTime(condition, value, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCTime(String condition, List<Date> values, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCTime(condition, values, property);
        return this;
    }

    public AddCriterion addCriterionForJDBCTime(String condition, Date value1, Date value2, String property) {
        mbgGeneratedCriteria.addCriterionForJDBCTime(condition, value1, value2, property);
        return this;
    }

}
