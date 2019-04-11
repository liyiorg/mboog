package mboog.support.service;

import mboog.support.mapper.BaseMapper;

import java.util.List;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @param <T>          MbgMapper
 * @author LiYi
 */
public interface WriteService<PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>>
        extends BaseService<PrimaryKey, Model, Example, T> {

    default int deleteByExample(Example example) {
        return S.writeMapper(this).deleteByExample(example);
    }

    default int deleteByPrimaryKey(PrimaryKey id) {
        return S.writeMapper(this).deleteByPrimaryKey(id);
    }

    default int insert(Model record) {
        return S.writeMapper(this).insert(record);
    }

    default int batchInsert(List<Model> records) {
        if (records == null || records.size() == 0) {
            return 0;
        }
        return S.writeMapper(this).batchInsert(records);
    }

    default int insertSelective(Model record) {
        return S.writeMapper(this).insertSelective(record);
    }

    default int batchInsertSelective(List<Model> records) {
        if (records == null || records.size() == 0) {
            return 0;
        }
        return S.writeMapper(this).batchInsertSelective(records);
    }

    default int updateByExampleSelective(Model record, Example example) {
        return S.writeMapper(this).updateByExampleSelective(record, example);
    }

    default int updateByExample(Model record, Example example) {
        return S.writeMapper(this).updateByExample(record, example);
    }

    default int updateByPrimaryKeySelective(Model record) {
        return S.writeMapper(this).updateByPrimaryKeySelective(record);
    }

    default int updateByPrimaryKey(Model record) {
        return S.writeMapper(this).updateByPrimaryKey(record);
    }

    default int updateByPrimaryKeyWithOptimisticLock(Model record) {
        return S.writeMapper(this).updateByPrimaryKeyWithOptimisticLock(record);
    }

    default int updateByPrimaryKeySelectiveWithOptimisticLock(Model record) {
        return S.writeMapper(this).updateByPrimaryKeySelectiveWithOptimisticLock(record);
    }
}
