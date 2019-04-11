package mboog.generator.plugins;

import mboog.generator.util.MBGFileUtil;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author LiYi
 */
public class CachePlugin extends PluginAdapter {

    private static String CacheRefXMLName = "cache-ref";
    private static String CacheRefNamespaceAttributeName = "namespace";
    private static String CacheXMLName = "cache";
    private static String CachePropertyName = "cache";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        // delete cache-ref
        deleteCacheRefXml(introspectedTable);
        String cache = introspectedTable.getTableConfigurationProperty(CachePropertyName);
        // No Cache
        if (StringUtility.stringHasValue(cache)) {
            if ("false".equals(cache)) {
                deleteXmlElement(document, CacheXMLName);
            } else if (!StringUtility.isTrue(cache)) {
                // 使用名称
                if (cache.indexOf(".") == -1) {
                    cache = introspectedTable.getMyBatis3SqlMapNamespace().replaceFirst("\\.\\w+$", ".") + cache;
                }
                introspectedTable.getMyBatis3SqlMapNamespace();
                XmlElement xmlElement_cacheRef = new XmlElement(CacheRefXMLName);
                xmlElement_cacheRef.addAttribute(new Attribute(CacheRefNamespaceAttributeName, cache));
                document.getRootElement().addElement(0, xmlElement_cacheRef);
                // context.getCommentGenerator().addComment(xmlElement_cacheRef);
                deleteXmlElement(document, CacheXMLName);
            }
        }
        sortXmlElement(document, CacheXMLName, 0);
        return true;
    }

    /**
     * Delete XML Element
     *
     * @param document document
     * @param name     tagName
     */
    private void deleteXmlElement(Document document, String name) {
        for (Element element : document.getRootElement().getElements()) {
            if (element instanceof XmlElement) {
                XmlElement tempElement = (XmlElement) element;
                if (tempElement.getName().equals(name)) {
                    document.getRootElement().getElements().remove(element);
                    break;
                }
            }
        }
    }

    /**
     * 排序
     *
     * @param document
     * @param name
     * @param index
     */
    private void sortXmlElement(Document document, String name, int index) {
        for (Element element : document.getRootElement().getElements()) {
            if (element instanceof XmlElement) {
                XmlElement tempElement = (XmlElement) element;
                if (tempElement.getName().equals(name)) {
                    document.getRootElement().getElements().remove(element);
                    document.getRootElement().getElements().add(index, element);
                    break;
                }
            }
        }
    }


    /**
     * 删除 cache-ref 标签，规避mybatis DTD cache-ref 不能添加MBG 注释问题
     *
     * @param introspectedTable
     * @return
     */
    private boolean deleteCacheRefXml(IntrospectedTable introspectedTable) {
        File mapperXmlFile = MBGFileUtil.getFile("/src/main/resources/",
                introspectedTable.getMyBatis3SqlMapNamespace().replaceAll("\\.", "/") + ".xml");
        if (mapperXmlFile.exists()) {
            try {
                String xml = MBGFileUtil.copyToString(new FileInputStream(mapperXmlFile), Charset.forName("utf-8"));
                if (xml.indexOf("<cache-ref ") != -1) {
                    xml = xml.replaceAll("\\s*<cache-ref\\s+namespace=\"[^\"]*\"\\s*((/>)|(\\s*></cache-ref>))\\s*",
                            System.lineSeparator());
                    MBGFileUtil.copy(xml, Charset.forName("utf-8"), new FileOutputStream(mapperXmlFile));
                    return true;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
