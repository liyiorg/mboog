package mboog.generator.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Set;

/**
 * ColumnListPlugin 适用于<br>
 * <p>
 * dependency plugins <br>
 * <code>ExampleCPlugin</code>
 * <p>
 * Mapper.selectByExample(Example) <br>
 * Mapper.selectByExampleWithBLOBs(Example)
 *
 * @author LiYi
 */
public class ColumnListPlugin extends PluginAdapter {

    private static final String COLUMN_LIST_ABLE_CLASS = PluginsConstants.BASE_SUPPORT_PACKAGE + ".example.ColumnListAble";

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 添加import
        topLevelClass.addImportedType(COLUMN_LIST_ABLE_CLASS);
        topLevelClass.addImportedType(Set.class.getName());
        topLevelClass.addImportedType(introspectedTable.getExampleType() + ".C");

        topLevelClass.addSuperInterface(new FullyQualifiedJavaType(
                String.format("%s<%s, C, C>", COLUMN_LIST_ABLE_CLASS, introspectedTable.getExampleType())));
        for (Method method : topLevelClass.getMethods()) {
            if (method.isConstructor() && (method.getParameters() == null || method.getParameters().size() == 0)) {
                method.addBodyLine("initColumnList(C.class);");
                break;
            }
        }

        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        int base_Column_List_index = 0;
        List<Element> elements = element.getElements();
        for (int i = 0; i < elements.size(); i++) {
            Element e = element.getElements().get(i);
            if (e instanceof XmlElement) {
                XmlElement exml = (XmlElement) e;
                if ("include".equals(exml.getName())) {
                    for (Attribute attribute : exml.getAttributes()) {
                        if ("refid".equals(attribute.getName()) && "Base_Column_List".equals(attribute.getValue())) {
                            base_Column_List_index = i;
                            break;
                        }
                    }
                }
            }
        }

        if (base_Column_List_index != 0) {
            XmlElement when = new XmlElement("when");
            when.addAttribute(new Attribute("test", "columnList != null"));
            when.addElement(new TextElement("${columnList}"));
            XmlElement otherwise = new XmlElement("otherwise");
            otherwise.addElement(elements.get(base_Column_List_index));
            if (introspectedTable.hasBLOBColumns()) {
                otherwise.addElement(new TextElement(","));
                XmlElement include = new XmlElement("include");
                include.addAttribute(new Attribute("refid", "Blob_Column_List"));
                otherwise.addElement(include);
            }
            XmlElement chooseXMLElement = new XmlElement("choose");
            chooseXMLElement.addElement(when);
            chooseXMLElement.addElement(otherwise);

            // 替换<include refid="Base_Column_List" /> 为 <choose>
            elements.remove(base_Column_List_index);
            elements.add(base_Column_List_index, chooseXMLElement);
        }
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

}
