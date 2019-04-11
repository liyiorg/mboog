package mboog.generator.plugins;

import mboog.generator.internal.rules.MboogRulesDelegate;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

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
            introspectedTable.setRecordWithBLOBsType(introspectedTable.getBaseRecordType());
        }
    }

    /**
     * 修改BaseResultMap 添加BLOB 字段
     *
     * @param element element
     * @param introspectedTable introspectedTable
     * @return boolean
     */
    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        if (introspectedTable.hasBLOBColumns()) {
            introspectedTable.getBLOBColumns().forEach(n -> {
                XmlElement result = new XmlElement("result");
                result.addAttribute(new Attribute("column", MyBatis3FormattingUtilities.getRenamedColumnNameForResultMap(n)));
                result.addAttribute(new Attribute("jdbcType", n.getJdbcTypeName()));
                result.addAttribute(new Attribute("property", n.getJavaProperty()));
                element.addElement(result);
            });
        }
        return super.sqlMapResultMapWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
