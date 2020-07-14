package mboog.generator.internal.types;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;
import org.mybatis.generator.internal.util.StringUtility;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

/**
 * @author LiYi
 */
public class JavaTypeResolverImpl extends JavaTypeResolverDefaultImpl {

    protected boolean jsr310;
    protected boolean tinyintToInteger;
    protected boolean smallintToInteger;

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        jsr310 = StringUtility.isTrue(properties.getProperty("jsr310", "true"));
        tinyintToInteger = StringUtility.isTrue(properties.getProperty("tinyintToInteger", "true"));
        smallintToInteger = StringUtility.isTrue(properties.getProperty("smallintToInteger", "true"));
    }

    @Override
    protected FullyQualifiedJavaType overrideDefaultType(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;

        switch (column.getJdbcType()) {
            case Types.BIT:
                answer = calculateBitReplacement(column, defaultType);
                break;
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE:
                answer = calculateJsr310Replacement(column, defaultType);
                break;
            case Types.TINYINT:
                answer = calculateTinyintReplacement(column, defaultType);
                break;
            case Types.SMALLINT:
                answer = calculateSmallintReplacement(column, defaultType);
                break;
            case Types.DECIMAL:
            case Types.NUMERIC:
                answer = calculateBigDecimalReplacement(column, defaultType);
                break;
            default:
                break;
        }

        return answer;
    }


    protected FullyQualifiedJavaType calculateJsr310Replacement(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        FullyQualifiedJavaType answer = defaultType;
        if (jsr310) {
            switch (column.getJdbcType()) {
                case Types.TIME:
                    answer = new FullyQualifiedJavaType(LocalTime.class.getName());
                    break;
                case Types.TIMESTAMP:
                    answer = new FullyQualifiedJavaType(LocalDateTime.class.getName());
                    break;
                case Types.DATE:
                    if (column.getLength() > 0) {
                        answer = new FullyQualifiedJavaType(LocalDate.class.getName());
                    } else {
                        // MySql type YEAR
                        answer = new FullyQualifiedJavaType(Integer.class.getName());
                    }
                    break;
                default:
                    break;
            }
        }
        return answer;
    }

    protected FullyQualifiedJavaType calculateTinyintReplacement(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        if (tinyintToInteger) {
            return new FullyQualifiedJavaType(Integer.class.getName());
        }
        return defaultType;
    }

    protected FullyQualifiedJavaType calculateSmallintReplacement(IntrospectedColumn column, FullyQualifiedJavaType defaultType) {
        if (smallintToInteger) {
            return new FullyQualifiedJavaType(Integer.class.getName());
        }
        return defaultType;
    }

}
