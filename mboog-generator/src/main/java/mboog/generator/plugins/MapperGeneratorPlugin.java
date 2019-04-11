package mboog.generator.plugins;

import mboog.generator.util.MBGStringUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper 父类
 * <p>
 * 设置属性 readonly 生成只读接口Mapper <br>
 * 示例 <br>
 *
 * @author LiYi
 */
public class MapperGeneratorPlugin extends PluginAdapter {

    private static Log log = LogFactory.getLog(MapperGeneratorPlugin.class);


    private static final String READ_MAPPER_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".mapper.ReadMapper";

    private static final String WRITE_MAPPER_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".mapper.WriteMapper";

    private static final String NO_KEY_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".model.NoKey";

    private static final String MAPPER_CLASS = "org.apache.ibatis.annotations.Mapper";

    private boolean readonly;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        if ("VIEW".equalsIgnoreCase(introspectedTable.getTableType())) {
            readonly = true;
        } else {
            String readonly_pro = introspectedTable.getTableConfiguration().getProperty("readonly");
            readonly = StringUtility.isTrue(readonly_pro);
        }
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!introspectedTable.hasPrimaryKeyColumns()) {
            topLevelClass.addImportedType(NO_KEY_CLASS);
            topLevelClass.addSuperInterface(new FullyQualifiedJavaType(NO_KEY_CLASS));
        }
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        try {
            interfaze.addImportedType(new FullyQualifiedJavaType(MAPPER_CLASS));
            interfaze.addAnnotation("@Mapper");
            String baseRecordType = introspectedTable.getBaseRecordType();
            String exampleType = introspectedTable.getExampleType();

            String primaryKeyType;
            if (introspectedTable.hasPrimaryKeyColumns()) {
                List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
                if (columns.size() == 1) {
                    primaryKeyType = columns.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
                } else {
                    primaryKeyType = introspectedTable.getPrimaryKeyType();
                }
            } else {
                primaryKeyType = NO_KEY_CLASS;
            }

            interfaze.addImportedType(new FullyQualifiedJavaType(baseRecordType));
            interfaze.addImportedType(new FullyQualifiedJavaType(exampleType));
            if (!primaryKeyType.startsWith("java.lang.")) {
                interfaze.addImportedType(new FullyQualifiedJavaType(primaryKeyType));
            }

            List<String> superInterfaces = new ArrayList<>();
            superInterfaces.add(READ_MAPPER_CLASS);
            if (!readonly) {
                superInterfaces.add(WRITE_MAPPER_CLASS);
            }
            for (String superClass : superInterfaces) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(MBGStringUtil.shortClassName(superClass))
                        .append("<")
                        .append(MBGStringUtil.shortClassName(primaryKeyType))
                        .append(", ")
                        .append(MBGStringUtil.shortClassName(baseRecordType))
                        .append(", ")
                        .append(MBGStringUtil.shortClassName(exampleType))
                        .append(">");
                interfaze.addImportedType(new FullyQualifiedJavaType(superClass));
                interfaze.addSuperInterface(new FullyQualifiedJavaType(stringBuilder.toString()));
                log.debug("Extend Mapper " + interfaze.getType().getFullyQualifiedName() + " with " + stringBuilder.toString());
            }
            selectByPrimaryKeyWithColumns(interfaze, introspectedTable, exampleType, baseRecordType, primaryKeyType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    /**
     * 生成按主键获取对应列的数据
     *
     * @param interfaze
     * @param introspectedTable
     * @param exampleType
     * @param baseRecordType
     * @param primaryKeyType
     */
    private void selectByPrimaryKeyWithColumns(Interface interfaze, IntrospectedTable introspectedTable, String exampleType, String baseRecordType, String primaryKeyType) {
        if (introspectedTable.hasPrimaryKeyColumns()) {
            Method method = new Method();
            method.setName("selectByPrimaryKeyWithColumns");
            method.setVisibility(JavaVisibility.DEFAULT);
            method.setReturnType(new FullyQualifiedJavaType("default " + MBGStringUtil.shortClassName(baseRecordType)));
            method.addParameter(new Parameter(new FullyQualifiedJavaType(primaryKeyType), "primaryKey"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType("boolean"), "include"));
            method.addParameter(new Parameter(new FullyQualifiedJavaType(MBGStringUtil.shortClassName(exampleType) + ".C..."), "cs"));
            method.addBodyLine(String.format("%s example = new %s();", MBGStringUtil.shortClassName(exampleType), MBGStringUtil.shortClassName(exampleType)));
            method.addBodyLine("if (include) {");
            method.addBodyLine("    example.includeColumns(cs);");
            method.addBodyLine("} else {");
            method.addBodyLine("    example.excludeColumns(cs);");
            method.addBodyLine("}");
            method.addBodyLine("example.or(cri -> cri");
            for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                String name = column.getJavaProperty();
                String methodName = String.format("%s%s",
                        name.substring(0, 1).toUpperCase(),
                        name.length() > 1 ? name.substring(1) : "");
                if (introspectedTable.getPrimaryKeyColumns().size() == 1) {
                    method.addBodyLine(String.format("       .and%sEqualTo(primaryKey)", methodName));
                } else {
                    method.addBodyLine(String.format("       .and%sEqualTo(primaryKey.get%s())", methodName, methodName));
                }
            }
            method.addBodyLine(");");
            method.addBodyLine("return selectByExampleSingleResult(example);");
            context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
            interfaze.addMethod(method);
        }
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, Interface interfaze,
                                                       IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientCountByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                       IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, Interface interfaze,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByExampleMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                           IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze,
                                               IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, TopLevelClass topLevelClass,
                                               IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze,
                                                           IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                           IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                 IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByExampleWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                    IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface interfaze,
                                                                       IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                                       IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                        IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, Interface interfaze,
                                                  IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientSelectAllMethodGenerated(Method method, TopLevelClass topLevelClass,
                                                  IntrospectedTable introspectedTable) {
        return false;
    }


    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapDeleteByExampleElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapInsertElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithBLOBsElementGenerated(XmlElement element,
                                                                  IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByExampleWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element,
                                                                        IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        return !readonly && super.sqlMapInsertSelectiveElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        for (Attribute attribute : element.getAttributes()) {
            // 确定进入 Update_By_Example_Where_Clause
            if ("id".equals(attribute.getName()) && "Update_By_Example_Where_Clause".equals(attribute.getValue())) {
                if (readonly) {
                    return false;
                }
            }
        }
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

}
