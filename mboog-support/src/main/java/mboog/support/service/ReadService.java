package mboog.support.service;

import mboog.support.bean.Page;
import mboog.support.example.CInterface;
import mboog.support.mapper.BaseMapper;
import mboog.support.mapper.MapperMethodConstants;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @param <T>          MbgMapper
 * @author LiYi
 */
public interface ReadService<PrimaryKey, Model, Example, T extends BaseMapper<PrimaryKey, Model, Example>>
        extends BaseService<PrimaryKey, Model, Example, T> {

    /**
     * 查询计数
     *
     * @param example Example
     * @return 数量
     */
    default long countByExample(Example example) {
        return S.readMapper(this).countByExample(example);
    }

    /**
     * 条件查询
     *
     * @param example Example
     * @return Models
     */
    default List<Model> selectByExample(Example example) {
        return S.readMapper(this).selectByExample(example);
    }

    /**
     * 条件查询
     *
     * @param example Example
     * @return Model
     */
    default Model selectByExampleSingleResult(Example example) {
        return S.readMapper(this).selectByExampleSingleResult(example);
    }

    /**
     * 主键查询
     *
     * @param id PrimaryKey
     * @return Model
     */
    default Model selectByPrimaryKey(PrimaryKey id) {
        return S.readMapper(this).selectByPrimaryKey(id);
    }

    /**
     * 条件查询 分页
     *
     * @param example Example
     * @param page    页码
     * @param size    数量
     * @return Models
     */
    default List<Model> selectByExample(Example example, long page, long size) {
        return S.readMapper(this).selectByExample(example, page, size);
    }

    /**
     * Pagination selectByExample
     *
     * @param example NOT NULL
     * @param page    page_no
     * @param size    page_size
     * @return Page
     */
    default Page<Model> selectPageByExample(Example example, long page, long size) {
        return S.readMapper(this).selectPageByExample(example, page, size);
    }

    /**
     * 主键查询 指定列
     *
     * @param primaryKey PrimaryKey
     * @param include    包含或排除
     * @param cs         列
     * @param <C>        C
     * @return Model
     */
    default <C extends CInterface> Model selectByPrimaryKeyWithColumns(PrimaryKey primaryKey, boolean include, C... cs) {
        try {
            Class clazz = Class.forName(mapper().getMapperName());
            Method method = clazz.getMethod(MapperMethodConstants.SELECT_BY_PRIMARY_KEY_WITH_COLUMNS, primaryKey.getClass(), boolean.class, cs.getClass());
            return (Model) method.invoke(mapper(), primaryKey, include, cs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
