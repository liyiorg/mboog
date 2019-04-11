package mboog.generator.plugins.upsert;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author LiYi
 */
public class MySQLUpsertPlugin extends AbstractUpsertPlugin {

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (canUpsert) {
            XmlElement sqlInsertElement = new XmlElement("sql");
            sqlInsertElement.addAttribute(new Attribute("id", "Insert"));

            XmlElement refidInsertElement = new XmlElement("include");
            refidInsertElement.addAttribute(new Attribute("refid", "Insert"));

            XmlElement sqlInsertSelectiveElement = new XmlElement("sql");
            sqlInsertSelectiveElement.addAttribute(new Attribute("id", "InsertSelective"));

            XmlElement refidInsertSelectiveElement = new XmlElement("include");
            refidInsertSelectiveElement.addAttribute(new Attribute("refid", "InsertSelective"));

            // 获取所有insert xml element
            Map<String, XmlElement> insertXmlElementMap = document.getRootElement().getElements().stream()
                    .filter(e -> e instanceof XmlElement).map(e -> (XmlElement) e)
                    .filter(e -> "insert".equals(e.getName()))
                    .collect(Collectors.toMap(n -> n
                                    .getAttributes().stream()
                                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue))
                                    .get("id"),
                            Function.identity()));
            int index = 0;
            for (int i = 0; i < document.getRootElement().getElements().size(); i++) {
                Element element = document.getRootElement().getElements().get(i);
                if (element instanceof XmlElement) {
                    XmlElement xmlElement = (XmlElement) element;
                    if (xmlElement.getName().equals("insert")) {
                        index = i;
                        break;
                    }
                }
            }

            // 重构 insert XML
            XmlElement insertXml = insertXmlElementMap.get("insert");
            document.getRootElement().addElement(index++, sqlInsertElement);
            insertXml.getElements().forEach(e -> sqlInsertElement.addElement(e));
            insertXml.getElements().clear();
            context.getCommentGenerator().addComment(insertXml);
            insertXml.getElements().add(refidInsertElement);

            // 重构 insertSelective XML
            document.getRootElement().addElement(++index, sqlInsertSelectiveElement);
            XmlElement insertSelectiveXml = insertXmlElementMap.get("insertSelective");
            insertSelectiveXml.getElements().forEach(e -> sqlInsertSelectiveElement.addElement(e));
            insertSelectiveXml.getElements().clear();
            context.getCommentGenerator().addComment(insertSelectiveXml);
            insertSelectiveXml.getElements().add(refidInsertSelectiveElement);

            List<IntrospectedColumn> columns = introspectedTable.getNonPrimaryKeyColumns();

            // 生成 upsert XML
            XmlElement sqlUpsertElement = new XmlElement("insert");
            sqlUpsertElement.addAttribute(new Attribute("id", "upsert"));
            sqlUpsertElement.addAttribute(new Attribute("parameterType", parameterType));
            context.getCommentGenerator().addComment(sqlUpsertElement);
            sqlUpsertElement.addElement(refidInsertElement);
            sqlUpsertElement.addElement(new TextElement("on duplicate key update"));
            StringBuilder stringBuilder;
            for (int i = 0; i < columns.size(); i += 3) {
                stringBuilder = new StringBuilder();
                for (int x = 0; x < 3 && i + x < columns.size(); x++) {
                    IntrospectedColumn column = columns.get(i + x);
                    stringBuilder.append(String.format("%s = %s",
                            MyBatis3FormattingUtilities.getEscapedColumnName(column),
                            MyBatis3FormattingUtilities.getParameterClause(column)));
                    if (i + x < columns.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                sqlUpsertElement.addElement(new TextElement(stringBuilder.toString()));
            }
            document.getRootElement().addElement(index += 2, sqlUpsertElement);

            // 生成 upsertSelective XML
            XmlElement sqlUpsertSelectiveElement = new XmlElement("insert");
            sqlUpsertSelectiveElement.addAttribute(new Attribute("id", "upsertSelective"));
            sqlUpsertSelectiveElement.addAttribute(new Attribute("parameterType", parameterType));
            context.getCommentGenerator().addComment(sqlUpsertSelectiveElement);
            sqlUpsertSelectiveElement.addElement(refidInsertSelectiveElement);
            sqlUpsertSelectiveElement.addElement(new TextElement("on duplicate key update"));
            XmlElement trim = new XmlElement("trim");
            trim.addAttribute(new Attribute("suffixOverrides", ","));
            sqlUpsertSelectiveElement.addElement(trim);

            for (int i = 0; i < columns.size(); i++) {
                IntrospectedColumn column = columns.get(i);
                XmlElement ifElement = new XmlElement("if");
                ifElement.addAttribute(new Attribute("test", column.getJavaProperty() + " != null"));
                ifElement.addElement(new TextElement(String.format("%s = %s,",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getParameterClause(column))));
                trim.addElement(ifElement);
            }
            document.getRootElement().addElement(++index, sqlUpsertSelectiveElement);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }


}
