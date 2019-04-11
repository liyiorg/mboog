package mboog.support.mapper;

import mboog.support.bean.Page;
import mboog.support.example.PaginationAble;
import mboog.support.exceptions.ExampleException;
import mboog.support.exceptions.MapperException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @param <PrimaryKey> PrimaryKey
 * @param <Model>      Model
 * @param <Example>    Example
 * @author LiYi
 */
public interface ReadMapper<PrimaryKey, Model, Example>
        extends BaseMapper<PrimaryKey, Model, Example> {

    long countByExample(Example example);

    List<Model> selectByExample(Example example);

    default Model selectByExampleSingleResult(Example example) {
        List<Model> results = selectByExample(example);
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new MapperException("data results must single result");
        }
        return results.iterator().next();
    }

    Model selectByPrimaryKey(PrimaryKey id);

    default List<Model> selectByExample(Example example, long page, long size) {
        if (Objects.isNull(example)) {
            throw new ExampleException("Example can't null");
        }
        if (example instanceof PaginationAble) {
            PaginationAble temp = (PaginationAble) example;
            if ("Oracle".equalsIgnoreCase(temp.getDatabaseType())) {
                temp.setLimitStart((page - 1) * size);
                temp.setLimitEnd(page * size);
            } else {
                temp.setLimitStart((page - 1) * size);
                temp.setLimitEnd(size);
            }

            return selectByExample(example);
        } else {
            throw new ExampleException("Example must PaginationAble");
        }

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
        if (Objects.isNull(example)) {
            throw new ExampleException("Example can't null");
        }
        if (example instanceof PaginationAble) {
            PaginationAble temp = (PaginationAble) example;
            String orderByClause = temp.getOrderByClause();
            temp.setOrderByClause(null);
            long count = countByExample(example);
            List<Model> list = null;
            if (count > 0) {
                temp.setOrderByClause(orderByClause);
                list = selectByExample(example, page, size);
            } else {
                list = Collections.emptyList();
            }
            return new Page<Model>(list, count, page, size);
        } else {
            throw new ExampleException("Example must PaginationAble");
        }

    }

}
