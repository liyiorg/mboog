package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 乐观锁 更新
 *
 * @author LiYi
 */
public class OptimisticLockPlugin extends PluginAdapter {

    private static String DEFAULT_LOCK_COLUMN = "lock_version";

    private static String LOCK_NAEM = "lock";

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
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        if (!readonly) {
            try {
                String lockVersionColumnName = null;
                String lock = introspectedTable.getTableConfiguration().getProperty(LOCK_NAEM);
                if (StringUtility.stringHasValue(lock)) {
                    if (StringUtility.isTrue(lock)) {
                        lockVersionColumnName = DEFAULT_LOCK_COLUMN;
                    } else if (!"false".equals(lock)) {
                        lockVersionColumnName = lock;
                    }
                }
                if (lockVersionColumnName != null
                        && introspectedTable.hasPrimaryKeyColumns()) {
                    if (introspectedTable.getColumn(lockVersionColumnName) != null) {
                        // 获取所有insert xml element
                        Map<String, XmlElement> updateXmlElementMap = document.getRootElement().getElements().stream()
                                .filter(e -> e instanceof XmlElement).map(e -> (XmlElement) e)
                                .filter(e -> "update".equals(e.getName()))
                                .collect(Collectors.toMap(n -> n
                                                .getAttributes().stream()
                                                .collect(Collectors.toMap(Attribute::getName, Attribute::getValue))
                                                .get("id"),
                                        Function.identity()));
                        if (introspectedTable.hasBaseColumns()) {
                            XmlElement xmlElement1 = buildUpdateByPrimaryKeyWithOptimisticLock(lockVersionColumnName, introspectedTable);
                            document.getRootElement().addElement(xmlElement1);
                            XmlElement xmlElement2 = buildUpdateByPrimaryKeySelectiveWithOptimisticLock(lockVersionColumnName, introspectedTable);
                            document.getRootElement().addElement(xmlElement2);
                        }
                    } else {
                        throw new RuntimeException("No column name " + lockVersionColumnName + " with table " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


    /**
     * 生成 updateByPrimaryKey 乐观锁模式
     *
     * @param lock_version_column
     * @param introspectedTable
     * @return
     */
    private XmlElement buildUpdateByPrimaryKeyWithOptimisticLock(String lock_version_column, IntrospectedTable introspectedTable) {

        XmlElement xmlElement = new XmlElement("update");
        xmlElement.addAttribute(new Attribute("id", "updateByPrimaryKeyWithOptimisticLock"));
        String parameterType;
        if (introspectedTable.hasBLOBColumns() && introspectedTable.getBLOBColumns().size() > 1) {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        } else {
            parameterType = introspectedTable.getBaseRecordType();
        }
        xmlElement.addAttribute(new Attribute("parameterType", parameterType));
        context.getCommentGenerator().addComment(xmlElement);

        xmlElement.addElement(new TextElement("update " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        XmlElement setXmlElement = new XmlElement("set");
        for (IntrospectedColumn column : introspectedTable.getNonPrimaryKeyColumns()) {
            if (column.getActualColumnName().equals(lock_version_column)) {
                String body = String.format("%s = %s + 1,",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getEscapedColumnName(column));
                setXmlElement.addElement(new TextElement(body));
            } else {
                String body = String.format("%s = %s,",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getParameterClause(column));
                setXmlElement.addElement(new TextElement(body));
            }
        }
        xmlElement.addElement(setXmlElement);

        // 构建 where 部分
        List<IntrospectedColumn> keyColumns = new ArrayList<>();
        keyColumns.addAll(introspectedTable.getPrimaryKeyColumns());
        keyColumns.add(introspectedTable.getColumn(lock_version_column));
        for (int i = 0; i < keyColumns.size(); i++) {
            IntrospectedColumn column = keyColumns.get(i);
            String body = String.format("%s %s = %s",
                    i == 0 ? "where" : "  and",
                    MyBatis3FormattingUtilities.getEscapedColumnName(column),
                    MyBatis3FormattingUtilities.getParameterClause(column));
            xmlElement.addElement(new TextElement(body));
        }

        return xmlElement;
    }


    /**
     * 生成 updateByPrimaryKeySelective 乐观锁模式
     *
     * @param lock_version_column
     * @param introspectedTable
     * @return
     */
    private XmlElement buildUpdateByPrimaryKeySelectiveWithOptimisticLock(String lock_version_column, IntrospectedTable introspectedTable) {

        XmlElement xmlElement = new XmlElement("update");
        xmlElement.addAttribute(new Attribute("id", "updateByPrimaryKeySelectiveWithOptimisticLock"));
        String parameterType;
        if (introspectedTable.hasBLOBColumns() && introspectedTable.getBLOBColumns().size() > 1) {
            parameterType = introspectedTable.getRecordWithBLOBsType();
        } else {
            parameterType = introspectedTable.getBaseRecordType();
        }
        xmlElement.addAttribute(new Attribute("parameterType", parameterType));
        context.getCommentGenerator().addComment(xmlElement);

        xmlElement.addElement(new TextElement("update " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
        XmlElement setXmlElement = new XmlElement("set");
        for (IntrospectedColumn column : introspectedTable.getNonPrimaryKeyColumns()) {
            if (column.getActualColumnName().equals(lock_version_column)) {
                String body = String.format("  %s = %s + 1,",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getEscapedColumnName(column));
                setXmlElement.addElement(new TextElement(body));
            } else {
                XmlElement innerIfXmlElement = new XmlElement("if");
                String test = String.format("%s != null", column.getJavaProperty());
                innerIfXmlElement.addAttribute(new Attribute("test", test));
                String body = String.format("%s = %s,",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getParameterClause(column));
                innerIfXmlElement.addElement(new TextElement(body));
                setXmlElement.addElement(innerIfXmlElement);
            }
        }
        xmlElement.addElement(setXmlElement);

        // 构建 where 部分
        List<IntrospectedColumn> keyColumns = new ArrayList<>();
        keyColumns.addAll(introspectedTable.getPrimaryKeyColumns());
        keyColumns.add(introspectedTable.getColumn(lock_version_column));
        for (int i = 0; i < keyColumns.size(); i++) {
            IntrospectedColumn column = keyColumns.get(i);
            String body = String.format("%s %s = %s",
                    i == 0 ? "where" : "  and",
                    MyBatis3FormattingUtilities.getEscapedColumnName(column),
                    MyBatis3FormattingUtilities.getParameterClause(column));
            xmlElement.addElement(new TextElement(body));
        }

        return xmlElement;
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
