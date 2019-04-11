package mboog.support.example;

import mboog.support.util.CUtil;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface ColumnListAble<M, E extends Enum<E>, C extends CInterface> extends ExampleData {

    default void setColumnList(String columnList) {
        dataSet(ExampleConstants.COLUMN_LIST, columnList);
    }

    default String getColumnList() {
        return dataGet(ExampleConstants.COLUMN_LIST);
    }

    default void initColumnList(Class<E> clazz) {
        if (!CEnumData.existColumnListable(this.getClass())) {
            CEnumData.putColumnListable(this.getClass(), clazz);
        }
    }

    default M select(String... select) {
        setColumnList(String.join(",", select));
        return (M) this;
    }

    default M includeColumns(C... cs) {
        CUtil.includeColumns(CEnumData.getColumnListable(this.getClass()), this, cs);
        return (M) this;
    }

    default M includeColumns(Supplier<List<C>> supplier) {
        CUtil.includeColumns(CEnumData.getColumnListable(this.getClass()), this, supplier.get());
        return (M) this;
    }

    default M excludeColumns(C... cs) {
        CUtil.excludeColumns(CEnumData.getColumnListable(this.getClass()), this, cs);
        return (M) this;
    }

    default M excludeColumns(Supplier<List<C>> supplier) {
        CUtil.excludeColumns(CEnumData.getColumnListable(this.getClass()), this, supplier.get());
        return (M) this;
    }

}
