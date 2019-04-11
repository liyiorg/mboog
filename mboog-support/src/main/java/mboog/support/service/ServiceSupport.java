package mboog.support.service;

import mboog.support.mapper.BaseMapper;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @param <T>          T
 * @author LiYi
 */
public abstract class ServiceSupport<PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>>
        implements BaseService<PrimaryKey, Model, Example, T> {

    protected T mapper;


    @SuppressWarnings("unchecked")
    @Override
    public <Mapper extends T> Mapper mapper() {
        return (Mapper) mapper;
    }


    public T getMapper() {
        return mapper;
    }

    public void setMapper(T mapper) {
        this.mapper = mapper;
    }


}
