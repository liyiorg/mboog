package mboog.generator.plugins;

import mboog.generator.internal.rules.MboogRulesDelegate;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Rules 声明
 * 移除BLOBS
 *
 * @author liyi
 */
public class RulesDelegatePlugin extends PluginAdapter {

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        MboogRulesDelegate mboogRulesDelegate = new MboogRulesDelegate(introspectedTable.getRules());
        introspectedTable.setRules(mboogRulesDelegate);
        if (StringUtility.stringHasValue(introspectedTable.getRecordWithBLOBsType())) {
            if (introspectedTable.hasBLOBColumns()) {
                introspectedTable.getBaseColumns().addAll(introspectedTable.getBLOBColumns());
                introspectedTable.getBLOBColumns().clear();
            }
        }
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        if (!"VIEW".equalsIgnoreCase(introspectedTable.getTableType()) && introspectedTable.getGeneratedKey() == null) {
            // 获取所有insert xml element
            Map<String, XmlElement> insertXmlElementMap = document.getRootElement().getElements().stream()
                    .filter(e -> e instanceof XmlElement).map(e -> (XmlElement) e)
                    .filter(e -> "insert".equals(e.getName()))
                    .collect(Collectors.toMap(n -> n
                                    .getAttributes().stream()
                                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue))
                                    .get("id"),
                            Function.identity()));

            // 重写 insert XML 片段
            XmlElement sqlInsertElement = new XmlElement("sql");
            sqlInsertElement.addAttribute(new Attribute("id", "Insert"));
            sqlInsertElement.getElements().addAll(new ArrayList<>(insertXmlElementMap.get("insert").getElements()));

            XmlElement refidInsertElement = new XmlElement("include");
            refidInsertElement.addAttribute(new Attribute("refid", "Insert"));
            insertXmlElementMap.get("insert").getElements().clear();
            context.getCommentGenerator().addComment(insertXmlElementMap.get("insert"));
            insertXmlElementMap.get("insert").getElements().add(refidInsertElement);


            // 重写 insertSelective XML 片段
            XmlElement sqlInsertSelectiveElement = new XmlElement("sql");
            sqlInsertSelectiveElement.addAttribute(new Attribute("id", "InsertSelective"));
            sqlInsertSelectiveElement.getElements().addAll(new ArrayList<>(insertXmlElementMap.get("insertSelective").getElements()));

            XmlElement refidInsertSelectiveElement = new XmlElement("include");
            refidInsertSelectiveElement.addAttribute(new Attribute("refid", "InsertSelective"));
            insertXmlElementMap.get("insertSelective").getElements().clear();
            context.getCommentGenerator().addComment(insertXmlElementMap.get("insertSelective"));
            insertXmlElementMap.get("insertSelective").getElements().add(refidInsertSelectiveElement);

            int index = 0;
            for (int i = 0; i < document.getRootElement().getElements().size(); i++) {
                Element element = document.getRootElement().getElements().get(i);
                if (element instanceof XmlElement) {
                    XmlElement xmlElement = (XmlElement) element;
                    if (xmlElement.getName().equals("select")) {
                        index = i;
                        break;
                    }
                }
            }
            document.getRootElement().addElement(index++, sqlInsertElement);
            document.getRootElement().addElement(index, sqlInsertSelectiveElement);

        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
