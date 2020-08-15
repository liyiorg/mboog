package mboog.generator.plugins.upsert;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 * @author LiYi
 */
public class MySQLUpsertPlugin extends AbstractUpsertPlugin {


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (canUpsert) {
            int index = 0;
            for (int i = 0; i < document.getRootElement().getElements().size(); i++) {
                Element element = document.getRootElement().getElements().get(i);
                if (element instanceof XmlElement) {
                    XmlElement xmlElement = (XmlElement) element;
                    if ("insert".equals(xmlElement.getName())) {
                        index = i + 2;
                        break;
                    }
                }
            }

            List<IntrospectedColumn> columns = introspectedTable.getNonPrimaryKeyColumns();

            // 生成 upsert XML
            XmlElement sqlUpsertElement = new XmlElement("insert");
            sqlUpsertElement.addAttribute(new Attribute("id", "upsert"));
            sqlUpsertElement.addAttribute(new Attribute("parameterType", parameterType));
            context.getCommentGenerator().addComment(sqlUpsertElement);
            XmlElement refidInsertElement = new XmlElement("include");
            refidInsertElement.addAttribute(new Attribute("refid", "Insert"));
            sqlUpsertElement.addElement(refidInsertElement);
            sqlUpsertElement.addElement(new TextElement("on duplicate key update"));
            StringBuilder stringBuilder;
            for (int i = 0; i < columns.size(); i += 3) {
                stringBuilder = new StringBuilder();
                for (int x = 0; x < 3 && i + x < columns.size(); x++) {
                    IntrospectedColumn column = columns.get(i + x);
                    stringBuilder.append(String.format("%s = values(%s)",
                            MyBatis3FormattingUtilities.getEscapedColumnName(column),
                            MyBatis3FormattingUtilities.getEscapedColumnName(column)));
                    if (i + x < columns.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                sqlUpsertElement.addElement(new TextElement(stringBuilder.toString()));
            }
            document.getRootElement().addElement(index++, sqlUpsertElement);

            // 生成 upsertSelective XML
            XmlElement sqlUpsertSelectiveElement = new XmlElement("insert");
            sqlUpsertSelectiveElement.addAttribute(new Attribute("id", "upsertSelective"));
            sqlUpsertSelectiveElement.addAttribute(new Attribute("parameterType", parameterType));
            context.getCommentGenerator().addComment(sqlUpsertSelectiveElement);
            XmlElement refidInsertSelectiveElement = new XmlElement("include");
            refidInsertSelectiveElement.addAttribute(new Attribute("refid", "InsertSelective"));
            sqlUpsertSelectiveElement.addElement(refidInsertSelectiveElement);
            sqlUpsertSelectiveElement.addElement(new TextElement("on duplicate key update"));
            XmlElement trim = new XmlElement("trim");
            trim.addAttribute(new Attribute("suffixOverrides", ","));
            sqlUpsertSelectiveElement.addElement(trim);

            for (int i = 0; i < columns.size(); i++) {
                IntrospectedColumn column = columns.get(i);
                XmlElement ifElement = new XmlElement("if");
                ifElement.addAttribute(new Attribute("test", column.getJavaProperty() + " != null"));
                ifElement.addElement(new TextElement(String.format("%s = values(%s),",
                        MyBatis3FormattingUtilities.getEscapedColumnName(column),
                        MyBatis3FormattingUtilities.getEscapedColumnName(column))));
                trim.addElement(ifElement);
            }
            document.getRootElement().addElement(index, sqlUpsertSelectiveElement);
        }
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

}
