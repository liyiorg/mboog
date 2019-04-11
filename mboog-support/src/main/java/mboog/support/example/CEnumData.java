package mboog.support.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiYi
 */
class CEnumData {

    private static final Map<CInterface, CItem> C_INTERFACE_MAP = new ConcurrentHashMap<>(2048);

    private static final Map<Class<?>, Class<?>> COLUMN_LISTABLE_MAP = new ConcurrentHashMap<>(128);

    static void putCItem(CInterface cInterface, CItem cItem) {
        C_INTERFACE_MAP.put(cInterface, cItem);
    }

    static CItem getCItem(CInterface cInterface) {
        return C_INTERFACE_MAP.get(cInterface);
    }

    static void putColumnListable(Class<?> classA, Class<?> classB) {
        COLUMN_LISTABLE_MAP.put(classA, classB);
    }

    static <T> T getColumnListable(Class<?> classA) {
        return (T) COLUMN_LISTABLE_MAP.get(classA);
    }

    static boolean existColumnListable(Class<?> classA) {
        return COLUMN_LISTABLE_MAP.containsKey(classA);
    }

}
