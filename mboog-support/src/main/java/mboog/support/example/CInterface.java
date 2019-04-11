package mboog.support.example;

/**
 * @author LiYi
 */
public interface CInterface {

    String name();

    default void init(CItem cItem) {
        CEnumData.putCItem(this, cItem);
    }

    default int getType() {
        return CEnumData.getCItem(this).getType();
    }

    default int getJdbcType() {
        return CEnumData.getCItem(this).getJdbcType();
    }

    default boolean isDelimited() {
        return CEnumData.getCItem(this).isDelimited();
    }

    default String getColumnName() {
        return CEnumData.getCItem(this).getColumnName();
    }

    default String includeColumnName() {
        return CEnumData.getCItem(this).includeColumnName();
    }

    default String delimitedName() {
        return CEnumData.getCItem(this).delimitedName();
    }

    default String aliasDelimitedName() {
        return CEnumData.getCItem(this).aliasDelimitedName();
    }

}
