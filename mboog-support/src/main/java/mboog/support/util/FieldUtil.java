package mboog.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author LiYi
 */
public abstract class FieldUtil {

    /**
     * 设置field 值
     *
     * @param target    target
     * @param fieldName fieldName
     * @param value     value
     * @throws IllegalAccessException
     */
    public static void writeDeclaredField(Object target, String fieldName, Object value)
            throws IllegalAccessException {
        if (target == null) {
            throw new IllegalArgumentException("target object must not be null");
        }
        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Cannot locate declared field " + cls.getName() + "." + fieldName);
        }
        field.set(target, value);
    }

    /**
     * 读取Field 值
     *
     * @param target    target
     * @param fieldName fieldName
     * @return Object
     * @throws IllegalAccessException IllegalAccessException
     */
    public static Object readField(Object target, String fieldName) throws IllegalAccessException {
        if (target == null) {
            throw new IllegalArgumentException("target object must not be null");
        }
        Class<?> cls = target.getClass();
        Field field = getField(cls, fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Cannot locate field " + fieldName + " on " + cls);
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(target);
    }

    /**
     * 获取Field
     *
     * @param cls       cls
     * @param fieldName fieldName
     * @return Field
     */
    public static Field getField(final Class<?> cls, String fieldName) {
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                Field field = acls.getDeclaredField(fieldName);
                if (!Modifier.isPublic(field.getModifiers())) {
                    field.setAccessible(true);
                    return field;
                }
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace();
                // ignore
            }
        }
        return null;
    }
}