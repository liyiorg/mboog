package mboog.support.mapper;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @author LiYi
 */
public interface UpsertMapper<PrimaryKey, Model, Example>
        extends BaseMapper<PrimaryKey, Model, Example> {

    int upsert(Model record);

    int upsertSelective(Model record);

}
