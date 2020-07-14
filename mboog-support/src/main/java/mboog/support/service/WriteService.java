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

    /**
     * 条件删除
     *
     * @param example Example
     * @return 执行数
     */
    default int deleteByExample(Example example) {
        return S.writeMapper(this).deleteByExample(example);
    }

    /**
     * 主键删除
     *
     * @param id
     * @return 执行数
     */
    default int deleteByPrimaryKey(PrimaryKey id) {
        return S.writeMapper(this).deleteByPrimaryKey(id);
    }

    /**
     * 添加
     *
     * @param record Model
     * @return 执行数
     */
    default int insert(Model record) {
        return S.writeMapper(this).insert(record);
    }

    /**
     * 批量添加
     *
     * @param records Models
     * @return 执行数
     */
    default int batchInsert(List<Model> records) {
        if (records == null || records.size() == 0) {
            return 0;
        }
        return S.writeMapper(this).batchInsert(records);
    }

    /**
     * 添加 （非空字段）
     *
     * @param record Model
     * @return 执行数
     */
    default int insertSelective(Model record) {
        return S.writeMapper(this).insertSelective(record);
    }

    /**
     * 批量添加 （非空字段）
     *
     * @param records Models
     * @return 执行数
     */
    default int batchInsertSelective(List<Model> records) {
        if (records == null || records.size() == 0) {
            return 0;
        }
        return S.writeMapper(this).batchInsertSelective(records);
    }

    /**
     * 条件更新 （非空字段）
     *
     * @param record  Model
     * @param example Example
     * @return 执行数
     */
    default int updateByExampleSelective(Model record, Example example) {
        return S.writeMapper(this).updateByExampleSelective(record, example);
    }

    /**
     * 条件更新
     *
     * @param record  Model
     * @param example Example
     * @return 执行数
     */
    default int updateByExample(Model record, Example example) {
        return S.writeMapper(this).updateByExample(record, example);
    }

    /**
     * 主键更新
     *
     * @param record Model
     * @return 执行数
     */
    default int updateByPrimaryKeySelective(Model record) {
        return S.writeMapper(this).updateByPrimaryKeySelective(record);
    }

    /**
     * 主键更新 （非空字段）
     *
     * @param record Model
     * @return 执行数
     */
    default int updateByPrimaryKey(Model record) {
        return S.writeMapper(this).updateByPrimaryKey(record);
    }

    /**
     * 主键更新 乐观锁
     *
     * @param record Model
     * @return 执行数
     */
    default int updateByPrimaryKeyWithOptimisticLock(Model record) {
        return S.writeMapper(this).updateByPrimaryKeyWithOptimisticLock(record);
    }

    /**
     * 主键更新 乐观锁 （非空字段）
     *
     * @param record Model
     * @return 执行数
     */
    default int updateByPrimaryKeySelectiveWithOptimisticLock(Model record) {
        return S.writeMapper(this).updateByPrimaryKeySelectiveWithOptimisticLock(record);
    }
}
