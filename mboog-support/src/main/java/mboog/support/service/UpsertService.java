package mboog.support.service;

import mboog.support.mapper.BaseMapper;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @param <T>          MbgMapper
 * @author LiYi
 */
public interface UpsertService<PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>>
        extends BaseService<PrimaryKey, Model, Example, T> {

    /**
     * 保存或更新（全量）
     *
     * @param record Model
     * @return 更新记录数
     */
    default int upsert(Model record) {
        return S.upsertMapper(this).upsert(record);
    }

    /**
     * 保存或更新（非空值）
     *
     * @param record Model
     * @return 更新记录数
     */
    default int upsertSelective(Model record) {
        return S.upsertMapper(this).upsertSelective(record);
    }

}
