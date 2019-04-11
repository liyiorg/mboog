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

    default int upsert(Model record) {
        return S.upsertMapper(this).upsert(record);
    }

    default int upsertSelective(Model record) {
        return S.upsertMapper(this).upsertSelective(record);
    }

}
