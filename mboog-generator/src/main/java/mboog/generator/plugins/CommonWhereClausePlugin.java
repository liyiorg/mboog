package mboog.generator.plugins;

import java.util.List;
import java.util.Optional;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Where Clause
 * @author LiYi
 */
public class CommonWhereClausePlugin extends PluginAdapter {

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement sqlElement = new XmlElement("sql");
        sqlElement.addAttribute(new Attribute("id", "Common_Where_Clause"));
        XmlElement ifElement = new XmlElement("if");
        ifElement.addAttribute(new Attribute("test", "criteria.valid"));
        XmlElement trimElement = new XmlElement("trim");
        trimElement.addAttribute(new Attribute("prefix", "("));
        trimElement.addAttribute(new Attribute("prefixOverrides", "and|or"));
        trimElement.addAttribute(new Attribute("suffix", ")"));
        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "criteria.criteria"));
        foreachElement.addAttribute(new Attribute("item", "criterion"));
        XmlElement chooseElement = new XmlElement("choose");

        // noValue
        XmlElement whenElement_1 = new XmlElement("when");
        whenElement_1.addAttribute(new Attribute("test", "criterion.noValue"));
        whenElement_1.addElement(new TextElement("${criterion.prefix} ${criterion.condition}"));
        chooseElement.addElement(whenElement_1);

        // singleValue
        XmlElement whenElement_2 = new XmlElement("when");
        whenElement_2.addAttribute(new Attribute("test", "criterion.singleValue"));
        whenElement_2.addElement(new TextElement("${criterion.prefix} ${criterion.condition} #{criterion.value}"));
        chooseElement.addElement(whenElement_2);

        // betweenValue
        XmlElement whenElement_3 = new XmlElement("when");
        whenElement_3.addAttribute(new Attribute("test", "criterion.betweenValue"));
        whenElement_3.addElement(
                new TextElement("${criterion.prefix} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}"));
        chooseElement.addElement(whenElement_3);

        // listValue
        XmlElement whenElement_4 = new XmlElement("when");
        whenElement_4.addAttribute(new Attribute("test", "criterion.listValue"));
        whenElement_4.addElement(new TextElement("${criterion.prefix} ${criterion.condition}"));
        XmlElement innerForeachElement = new XmlElement("foreach");
        innerForeachElement.addAttribute(new Attribute("close", ")"));
        innerForeachElement.addAttribute(new Attribute("collection", "criterion.value"));
        innerForeachElement.addAttribute(new Attribute("item", "listItem"));
        innerForeachElement.addAttribute(new Attribute("open", "("));
        innerForeachElement.addAttribute(new Attribute("separator", ","));
        innerForeachElement.addElement(new TextElement("#{listItem}"));
        whenElement_4.addElement(innerForeachElement);
        chooseElement.addElement(whenElement_4);

        foreachElement.addElement(chooseElement);
        trimElement.addElement(foreachElement);
        ifElement.addElement(new TextElement("${criteria.prefix}"));
        ifElement.addElement(trimElement);
        context.getCommentGenerator().addComment(sqlElement);
        sqlElement.getElements().add(ifElement);

        //调整 xml Common_Where_Clause 的输出位置
        List<Element> elements = document.getRootElement().getElements();
        boolean finded = false;
        for (int i = 0; i < elements.size(); i++) {
            if (finded) {
                break;
            }
            Element e = elements.get(i);
            if (e instanceof XmlElement) {
                XmlElement ex = (XmlElement) e;
                for (Attribute a : ex.getAttributes()) {
                    if ("id".equals(a.getName()) && (
                            "Example_Where_Clause".equals(a.getValue())
                                    || "Update_By_Example_Where_Clause".equals(a.getValue()))) {
                        document.getRootElement().addElement(i, sqlElement);
                        finded = true;
                    }
                }
            }
        }

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getElements().stream()
                .filter(e -> e instanceof XmlElement)
                .map(e -> (XmlElement) e)
                .filter(e -> "where".equals(e.getName()))
                .peek(e -> {
                    Optional<XmlElement> optional = e.getElements().stream()
                            .filter(s -> s instanceof XmlElement)
                            .map(s -> (XmlElement) s)
                            .filter(s -> "foreach".equals(s.getName())).findAny();
                    optional.ifPresent(p -> {
                        XmlElement includeElement = new XmlElement("include");
                        Attribute attribute = new Attribute("refid", "Common_Where_Clause");
                        includeElement.addAttribute(attribute);
                        //删除foreach 中的 separator 属性
                        for (Attribute a : p.getAttributes()) {
                            if ("separator".equals(a.getName())) {
                                p.getAttributes().remove(a);
                                break;
                            }
                        }
                        p.getElements().clear();
                        p.getElements().add(includeElement);
                    });
                }).findAny();
        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

}
