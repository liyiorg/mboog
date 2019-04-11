package mboog.generator.util;

/**
 * @author LiYi
 */
public abstract class MBGStringUtil {

    /**
     * 获取类simple name
     *
     * @param fullClassName fullClassName
     * @return String
     */
    public static String shortClassName(String fullClassName) {
        if (fullClassName != null) {
            return fullClassName.replaceAll("(.*\\.)+(.*)", "$2");
        }
        return fullClassName;
    }
}
