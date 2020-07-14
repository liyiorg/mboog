package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.logging.Log;
import org.mybatis.generator.logging.LogFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * choose improvement types<br>
 * <p>
 * set table property criterionImprovement=[1,2]
 *
 * @author LiYi
 */
public class CriterionPlugin extends PluginAdapter {

    private static Log log = LogFactory.getLog(CriterionPlugin.class);

    private static final String ABSTRACT_GENERATED_CRITERIA_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.AbstractGeneratedCriteria";

    private static final String CRITERION_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.Criterion";

    private static final String[] REMOVE_METHODS = {
            "GeneratedCriteria",
            "isValid",
            "getAllCriteria",
            "getCriteria",
            "addCriterion",
            "addCriterionForJDBCDate",
            "addCriterionForJDBCTime",
            "and.*Like",
            "and.*NotLike"};

    private static final String[] METHODS_PATTERN = {
            "and(.*)IsNull",
            "and(.*)IsNotNull",
            "and(.*)EqualTo",
            "and(.*)NotEqualTo",
            "and(.*)GreaterThan",
            "and(.*)GreaterThanOrEqualTo",
            "and(.*)LessThan",
            "and(.*)LessThanOrEqualTo",
            "and(.*)Like",
            "and(.*)LikeInsensitive",
            "and(.*)NotLike",
            "and(.*)NotLikeInsensitive",
            "and(.*)In",
            "and(.*)NotIn",
            "and(.*)Between",
            "and(.*)NotBetween"};

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<InnerClass> innerClassList = topLevelClass.getInnerClasses();
        boolean change = false;
        boolean removed = false;
        for (InnerClass innerClass : innerClassList) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                for (Field field : innerClass.getFields()) {
                    // 删除criteria 成员变量
                    if ("criteria".equals(field.getName())) {
                        innerClass.getFields().remove(field);
                        break;
                    }
                }

                topLevelClass.addImportedType(new FullyQualifiedJavaType(ABSTRACT_GENERATED_CRITERIA_CLASS));
                innerClass.setSuperClass(String.format("%s<Criteria>", ABSTRACT_GENERATED_CRITERIA_CLASS));
                change = true;
                //删除方法
                removeMethods(innerClass, introspectedTable);
                // 补充方法
                addColumnsMethods(innerClass, topLevelClass, introspectedTable);
                // 方法排序
                autoSortMethod(innerClass, introspectedTable);
                break;
            }
        }
        if (!change) {
            log.debug("Not find InnerClass GeneratedCriteria");
        }

        for (InnerClass innerClass : innerClassList) {
            if ("Criterion".equals(innerClass.getType().getShortName())) {
                if (!change) {
                    topLevelClass.addImportedType(CRITERION_CLASS);
                }
                innerClassList.remove(innerClass);
                removed = true;
                break;
            }
        }
        if (!removed) {
            log.debug("Not find InnerClass Criterion");
        }
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 删除方法
     *
     * @param generatedCriteria
     * @param introspectedTable
     */
    private void removeMethods(InnerClass generatedCriteria, IntrospectedTable introspectedTable) {
        List<String> removeMethods = new ArrayList<>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            StringBuilder name = new StringBuilder(column.getJavaProperty());
            name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
            if ("java.lang.Boolean".equals(column.getFullyQualifiedJavaType().getFullyQualifiedName())) {
                removeMethods.add(String.format("and%sNotEqualTo", name));
                removeMethods.add(String.format("and%sGreaterThan", name));
                removeMethods.add(String.format("and%sGreaterThanOrEqualTo", name));
                removeMethods.add(String.format("and%sLessThan", name));
                removeMethods.add(String.format("and%sLessThanOrEqualTo", name));
                removeMethods.add(String.format("and%sLike", name));
                removeMethods.add(String.format("and%sLikeInsensitive", name));
                removeMethods.add(String.format("and%sNotLike", name));
                removeMethods.add(String.format("and%sNotLikeInsensitive", name));
                removeMethods.add(String.format("and%sIn", name));
                removeMethods.add(String.format("and%sNotIn", name));
                removeMethods.add(String.format("and%sBetween", name));
                removeMethods.add(String.format("and%sNotBetween", name));
            }
        }
        removeMethods.addAll(Arrays.asList(REMOVE_METHODS));
        // 删除super 中已有的方法
        for (int i = 0; i < generatedCriteria.getMethods().size(); i++) {
            Method m = generatedCriteria.getMethods().get(i);
            for (String reMethod : removeMethods) {
                if (reMethod.equals(m.getName()) || m.getName().matches(reMethod)) {
                    generatedCriteria.getMethods().remove(i--);
                    break;
                }
            }

        }


    }

    /**
     * sort all methods
     *
     * @param generatedCriteria
     * @param introspectedTable
     */
    private void autoSortMethod(InnerClass generatedCriteria, IntrospectedTable introspectedTable) {
        List<Method> list = new ArrayList<>();
        introspectedTable.getAllColumns().stream().forEach(column -> {
            StringBuilder name = new StringBuilder(column.getJavaProperty());
            name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
            Arrays.stream(METHODS_PATTERN).forEach(m -> {
                String methodName = m.replace("(.*)", name);
                List<Method> searchList = generatedCriteria.getMethods().stream()
                        .filter(gm -> gm.getName().equals(methodName))
                        .collect(Collectors.toList());
                list.addAll(searchList);
            });
        });
        // 已匹配 METHODS_PATTERN 的方法
        Set<String> names = list.stream().map(m -> m.getName()).collect(Collectors.toSet());
        // 其它自定义方法
        List<Method> otherMethods = generatedCriteria.getMethods().stream().filter(m -> !names.contains(m.getName())).collect(Collectors.toList());
        list.addAll(otherMethods);
        generatedCriteria.getMethods().clear();
        generatedCriteria.getMethods().addAll(list);
    }

    /**
     * 补充方法
     *
     * @param generatedCriteria
     * @param topLevelClass
     * @param introspectedTable
     */
    private void addColumnsMethods(InnerClass generatedCriteria, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            // string 补充 Like NotLike method
            if (introspectedColumn.isStringColumn() && !introspectedColumn.isBLOBColumn()) {
                addInnerMethod(generatedCriteria, getSetLikePatternMethod(introspectedColumn));
                addInnerMethod(generatedCriteria, getSetNotLikePatternMethod(introspectedColumn));
            }
        }
    }

    private void addInnerMethod(InnerClass generatedCriteria, Method method) {
        generatedCriteria.getMethods().add(method);
    }


    private Method getSetLikePatternMethod(IntrospectedColumn introspectedColumn) {
        return getPatternValueMethod(introspectedColumn, "Like", "like");
    }

    private Method getSetNotLikePatternMethod(IntrospectedColumn introspectedColumn) {
        return getPatternValueMethod(introspectedColumn, "NotLike", "not like");
    }

    private Method getPatternValueMethod(IntrospectedColumn introspectedColumn,
                                         String nameFragment,
                                         String operator) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "pattern"));
        method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(), "value"));

        StringBuilder sb = new StringBuilder();
        sb.append(introspectedColumn.getJavaProperty());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        sb.insert(0, "and");
        sb.append(nameFragment);
        method.setName(sb.toString());
        method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());

        method.addBodyLine(
                String.format("addCriterionPattern(\"%s %s\", pattern, value, \"%s\");",
                        MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn),
                        operator,
                        introspectedColumn.getJavaProperty()));
        method.addBodyLine("return (Criteria) this;");
        return method;
    }


    @Override
    public boolean validate(List<String> arg0) {
        return true;
    }

}
