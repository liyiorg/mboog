package mboog.support.example;

import mboog.support.util.CUtil;

import java.util.List;
import java.util.function.Supplier;

/**
 * @param <M>
 * @param <E>
 * @param <C>
 * @author LiYi
 */
@SuppressWarnings("unchecked")
public interface ColumnListAble<M, E extends Enum<E>, C extends CInterface> extends ExampleData {

    /**
     * 设置列
     *
     * @param columnList columnList
     */
    default void setColumnList(String columnList) {
        dataSet(ExampleConstants.COLUMN_LIST, columnList);
    }

    /**
     * 获取列数据
     *
     * @return 列数据
     */
    default String getColumnList() {
        return dataGet(ExampleConstants.COLUMN_LIST);
    }

    /**
     * 初始化
     *
     * @param clazz clazz
     */
    default void initColumnList(Class<E> clazz) {
        if (!CEnumData.existColumnListable(this.getClass())) {
            CEnumData.putColumnListable(this.getClass(), clazz);
        }
    }

    /**
     * select
     *
     * @param select 列
     * @return M
     */
    default M select(String... select) {
        setColumnList(String.join(",", select));
        return (M) this;
    }

    /**
     * 包含列
     *
     * @param cs cs
     * @return M
     */
    default M includeColumns(C... cs) {
        CUtil.includeColumns(CEnumData.getColumnListable(this.getClass()), this, cs);
        return (M) this;
    }

    /**
     * 包含列
     *
     * @param supplier supplier
     * @return M
     */
    default M includeColumns(Supplier<List<C>> supplier) {
        CUtil.includeColumns(CEnumData.getColumnListable(this.getClass()), this, supplier.get());
        return (M) this;
    }

    /**
     * 排除列
     *
     * @param cs cs
     * @return M
     */
    default M excludeColumns(C... cs) {
        CUtil.excludeColumns(CEnumData.getColumnListable(this.getClass()), this, cs);
        return (M) this;
    }

    /**
     * 排除列
     *
     * @param supplier supplier
     * @return M
     */
    default M excludeColumns(Supplier<List<C>> supplier) {
        CUtil.excludeColumns(CEnumData.getColumnListable(this.getClass()), this, supplier.get());
        return (M) this;
    }

}
