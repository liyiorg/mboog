package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Objects;

/**
 * Model Swagger plugin
 *
 * @author LiYi
 */
public class ModelSwaggerPlugin extends PluginAdapter {

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
        if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
            topLevelClass.addAnnotation(String.format("@ApiModel(\"%s\")", introspectedTable.getRemarks()));
        } else {
            topLevelClass.addAnnotation("@ApiModel");
        }
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@ApiModelProperty(");
        boolean hasName = false;
        if (StringUtility.stringHasValue(introspectedColumn.getRemarks())) {
            stringBuilder.append("value=\"").append(introspectedColumn.getRemarks()).append("\"");
            hasName = true;
        }
        String example = null;
        switch (introspectedColumn.getFullyQualifiedJavaType().getShortName()) {
            case "LocalDateTime":
                example = "yyyy-MM-dd HH:mm:ss";
                break;
            case "LocalDate":
                example = "yyyy-MM-dd";
                break;
            case "LocalTime":
                example = "HH:mm:ss";
                break;
            case "Date":
                example = "yyyy-MM-dd HH:mm:ss";
                break;
        }
        if (Objects.nonNull(example)) {
            stringBuilder.append(hasName ? " ," : "").append("example=\"").append(example).append("\"");
        }
        stringBuilder.append(")");
        if (stringBuilder.length() > 20) {
            field.addAnnotation(stringBuilder.toString());
        } else {
            field.addAnnotation("@ApiModelProperty");
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
        topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
        if (StringUtility.stringHasValue(introspectedTable.getRemarks())) {
            topLevelClass.addAnnotation(String.format("@ApiModel(\"%s\")", introspectedTable.getRemarks()));
        } else {
            topLevelClass.addAnnotation("@ApiModel");
        }
        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
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
