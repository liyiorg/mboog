package mboog.generator.plugins;

import java.util.Arrays;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * ExampleBasePlugin Set SuperClass
 * <code>mboog.mbg.support.example.MbgExample</code>
 *
 * @author LiYi
 */
public class ExampleBasePlugin extends PluginAdapter {

    private static final String ABSTRACT_EXAMPLE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.AbstractExample";

    private static final String[] REMOVE_METHODS = "getOredCriteria,or,or,createCriteria,createCriteriaInternal,setOrderByClause,getOrderByClause,setDistinct,isDistinct,clear".split(",");

    private static final String[] REMOVE_FIELDS = "oredCriteria,orderByClause,distinct".split(",");

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addAnnotation("@SuppressWarnings(\"unused\")");
        // 添加import
        topLevelClass.addImportedType("java.io.Serializable");
        topLevelClass.addImportedType(ABSTRACT_EXAMPLE_CLASS);
        topLevelClass.addImportedType(introspectedTable.getExampleType() + ".Criteria");
        topLevelClass.setSuperClass(
                String.format("%s<%s, %s, %s>", ABSTRACT_EXAMPLE_CLASS, introspectedTable.getExampleType(), "Criteria", "C"));

        // 添加 Serializable
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));
        Field field_serial = new Field("serialVersionUID", new FullyQualifiedJavaType("long"));
        field_serial.setStatic(true);
        field_serial.setFinal(true);
        field_serial.setVisibility(JavaVisibility.PRIVATE);
        field_serial.setInitializationString("1L");
        field_serial.addJavaDocLine("");
        topLevelClass.getFields().add(0, field_serial);

        // 删除 父类中已存在的 METHOD
        Arrays.stream(REMOVE_METHODS).forEach(name -> {
            for (Method method : topLevelClass.getMethods()) {
                if (name.equals(method.getName())) {
                    topLevelClass.getMethods().remove(method);
                    break;
                }
            }
        });

        // 删除 父类中已存在的 FIELD
        Arrays.stream(REMOVE_FIELDS).forEach(name -> {
            for (Field field : topLevelClass.getFields()) {
                if (name.equals(field.getName())) {
                    topLevelClass.getFields().remove(field);
                    break;
                }
            }
        });

        for (Method method : topLevelClass.getMethods()) {
            if (method.isConstructor() && (method.getParameters() == null || method.getParameters().size() == 0)) {
                if (!method.getBodyLines().isEmpty()) {
                    for (int i = 0; i < method.getBodyLines().size(); i++) {
                        String body = method.getBodyLines().get(i);
                        if (body.startsWith("oredCriteria")) {
                            method.getBodyLines().remove(i);
                            break;
                        }
                    }
                }
                method.addBodyLine("supplier = Criteria::new;");
                break;
            }
        }

        // Criteria 添加 Serializable
        List<InnerClass> innerClassList = topLevelClass.getInnerClasses();
        for (InnerClass innerClass : innerClassList) {
            if ("Criteria".equals(innerClass.getType().getShortName())) {
                // 添加 Serializable
                innerClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));
                innerClass.addField(field_serial);
            }
        }

		/*Method instanceMethod = new Method("newInstance");
		instanceMethod.setStatic(true);
		instanceMethod.setVisibility(JavaVisibility.PUBLIC);
		instanceMethod.setReturnType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
		instanceMethod.addBodyLine(
				String.format("return new %s();", MBGStringUtil.shortClassName(introspectedTable.getExampleType())));
		topLevelClass.addMethod(instanceMethod);
		context.getCommentGenerator().addGeneralMethodComment(instanceMethod, introspectedTable);*/

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

}
