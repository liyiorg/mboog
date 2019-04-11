package mboog.support.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @author LiYi
 */
public interface WriteMapper<PrimaryKey, Model, Example>
        extends BaseMapper<PrimaryKey, Model, Example> {

    int deleteByExample(Example example);

    int deleteByPrimaryKey(PrimaryKey id);

    int insert(Model record);

    int insertSelective(Model record);

    int updateByExampleSelective(@Param("record") Model record, @Param("example") Example example);

    int updateByExample(@Param("record") Model record, @Param("example") Example example);

    int updateByPrimaryKeySelective(Model record);

    int updateByPrimaryKey(Model record);

    int batchInsert(List<Model> records);

    int batchInsertSelective(List<Model> records);

    int updateByPrimaryKeyWithOptimisticLock(Model record);

    int updateByPrimaryKeySelectiveWithOptimisticLock(Model record);
}
