package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * Model Lombok plugin
 *
 * @author LiYi
 */
public class ModelLombokPlugin extends PluginAdapter {

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelLombokGenerated(topLevelClass, false, introspectedTable.getPrimaryKeyColumns().size() > 1);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }


    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelLombokGenerated(topLevelClass, true, false);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }


    /**
     * Model lombok build
     *
     * @param topLevelClass   Top level Class
     * @param primaryKeyClass Primary key Class
     * @param callSuper callSuper
     */
    private void modelLombokGenerated(TopLevelClass topLevelClass, boolean primaryKeyClass, boolean callSuper) {

        topLevelClass.addImportedType("lombok.Getter");
        topLevelClass.addImportedType("lombok.Setter");
        topLevelClass.addImportedType("lombok.ToString");
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");

        topLevelClass.addAnnotation("@Getter");
        topLevelClass.addAnnotation("@Setter");

        if (primaryKeyClass || !callSuper) {
            topLevelClass.addAnnotation("@ToString");
            topLevelClass.addAnnotation("@EqualsAndHashCode");
        } else {
            topLevelClass.addAnnotation("@ToString(callSuper = true)");
            topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        }

        // Remove methods [get*, set*, toString, equals, hashCode]
        List<Method> methods = topLevelClass.getMethods();
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (method.getName().startsWith("get")
                    || method.getName().startsWith("set")
                    || method.getName().matches("toString|equals|hashCode")) {
                methods.remove(i--);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
