package mboog.generator.plugins;

import mboog.generator.plugins.upsert.AbstractUpsertPlugin;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LiYi
 */
public class BatchInsertPlugin extends PluginAdapter {

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
            List<IntrospectedColumn> columns;
            if (introspectedTable.getGeneratedKey() != null) {
                String generatedKeyColumn = introspectedTable.getGeneratedKey().getColumn();
                columns = introspectedTable.getAllColumns().stream()
                        .filter(n -> !n.getActualColumnName().equals(generatedKeyColumn))
                        .collect(Collectors.toList());
            } else {
                columns = introspectedTable.getAllColumns();
            }

            String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

            document.getRootElement().addElement(batchInsertXmlElement(tableName, columns));
            document.getRootElement().addElement(batchInsertSelectiveXmlElement(tableName, columns));

        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private XmlElement batchInsertXmlElement(String tableName, List<IntrospectedColumn> columns) {
        // 构建 batchInsert
        XmlElement batchInsertXml = new XmlElement("insert");
        batchInsertXml.addAttribute(new Attribute("id", "batchInsert"));
        batchInsertXml.addAttribute(new Attribute("parameterType", "java.util.List"));
        context.getCommentGenerator().addComment(batchInsertXml);
        batchInsertXml.addElement(new TextElement("insert into " + tableName + " ("));
        StringBuilder stringBuilderInto = new StringBuilder("  ");
        for (int i = 0; i < columns.size(); i += 4) {
            for (int x = 0; x < 4 && i + x < columns.size(); x++) {
                IntrospectedColumn column = columns.get(i + x);
                OutputUtilities.xmlIndent(stringBuilderInto,1);
                stringBuilderInto.append(MyBatis3FormattingUtilities.getEscapedColumnName(column));
                if (i + x < columns.size() - 1) {
                    stringBuilderInto.append(",");
                } else {
                    stringBuilderInto.append(" )");
                }
            }
            if (i + 4 < columns.size()) {
                OutputUtilities.newLine(stringBuilderInto);
            }
            OutputUtilities.xmlIndent(stringBuilderInto,2);
        }
        StringBuilder stringBuilderValues = new StringBuilder("(");
        for (int i = 0; i < columns.size(); i += 3) {
            for (int x = 0; x < 3 && i + x < columns.size(); x++) {
                IntrospectedColumn column = columns.get(i + x);
                String parameterClause = MyBatis3FormattingUtilities.getParameterClause(column);
                parameterClause = parameterClause.replace("#{", "#{listItem.");
                OutputUtilities.xmlIndent(stringBuilderValues,1);
                stringBuilderValues.append(parameterClause);

                if (i + x < columns.size() - 1) {
                    stringBuilderValues.append(",");
                } else {
                    stringBuilderValues.append(" )");
                }
            }
            if (i + 3 < columns.size()) {
                OutputUtilities.newLine(stringBuilderValues);
            }
            OutputUtilities.xmlIndent(stringBuilderValues,2);
        }
        batchInsertXml.addElement(new TextElement(stringBuilderInto.toString()));
        batchInsertXml.addElement(new TextElement("values"));
        XmlElement foreachXmlElement = new XmlElement("foreach");
        foreachXmlElement.addAttribute(new Attribute("collection", "list"));
        foreachXmlElement.addAttribute(new Attribute("item", "listItem"));
        foreachXmlElement.addAttribute(new Attribute("separator", ","));
        foreachXmlElement.addElement(new TextElement(stringBuilderValues.toString()));
        batchInsertXml.addElement(foreachXmlElement);
        return batchInsertXml;
    }

    private XmlElement batchInsertSelectiveXmlElement(String tableName, List<IntrospectedColumn> columns) {
        // 构建 batchInsertSelective
        XmlElement batchInsertSelectiveXml = new XmlElement("insert");
        batchInsertSelectiveXml.addAttribute(new Attribute("id", "batchInsertSelective"));
        batchInsertSelectiveXml.addAttribute(new Attribute("parameterType", "java.util.List"));
        context.getCommentGenerator().addComment(batchInsertSelectiveXml);
        batchInsertSelectiveXml.addElement(new TextElement("insert into " + tableName));

        XmlElement trimIntoXmlElement = new XmlElement("trim");
        trimIntoXmlElement.addAttribute(new Attribute("suffixOverrides", ","));
        trimIntoXmlElement.addAttribute(new Attribute("prefix", "("));
        trimIntoXmlElement.addAttribute(new Attribute("suffix", ") values"));

        XmlElement foreachXmlElement = new XmlElement("foreach");
        foreachXmlElement.addAttribute(new Attribute("collection", "list"));
        foreachXmlElement.addAttribute(new Attribute("item", "listItem"));
        foreachXmlElement.addAttribute(new Attribute("separator", ","));

        XmlElement trimValueXmlElement = new XmlElement("trim");
        trimValueXmlElement.addAttribute(new Attribute("suffixOverrides", ","));
        trimValueXmlElement.addAttribute(new Attribute("prefix", "("));
        trimValueXmlElement.addAttribute(new Attribute("suffix", ")"));

        foreachXmlElement.addElement(trimValueXmlElement);
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn column = columns.get(i);

            XmlElement innerInfXmlElement = new XmlElement("if");
            String test = String.format("list[0].%s != null", column.getJavaProperty());
            innerInfXmlElement.addAttribute(new Attribute("test", test));
            innerInfXmlElement.addElement(new TextElement(MyBatis3FormattingUtilities.getEscapedColumnName(column) + ","));
            trimIntoXmlElement.addElement(innerInfXmlElement);

            XmlElement innerValueInfXmlElement = new XmlElement("if");
            String valueTest = String.format("list[0].%s != null", column.getJavaProperty());
            innerValueInfXmlElement.addAttribute(new Attribute("test", valueTest));
            String parameterClause = MyBatis3FormattingUtilities.getParameterClause(column);
            parameterClause = parameterClause.replace("#{", "#{listItem.");
            innerValueInfXmlElement.addElement(new TextElement(parameterClause + ","));
            trimValueXmlElement.addElement(innerValueInfXmlElement);
        }
        batchInsertSelectiveXml.addElement(trimIntoXmlElement);
        batchInsertSelectiveXml.addElement(foreachXmlElement);
        return batchInsertSelectiveXml;
    }


    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
