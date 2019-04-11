package mboog.support.mapper;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @author LiYi
 */
public interface BaseMapper<PrimaryKey, Model, Example> {

    default String getMapperName() {
        return this.getClass().getGenericInterfaces()[0].getTypeName();
    }

}
