package mboog.support.example;

/**
 * @author LiYi
 */
public class CItem {

    private static final String D = ".";

    private int type;

    private int jdbcType;

    private boolean delimited;

    private String columnName;

    private String alias;

    private String beginningDelimiter;

    private String endingDelimiter;

    /**
     * @param type               types [1,2,3]
     * @param jdbcType           jdbcType
     * @param delimited          delimited
     * @param columnName         columnName
     * @param alias              alias
     * @param beginningDelimiter beginningDelimiter [",`, others]
     * @param endingDelimiter    endingDelimiter [",`, others]
     */
    public CItem(int type, int jdbcType, boolean delimited, String columnName, String alias, String beginningDelimiter,
                 String endingDelimiter) {
        this.type = type;
        this.jdbcType = jdbcType;
        this.delimited = delimited;
        this.columnName = columnName;
        this.alias = alias;
        this.beginningDelimiter = beginningDelimiter;
        this.endingDelimiter = endingDelimiter;
    }

    int getType() {
        return type;
    }

    int getJdbcType() {
        return jdbcType;
    }

    boolean isDelimited() {
        return delimited;
    }

    String getColumnName() {
        return columnName;
    }

    /**
     * 获取 别名 + 转义符包裹列名 + AS 名<br>
     * 格式：<br>
     *     aliasName.`columnName` as `aliasName_columnName`
     * @return
     */
    String includeColumnName() {
        if (alias == null) {
            return delimitedName();
        } else {
            if (delimited) {
                return String.format("%s as %s%s_%s%s", aliasDelimitedName(), beginningDelimiter, alias, columnName, endingDelimiter);
            } else {
                return String.format("%s as %s_%s", aliasDelimitedName(), alias, columnName);
            }
        }
    }

    /**
     * 获取 转义符包裹列名
     * @return
     */
    String delimitedName() {
        if (delimited) {
            return beginningDelimiter + columnName + endingDelimiter;
        }
        return columnName;
    }

    /**
     * 获取 别名 + 转义符包裹列名
     * @return
     */
    String aliasDelimitedName() {
        if (alias == null) {
            return delimitedName();
        }
        return alias + D + delimitedName();
    }
}
