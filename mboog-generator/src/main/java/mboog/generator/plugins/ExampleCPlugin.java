package mboog.generator.plugins;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerEnum;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * ExampleCPlugin Add enum C<br>
 *
 * @author LiYi
 */
public class ExampleCPlugin extends PluginAdapter {

    private static final String C_INTERFACE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.CInterface";

    private static final String C_ITEM_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.CItem";

    private static final String REMARKS_PROPERTY_NAME = "remarks";

    private static String DEFAULT_REMARKS;

    private String remarks;


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        DEFAULT_REMARKS = properties.getProperty(REMARKS_PROPERTY_NAME, "1");
        String remarks_pro = introspectedTable.getTableConfiguration().getProperty(REMARKS_PROPERTY_NAME);
        if (StringUtility.stringHasValue(remarks_pro)) {
            remarks = remarks_pro;
        } else {
            remarks = DEFAULT_REMARKS;
        }

    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //添加import
        topLevelClass.addImportedType(C_INTERFACE_CLASS);
        topLevelClass.addImportedType(C_ITEM_CLASS);
        topLevelClass.addImportedType("java.sql.Types");

        //添加内部枚举C
        addInnerEnum_C(topLevelClass, introspectedTable);
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 添加列枚举
     *
     * @param topLevelClass     topLevelClass
     * @param introspectedTable introspectedTable
     */
    private void addInnerEnum_C(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        InnerEnum innerEnum_C = new InnerEnum(new FullyQualifiedJavaType("C"));
        innerEnum_C.setVisibility(JavaVisibility.PUBLIC);
        innerEnum_C.setStatic(true);
        innerEnum_C.addSuperInterface(new FullyQualifiedJavaType(C_INTERFACE_CLASS));

        String useActualColumnNames = introspectedTable.getTableConfiguration().getProperty("useActualColumnNames");
        boolean enumUseActualColumnName = StringUtility.isTrue(useActualColumnNames);

        //添加构造项
        Map<String, String> constantMap = new LinkedHashMap<>();
        buildEnumConstant(constantMap, introspectedTable.getPrimaryKeyColumns(), 1, enumUseActualColumnName);
        buildEnumConstant(constantMap, introspectedTable.getBaseColumns(), 2, enumUseActualColumnName);
        buildEnumConstant(constantMap, introspectedTable.getBLOBColumns(), 3, enumUseActualColumnName);

        StringBuilder stringBuilder = new StringBuilder();
        // 添加表注释
        if (!"0".equals(remarks)) {
            stringBuilder.append("//--------------------------------------------------")
                    .append(System.lineSeparator())
                    .append("\t\t//[").append(introspectedTable.getTableType()).append("]");
            if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
                stringBuilder.append("\t").append(introspectedTable.getRemarks());
            }
            stringBuilder.append(System.lineSeparator())
                    .append("\t\t//--------------------------------------------------");
        }

        switch (remarks) {
            case "0":
                BuildRemarks.type_0(innerEnum_C, stringBuilder, constantMap);
                break;
            case "2":
                BuildRemarks.type_2(innerEnum_C, stringBuilder, constantMap);
                break;
            case "3":
                BuildRemarks.type_3(innerEnum_C, stringBuilder, constantMap);
                break;
            default:
                BuildRemarks.type_1(innerEnum_C, stringBuilder, constantMap);
        }

        String tableAlias = introspectedTable.getTableConfiguration().getAlias();
        String tableAliasStr = StringUtility.stringHasValue(tableAlias) ? "\"" + tableAlias + "\"" : "null";
        String beginningDelimiter = context.getBeginningDelimiter().replaceAll("\"", "\\\\\"");
        String endingDelimiter = context.getEndingDelimiter().replaceAll("\"", "\\\\\"");

        Method method_C = new Method("C");
        method_C.setConstructor(true);
        method_C.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "types"));
        method_C.addParameter(new Parameter(new FullyQualifiedJavaType("int"), "jdbcType"));
        method_C.addParameter(new Parameter(new FullyQualifiedJavaType("boolean"), "delimited"));
        method_C.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "columnName"));

        method_C.addBodyLine("init(new CItem(types, jdbcType, delimited, columnName, "
                + tableAliasStr + " , \"" + beginningDelimiter + "\", \"" + endingDelimiter + "\"));");
        innerEnum_C.addMethod(method_C);

        context.getCommentGenerator().addEnumComment(innerEnum_C, introspectedTable);
        topLevelClass.addInnerEnum(innerEnum_C);
    }


    /**
     * 生成构造项
     *
     * @param map                     map
     * @param introspectedColumns     introspectedColumns
     * @param type                    [1,2,3]
     * @param enumUseActualColumnName enumUseActualColumnName
     */
    private void buildEnumConstant(Map<String, String> map, List<IntrospectedColumn> introspectedColumns, int type, boolean enumUseActualColumnName) {
        for (IntrospectedColumn column : introspectedColumns) {
            String columnRemark = null;
            if (StringUtility.stringHasValue(column.getRemarks())) {
                columnRemark = column.getRemarks();
            }

            if (StringUtility.stringHasValue(column.getDefaultValue())) {
                if (columnRemark == null) {
                    columnRemark = "DV[" + column.getDefaultValue() + "]";
                } else {
                    columnRemark += " DV[" + column.getDefaultValue() + "]";
                }
            }
            String enumName = enumUseActualColumnName ? column.getActualColumnName() : column.getActualColumnName().toUpperCase();
            String key = String.format("%s(%d, Types.%s, %b, \"%s\")", enumName, type,
                    column.getJdbcTypeName(), column.isColumnNameDelimited(), column.getActualColumnName());
            map.put(key, columnRemark);
        }
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    private static class BuildRemarks {

        private static final int MAX_TABS_LENGTH = 10;
        private static final int APPEND_T = 2;


        /**
         * 计算格式化输出注释的最大key tab length
         *
         * @param keys keys
         * @param a    补位长度
         * @return maxTabs
         */
        private static int maxTabs(Object[] keys, int a) {
            int maxKeyLength = 0;
            for (Object key : keys) {
                if (key != null) {
                    //中文字符替换
                    String keystr = key.toString().replaceAll("[^\\x00-\\xff]", "##");
                    if (keystr.length() > maxKeyLength) {
                        maxKeyLength = keystr.length();
                    }
                }
            }
            return (maxKeyLength + a) / 4 + ((maxKeyLength + a) % 4 == 0 ? 0 : 1);
        }

        /**
         * 计算格式化输出注释的预留空白
         *
         * @param key     key
         * @param maxTabs maxTabs
         * @param appendT \t 补长
         * @param a       字符串补位长度
         * @return \t ...
         */
        private static String buildTabs(String key, int maxTabs, int appendT, int a) {
            key = key.replaceAll("[^\\x00-\\xff]", "##");
            int keyLength = key.length();
            int r = (keyLength + a) / 4;
            int l = maxTabs - r + appendT;
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < l; i++) {
                stringBuilder.append("\t");
            }
            return stringBuilder.toString();
        }

        /**
         * 无备注输出
         *
         * @param innerEnum_C   innerEnum_C
         * @param stringBuilder stringBuilder
         * @param constantMap   constantMap
         */
        static void type_0(InnerEnum innerEnum_C, StringBuilder stringBuilder,
                           Map<String, String> constantMap) {
            Iterator<String> iterator = constantMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                stringBuilder.append(System.lineSeparator()).append("\t\t");
                stringBuilder.append(key);
                if (iterator.hasNext()) {
                    stringBuilder.append(",");
                }
            }
            innerEnum_C.addEnumConstant(stringBuilder.toString());
        }

        /**
         * 备注输出 <br>
         * 同行输出 <br>
         * 格式 <br>
         * 备注 \t 构造
         *
         * @param innerEnum_C   innerEnum_C
         * @param stringBuilder stringBuilder
         * @param constantMap   constantMap
         */
        static void type_1(InnerEnum innerEnum_C, StringBuilder stringBuilder,
                           Map<String, String> constantMap) {
            try {
                int maxTabs = maxTabs(constantMap.values().toArray(), 7);
                int maxBuildTabs = maxTabs > MAX_TABS_LENGTH ? MAX_TABS_LENGTH : maxTabs;
                Iterator<String> iterator = constantMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String columnRemark = constantMap.get(key);
                    boolean hasRemark = StringUtility.stringHasValue(columnRemark);
                    String rm = "\t\t\t";
                    if (hasRemark) {
                        stringBuilder.append(System.lineSeparator());
                        rm = "/** " + columnRemark + " */";
                    }
                    stringBuilder.append("\t\t");
                    String brm = "";
                    if (hasRemark && maxTabs(new Object[]{columnRemark}, 7) <= MAX_TABS_LENGTH + APPEND_T) {
                        brm = columnRemark;
                    } else {
                        rm += System.lineSeparator() + "\t\t\t";
                    }
                    stringBuilder.append(rm).append(buildTabs(brm, maxBuildTabs, APPEND_T, 7)).append(key);
                    if (iterator.hasNext()) {
                        stringBuilder.append(",");
                    }
                }
                innerEnum_C.addEnumConstant(stringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 备注输出 <br>
         * 平行输出 <br>
         * 格式 <br>
         * 备注 <br>
         * 构造
         *
         * @param innerEnum_C   innerEnum_C
         * @param stringBuilder stringBuilder
         * @param constantMap   constantMap
         */
        static void type_2(InnerEnum innerEnum_C, StringBuilder stringBuilder,
                           Map<String, String> constantMap) {
            Iterator<String> iterator = constantMap.keySet().iterator();
            boolean first = true;
            while (iterator.hasNext()) {
                String key = iterator.next();
                String constant_str;
                if (StringUtility.stringHasValue(constantMap.get(key))) {
                    constant_str = "/** " + constantMap.get(key) + " */" + System.lineSeparator() + "\t\t" + key;
                } else {
                    constant_str = key;
                }
                if (first) {
                    constant_str = stringBuilder.toString() + System.lineSeparator() + "\t\t" + constant_str;
                    first = false;
                }
                innerEnum_C.addEnumConstant(constant_str);
            }
        }

        /**
         * 备注输出 <br>
         * 同行输出 <br>
         * 格式 <br>
         * 构造 \t 备注
         *
         * @param innerEnum_C   innerEnum_C
         * @param stringBuilder stringBuilder
         * @param constantMap   constantMap
         */
        static void type_3(InnerEnum innerEnum_C, StringBuilder stringBuilder,
                           Map<String, String> constantMap) {
            int maxTabs = maxTabs(constantMap.keySet().toArray(), 1);
            Iterator<String> iterator = constantMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String columnRemark = constantMap.get(key);
                stringBuilder.append(System.lineSeparator()).append("\t\t");
                stringBuilder.append(key);
                if (iterator.hasNext()) {
                    stringBuilder.append(",");
                } else if (columnRemark != null) {
                    stringBuilder.append(";");
                }

                if (columnRemark != null) {
                    stringBuilder.append(buildTabs(key, maxTabs, APPEND_T, 1)).append("//").append(columnRemark);
                }
            }
            innerEnum_C.addEnumConstant(stringBuilder.toString());
        }
    }

}
