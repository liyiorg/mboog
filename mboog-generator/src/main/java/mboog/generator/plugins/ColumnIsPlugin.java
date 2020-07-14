package mboog.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

/**
 *
 * 匹配表字段  is_xx 布尔类型格式
 * @author LiYi
 *
 */
public class ColumnIsPlugin extends PluginAdapter {

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
			String columnName = introspectedColumn.getActualColumnName();
			String javaProperty = introspectedColumn.getJavaProperty();
			if (columnName.toLowerCase().startsWith("is_") && columnName.length() > 3) {
				if (javaProperty.toLowerCase().startsWith("is_") && javaProperty.length() > 3) {
					String tempProperty = javaProperty.substring(3, 4).toLowerCase();
					if (javaProperty.length() > 4) {
						tempProperty += javaProperty.substring(4);
					}
					introspectedColumn.setJavaProperty(tempProperty);
				} else if (javaProperty.toLowerCase().startsWith("is") && javaProperty.length() > 2) {
					String tempProperty = javaProperty.substring(2, 3).toLowerCase();
					if (javaProperty.length() > 3) {
						tempProperty += javaProperty.substring(3);
					}
					introspectedColumn.setJavaProperty(tempProperty);
				}
			}
		}
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

}

