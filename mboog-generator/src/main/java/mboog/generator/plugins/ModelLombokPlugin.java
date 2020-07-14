package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * Model Lombok 使用
 *
 * @author LiYi
 */
public class ModelLombokPlugin extends PluginAdapter {

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelLombokGenerated(topLevelClass);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }


    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        modelLombokGenerated(topLevelClass);
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }


    /**
     * Model 类 lombok 处理
     *
     * @param topLevelClass
     */
    private void modelLombokGenerated(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("lombok.Getter");
        topLevelClass.addImportedType("lombok.Setter");
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        topLevelClass.addImportedType("lombok.ToString");

        topLevelClass.addAnnotation("@Getter");
        topLevelClass.addAnnotation("@Setter");
        topLevelClass.addAnnotation("@EqualsAndHashCode");
        topLevelClass.addAnnotation("@ToString");

        // Remove methods [get*, set*, hashCode, toString]
        List<Method> methods = topLevelClass.getMethods();
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            if (method.getName().startsWith("get")
                    || method.getName().startsWith("set")
                    || method.getName().matches("toString|hashCode")) {
                methods.remove(i);
                i--;
            }
        }
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
