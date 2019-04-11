package mboog.generator.plugins.pagination;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * @author LiYi
 */
public class OraclePaginationPlugin extends AbstractPaginationPlugin {

    @Override
    public String getDataBaseType() {
        return "Oracle";
    }

    @Override
    public boolean sqlMapExampleWhereClauseElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        for (Attribute attribute : element.getAttributes()) {
            // 确定进入 Example_Where_Clause XML
            if ("id".equals(attribute.getName()) && introspectedTable.getExampleWhereClauseId().equals(attribute.getValue())) {
                for (Element e : element.getElements()) {
                    if (e instanceof XmlElement) {
                        XmlElement exml = (XmlElement) e;
                        // 在where 内部添加条件块
                        if ("where".equals(exml.getName())) {
                            try {
                                XmlElement rownum = new XmlElement("if");
                                rownum.addAttribute(
                                        new Attribute("test", "limitEnd != null and orderByClause == null"));
                                rownum.addElement(new TextElement("and ROWNUM &lt; ${limitEnd}"));
                                exml.getElements().add(rownum);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }

        return super.sqlMapExampleWhereClauseElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        builderXML(element);
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    /**
     * 生成XML
     *
     * @param element
     */
    private void builderXML(XmlElement element) {
        try {
            // 获取备注
            List<Element> comments = new ArrayList<Element>();
            for (Element e : element.getElements()) {
                comments.add(e);
                if ("-->".equals(e.getFormattedContent(0))) {
                    break;
                }
            }

            // 无分页
            XmlElement when1 = new XmlElement("when");
            when1.addAttribute(new Attribute("test", "limitStart == null"));
            for (int i = comments.size(); i < element.getElements().size(); i++) {
                Element e = element.getElements().get(i);
                when1.addElement(e);
            }

            // 有分页，无排序
            XmlElement when2 = new XmlElement("when");
            when2.addAttribute(new Attribute("test", "limitStart != null and orderByClause == null"));
            when2.addElement(new TextElement("select * from(\n"));
            //排除最后的 order by 条件
            for (int i = comments.size(); i < element.getElements().size() - 1; i++) {
                Element e = element.getElements().get(i);
                if (e instanceof TextElement) {
                    TextElement etemp = (TextElement) e;
                    if (etemp.getContent().startsWith("from ")) {
                        when2.addElement(new TextElement(","));
                        when2.addElement(new TextElement("ROWNUM as rowno"));
                    }
                }
                when2.addElement(e);
            }
            when2.addElement(new TextElement(""));
            when2.addElement(new TextElement(") table_alias"));
            when2.addElement(new TextElement("where"));
            when2.addElement(new TextElement("\ttable_alias.rowno &gt;= ${limitStart}"));

            // 有分页，有排序
            XmlElement when3 = new XmlElement("when");
            when3.addAttribute(new Attribute("test", "limitStart != null and orderByClause != null"));
            when3.addElement(new TextElement("select * from ("));
            when3.addElement(new TextElement("\tselect table_warp_alias.*, ROWNUM as rowno from("));
            when3.addElement(new TextElement(""));
            for (int i = comments.size(); i < element.getElements().size(); i++) {
                Element e = element.getElements().get(i);
                when3.addElement(e);
            }
            when3.addElement(new TextElement(""));
            when3.addElement(new TextElement("\t) table_warp_alias"));
            when3.addElement(new TextElement("\twhere"));
            when3.addElement(new TextElement("\t\tROWNUM &lt; ${limitEnd}"));
            when3.addElement(new TextElement(") table_alias"));
            when3.addElement(new TextElement("where"));
            when3.addElement(new TextElement("\ttable_alias.rowno &gt;= ${limitStart}"));

            XmlElement chooseXMLElement = new XmlElement("choose");
            chooseXMLElement.addElement(when1);
            chooseXMLElement.addElement(when2);
            chooseXMLElement.addElement(when3);

            Field field = element.getClass().getDeclaredField("elements");
            field.setAccessible(true);
            // 清空内部element
            field.set(element, new ArrayList<Element>());
            // 设置备注
            for (Element e : comments) {
                element.addElement(e);
            }
            // 设置 chooseXMLElement
            element.addElement(chooseXMLElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

}
