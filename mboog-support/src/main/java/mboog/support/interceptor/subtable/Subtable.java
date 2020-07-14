package mboog.support.interceptor.subtable;

/**
 * Sub table
 *
 * @author LiYi
 */
public class Subtable {

    private static ThreadLocal<Sub> threadLocal = new ThreadLocal<Sub>();

    public static final Integer TYPE_APPEND = 1;

    public static final Integer TYPE_LAST_REPLACE = 2;

    public static final Integer TYPE_FULL_REPLACE = 3;

    /**
     * start sub table
     *
     * @param subtableName sub table name
     * @param type         [1 append ,2 last_replace ,3 full_replace]
     */
    public static void subtable(String subtableName, int type) {
        Sub sub = new Sub();
        sub.setName(subtableName);
        sub.setType(type);
        threadLocal.set(sub);
    }

    public static void clear() {
        threadLocal.remove();
    }

    protected static Sub get() {
        return threadLocal.get();
    }

    protected static class Sub {

        private String name; // 表名称

        private int type; // 1,2,3

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }
}
