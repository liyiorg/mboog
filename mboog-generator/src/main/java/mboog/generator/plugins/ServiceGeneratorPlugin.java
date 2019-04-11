package mboog.generator.plugins;

import mboog.generator.util.MBGStringUtil;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 生成Service 代码
 *
 * @author LiYi
 */
public class ServiceGeneratorPlugin extends PluginAdapter {

    private String targetPackage;

    private String targetImplPackage;

    private static final String TARGET_PACKAGE_PROPERTY_NAME = "targetPackage";

    private static final String TARGET_IMPL_PACKAGE_PROPERTY_NAME = "targetImplPackage";

    private static final String READ_SERVICE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".service.ReadService";

    private static final String WRITE_SERVICE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".service.WriteService";

    private static final String UPSERT_SERVICE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".service.UpsertService";

    private static final String SERVICE_SUPPORT_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".service.ServiceSupport";

    private static final String NO_KEY_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".model.NoKey";

    private static final String IMPORT_SPRING_SERVICE = "org.springframework.stereotype.Service";

    private static final String IMPORT_RESOURCE = "javax.annotation.Resource";

    private static final String IMPORT_POST_CONSTRUCT = "javax.annotation.PostConstruct";


    private boolean readonly;

    private String serviceClassName;

    private String serviceImplClassName;


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        if ("VIEW".equalsIgnoreCase(introspectedTable.getTableType())) {
            readonly = true;
        } else {
            String readonly_pro = introspectedTable.getTableConfiguration().getProperty("readonly");
            readonly = StringUtility.isTrue(readonly_pro);
        }

        //set targetPackage
        Object pro_targetPackage = properties.get(TARGET_PACKAGE_PROPERTY_NAME);
        if (pro_targetPackage != null && StringUtility.stringHasValue(pro_targetPackage.toString())) {
            targetPackage = pro_targetPackage.toString();
        } else {
            //set default service package
            String baseRecordType = introspectedTable.getBaseRecordType();
            String[] sp = baseRecordType.split("\\.");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < sp.length - 2; i++) {
                stringBuilder.append(sp[i]).append(".");
            }
            stringBuilder.append("service");
            targetPackage = stringBuilder.toString();
        }

        //set targetImplPackage
        Object pro_targetImplPackage = properties.get(TARGET_IMPL_PACKAGE_PROPERTY_NAME);
        if (pro_targetImplPackage != null && StringUtility.stringHasValue(pro_targetImplPackage.toString())) {
            targetImplPackage = pro_targetImplPackage.toString();
        } else {
            targetImplPackage = targetPackage + ".impl";
        }

        serviceClassName = String.format("%s.%sService", targetPackage, MBGStringUtil.shortClassName(introspectedTable.getBaseRecordType()));
        serviceImplClassName = String.format("%s.%sServiceImpl", targetImplPackage, MBGStringUtil.shortClassName(introspectedTable.getBaseRecordType()));
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String baseRecordType = introspectedTable.getBaseRecordType();
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
        String mapperType = introspectedTable.getMyBatis3JavaMapperType();
        String exampleType = introspectedTable.getExampleType();

        GeneratedJavaFile GeneratedJavaFileWithInterface = buildGeneratedJavaFileWithInterface(introspectedTable, baseRecordType, primaryKeyType, mapperType, exampleType);
        GeneratedJavaFile GeneratedJavaFileWithImpl = buildGeneratedJavaFileWithImpl(introspectedTable, baseRecordType, primaryKeyType, mapperType, exampleType);
        return Arrays.asList(GeneratedJavaFileWithInterface, GeneratedJavaFileWithImpl);
    }


    /**
     * 生成接口类
     *
     * @param introspectedTable
     * @param baseRecordType
     * @param primaryKeyType
     * @param mapperType
     * @param exampleType
     * @return
     */
    private GeneratedJavaFile buildGeneratedJavaFileWithInterface(IntrospectedTable introspectedTable, String baseRecordType, String primaryKeyType, String mapperType, String exampleType) {
        String genericsStr = joinGenerics(primaryKeyType, baseRecordType, exampleType, mapperType);
        Interface interfaceJava = new Interface(serviceClassName);
        interfaceJava.setVisibility(JavaVisibility.PUBLIC);
        Stream.of(primaryKeyType, baseRecordType, exampleType, mapperType)
                .forEach(n ->
                        interfaceJava.addImportedType(new FullyQualifiedJavaType(n))
                );

        List<String> superInterfaces = new ArrayList<>();
        superInterfaces.add(READ_SERVICE_CLASS);
        if (!readonly) {
            superInterfaces.add(WRITE_SERVICE_CLASS);
            if (introspectedTable.getTableConfiguration().getGeneratedKey() == null) {
                superInterfaces.add(UPSERT_SERVICE_CLASS);
            }
        }
        superInterfaces.forEach(n -> {
            interfaceJava.addImportedType(new FullyQualifiedJavaType(n));
            interfaceJava.addSuperInterface(new FullyQualifiedJavaType(String.format("%s%s", MBGStringUtil.shortClassName(n), genericsStr)));
        });

        context.getCommentGenerator().addJavaFileComment(interfaceJava);
        return new GeneratedJavaFile(interfaceJava, context.getJavaClientGeneratorConfiguration().getTargetProject(), "utf-8", context.getJavaFormatter());
    }

    /**
     * 生成接口实现类
     *
     * @param introspectedTable
     * @param baseRecordType
     * @param primaryKeyType
     * @param mapperType
     * @param exampleType
     * @return
     */
    private GeneratedJavaFile buildGeneratedJavaFileWithImpl(IntrospectedTable introspectedTable, String baseRecordType, String primaryKeyType, String mapperType, String exampleType) {
        String genericsStr = joinGenerics(primaryKeyType, baseRecordType, exampleType, mapperType);
        TopLevelClass topLevelClass = new TopLevelClass(serviceImplClassName);
        topLevelClass.addAnnotation(String.format("@%s", MBGStringUtil.shortClassName(IMPORT_SPRING_SERVICE)));
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        Stream.of(primaryKeyType, baseRecordType, exampleType, mapperType, serviceClassName, SERVICE_SUPPORT_CLASS, IMPORT_SPRING_SERVICE, IMPORT_RESOURCE, IMPORT_POST_CONSTRUCT)
                .forEach(n ->
                        topLevelClass.addImportedType(new FullyQualifiedJavaType(n))
                );
        topLevelClass.setSuperClass(String.format("%s%s", MBGStringUtil.shortClassName(SERVICE_SUPPORT_CLASS), genericsStr));
        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(MBGStringUtil.shortClassName(serviceClassName)));

        String shortName = MBGStringUtil.shortClassName(mapperType);
        String mapperName = shortName.substring(0, 1).toLowerCase() + shortName.substring(1);

        // 添加 mapperField
        Field mapperField = new Field();
        mapperField.setVisibility(JavaVisibility.PRIVATE);
        mapperField.setType(new FullyQualifiedJavaType(MBGStringUtil.shortClassName(shortName)));
        mapperField.setName(mapperName);
        mapperField.addAnnotation(String.format("@%s", MBGStringUtil.shortClassName(IMPORT_RESOURCE)));
        context.getCommentGenerator().addFieldComment(mapperField, introspectedTable);
        topLevelClass.addField(mapperField);

        // 添加方法 initServiceMethod
        Method initServiceMethod = new Method();
        initServiceMethod.setVisibility(JavaVisibility.PRIVATE);
        initServiceMethod.setName("initService");
        initServiceMethod.addBodyLine(String.format("super.mapper = %s;", mapperField.getName()));
        initServiceMethod.addAnnotation(String.format("@%s", MBGStringUtil.shortClassName(IMPORT_POST_CONSTRUCT)));
        context.getCommentGenerator().addGeneralMethodComment(initServiceMethod, introspectedTable);
        topLevelClass.addMethod(initServiceMethod);

        context.getCommentGenerator().addJavaFileComment(topLevelClass);
        return new GeneratedJavaFile(topLevelClass, context.getJavaClientGeneratorConfiguration().getTargetProject(), "utf-8", context.getJavaFormatter());
    }

    /**
     * 拼接 泛型声明串
     *
     * @param javaTypes 泛型类型
     * @return str
     */
    private String joinGenerics(String... javaTypes) {
        String javaTypesJoin = Arrays.stream(javaTypes)
                .map(n -> MBGStringUtil.shortClassName(n))
                .collect(Collectors.joining(", "));
        return String.format("<%s>", javaTypesJoin);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
