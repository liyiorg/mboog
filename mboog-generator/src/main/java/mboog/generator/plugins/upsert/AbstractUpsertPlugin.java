package mboog.generator.plugins.upsert;

import mboog.generator.plugins.PluginsConstants;
import mboog.generator.util.MBGStringUtil;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiYi
 */
public abstract class AbstractUpsertPlugin extends PluginAdapter {

    private static Log log = LogFactory.getLog(AbstractUpsertPlugin.class);

    private static final String UPSERT_MAPPER_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".mapper.UpsertMapper";

    protected boolean canUpsert;

    protected String parameterType;

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        boolean readonly;
        if ("VIEW".equalsIgnoreCase(introspectedTable.getTableType())) {
            readonly = true;
        } else {
            String readonly_pro = introspectedTable.getTableConfiguration().getProperty("readonly");
            readonly = StringUtility.isTrue(readonly_pro);
        }
        boolean noGeneratedKey = introspectedTable.getGeneratedKey() == null;
        canUpsert = !readonly && noGeneratedKey && introspectedTable.hasPrimaryKeyColumns();
        if (introspectedTable.hasBLOBColumns() && introspectedTable.getBLOBColumns().size() > 1) {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        } else {
            parameterType = introspectedTable.getBaseRecordType();
        }
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        if (canUpsert) {
            String baseRecordType = introspectedTable.getBaseRecordType();
            String exampleType = introspectedTable.getExampleType();
            String primaryKeyType;
            List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
            if (columns.size() == 1) {
                primaryKeyType = columns.get(0).getFullyQualifiedJavaType().getFullyQualifiedName();
            } else {
                primaryKeyType = introspectedTable.getPrimaryKeyType();
            }

            interfaze.addImportedType(new FullyQualifiedJavaType(baseRecordType));
            interfaze.addImportedType(new FullyQualifiedJavaType(exampleType));
            if (!primaryKeyType.startsWith("java.lang.")) {
                interfaze.addImportedType(new FullyQualifiedJavaType(primaryKeyType));
            }
            List<String> superInterfaces = new ArrayList<>();
            superInterfaces.add(UPSERT_MAPPER_CLASS);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(MBGStringUtil.shortClassName(UPSERT_MAPPER_CLASS))
                    .append("<")
                    .append(MBGStringUtil.shortClassName(primaryKeyType))
                    .append(", ")
                    .append(MBGStringUtil.shortClassName(baseRecordType))
                    .append(", ")
                    .append(MBGStringUtil.shortClassName(exampleType))
                    .append(">");
            interfaze.addImportedType(new FullyQualifiedJavaType(UPSERT_MAPPER_CLASS));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(stringBuilder.toString()));
            log.debug("Extend Mapper " + interfaze.getType().getFullyQualifiedName() + " with " + stringBuilder.toString());
        }
        return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
