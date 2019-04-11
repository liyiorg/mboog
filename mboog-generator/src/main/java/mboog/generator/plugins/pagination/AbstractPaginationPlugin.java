package mboog.generator.plugins.pagination;

import mboog.generator.plugins.PluginsConstants;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 分页插件父类
 *
 * @author LiYi
 */
public abstract class AbstractPaginationPlugin extends PluginAdapter {

    private static final String PAGINATION_ABLE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.PaginationAble";

    public abstract String getDataBaseType();

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        if (getDataBaseType() != null) {
            for (Method method : topLevelClass.getMethods()) {
                if (method.isConstructor() && (method.getParameters() == null || method.getParameters().size() == 0)) {
                    method.addBodyLine("databaseType = \"" + getDataBaseType() + "\";");
                    break;
                }
            }
        }

        // add PaginationAble interface
        topLevelClass.addImportedType(PAGINATION_ABLE_CLASS);
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(PAGINATION_ABLE_CLASS + String.format("<%s>", introspectedTable.getExampleType())));
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

}
