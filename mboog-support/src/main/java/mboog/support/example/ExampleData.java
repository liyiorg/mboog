package mboog.support.example;

/**
 * @author LiYi
 */
public interface ExampleData {

    <D> void dataSet(String key, D t);

    <D> D dataGet(String key);
}
