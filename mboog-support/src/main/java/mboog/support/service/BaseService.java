package mboog.support.service;

import mboog.support.mapper.BaseMapper;
import mboog.support.mapper.ReadMapper;
import mboog.support.mapper.UpsertMapper;
import mboog.support.mapper.WriteMapper;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @param <T>          MbgMapper
 * @author LiYi
 */
public interface BaseService<PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>> {

    <M extends T> M mapper();

    static class S {
        protected static <PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>> ReadMapper<PrimaryKey, Model, Example> readMapper(
                BaseService<PrimaryKey, Model, Example, T> mbgService) {
            return mbgService.mapper();
        }

        protected static <PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>> WriteMapper<PrimaryKey, Model, Example> writeMapper(
                BaseService<PrimaryKey, Model, Example, T> mbgService) {
            return mbgService.mapper();
        }

        protected static <PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>> UpsertMapper<PrimaryKey, Model, Example> upsertMapper(
                BaseService<PrimaryKey, Model, Example, T> mbgService) {
            return mbgService.mapper();
        }

    }

}
