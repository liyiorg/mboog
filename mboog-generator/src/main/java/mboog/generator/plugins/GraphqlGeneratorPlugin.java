package mboog.generator.plugins;

import mboog.generator.util.MBGFileUtil;
import mboog.generator.util.MBGStringUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.io.File;
import java.util.*;

/**
 * 生成Graphql type
 * <p>
 * table property <br>
 * targetPackage 存放目录
 * graphql true|false default true 生成graphql_type file <br>
 * graphqlIgnores 忽略的columns 多个用,号分割
 *
 * @author LiYi
 */
public class GraphqlGeneratorPlugin extends PluginAdapter {

    private static Log log = LogFactory.getLog(GraphqlGeneratorPlugin.class);

    private String targetPackage;

    private static final String TARGETPACKAGE_PROPERTY_NAME = "targetPackage";

    private static final String GRAPHQL_PROPERTY_NAME = "graphql";

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        //set targetPackage
        targetPackage = properties.getOrDefault(TARGETPACKAGE_PROPERTY_NAME, "graphqls").toString();
        super.initialized(introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        String graphql_pro = introspectedTable.getTableConfiguration().getProperty(GRAPHQL_PROPERTY_NAME);
        String baseRecordType = introspectedTable.getBaseRecordType();
        String graphqlFilePath = String.format("%s/TYPE_%s.graphqls",
                targetPackage.replace(".", "/"),
                MBGStringUtil.shortClassName(baseRecordType));
        File graphqlFile = MBGFileUtil.getResourcesFile(graphqlFilePath);
        if ("false".equals(graphql_pro)) {
            if (graphqlFile.exists()) {
                // 删除文件
                graphqlFile.delete();
            }
        } else {
            Set<String> ignores = new HashSet<>();
            if (graphql_pro != null && !StringUtility.isTrue(graphql_pro)) {
                for (String ignore : graphql_pro.split(",")) {
                    ignores.add(ignore);
                }
            }
            String graphqlTypeCode = builderGraphqlTypeCode(introspectedTable, ignores);
            MBGFileUtil.createFile(graphqlFile, graphqlTypeCode);
            log.debug("Generated file is saved as " + graphqlFile.getAbsolutePath());


        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 生成 graphql type code
     *
     * @param introspectedTable
     * @param ignores
     * @return
     */
    private String builderGraphqlTypeCode(IntrospectedTable introspectedTable, Set<String> ignores) {
        String baseRecordType = introspectedTable.getBaseRecordType();
        String baseRecordTypeWithBLOBs = introspectedTable.getRecordWithBLOBsType();
        boolean hasWithBLOBs = true;
        if (baseRecordTypeWithBLOBs == null || introspectedTable.getBLOBColumns().size() <= 1) {
            hasWithBLOBs = false;
        }
        Map<String, List<IntrospectedColumn>> map = new LinkedHashMap<>();

        // 常规方式
        map.put(MBGStringUtil.shortClassName(baseRecordType), introspectedTable.getAllColumns());
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<IntrospectedColumn>> entry : map.entrySet()) {
            if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
                stringBuilder.append("#").append(introspectedTable.getRemarks());
                OutputUtilities.newLine(stringBuilder);
            }
            stringBuilder.append("type ").append(entry.getKey()).append("{");
            for (IntrospectedColumn column : entry.getValue()) {
                // 排除忽略的字段
                if (ignores.contains(column.getActualColumnName())) {
                    continue;
                }
                OutputUtilities.newLine(stringBuilder);
                if (StringUtility.stringHasValue(column.getRemarks())) {
                    OutputUtilities.newLine(stringBuilder);
                    OutputUtilities.javaIndent(stringBuilder, 1);
                    stringBuilder.append("#").append(column.getRemarks());
                }
                OutputUtilities.newLine(stringBuilder);
                OutputUtilities.javaIndent(stringBuilder, 1);
                stringBuilder.append(column.getJavaProperty()).append(" : ");
                String scalarType = column.getFullyQualifiedJavaType().getShortName();
                switch (scalarType) {
                    case "Integer":
                        stringBuilder.append("Int");
                        break;
                    case "Double":
                        stringBuilder.append("Float");
                        break;
                    case "Byte":
                        stringBuilder.append("Int");
                        break;
                    default:
                        stringBuilder.append(scalarType);
                        break;
                }

            }
            OutputUtilities.newLine(stringBuilder);
            stringBuilder.append("}");
            OutputUtilities.newLine(stringBuilder);
            OutputUtilities.newLine(stringBuilder);
        }
        return stringBuilder.toString();
    }

}
