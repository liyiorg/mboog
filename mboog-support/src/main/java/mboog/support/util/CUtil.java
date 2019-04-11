package mboog.support.util;

import mboog.support.example.CInterface;
import mboog.support.example.ColumnListAble;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * C Enum util
 *
 * @author LiYi
 */
public abstract class CUtil {

    private static final Map<Class<? extends Enum<?>>, Set<CInterface>> C_DATA_MAP = new ConcurrentHashMap<>(128);

    private static final Comparator<CInterface> C_COMPARATOR = Comparator.comparing(CInterface::name);


    /**
     * 获取分组枚举项
     * @param clazz clazz
     * @param types 1,2,3
     * @param <E> CInterface Set
     * @return
     */
    public static <E extends Enum<E>> Set<CInterface> group(Class<E> clazz, int... types) {
        E[] es = clazz.getEnumConstants();
        Set<CInterface> set = null;
        for (E e : es) {
            if (e instanceof CInterface) {
                CInterface c = (CInterface) e;
                for (int type : types) {
                    if (c.getType() == type) {
                        if (set == null) {
                            set = new LinkedHashSet<>();
                        }
                        set.add(c);
                        break;
                    }
                }
            }
        }
        return set;
    }

    /**
     * 转换Enum Item 为 CInterface
     * @param clazz clazz
     * @param <E> CInterface
     * @return CInterface Set
     */
    public static <E extends Enum<E>> Set<CInterface> cInterfaces(Class<E> clazz) {
        E[] es = clazz.getEnumConstants();
        Set<CInterface> set = null;
        for (E e : es) {
            if (e instanceof CInterface) {
                CInterface c = (CInterface) e;
                if (set == null) {
                    set = new LinkedHashSet<>();
                }
                set.add(c);
            }
        }
        return set;
    }


    public static <C extends CInterface> String joinDelimitedNames(Collection<C> coll, boolean sort) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        Iterator<C> it;
        // sort
        if (sort) {
            List<C> list = new ArrayList<>(coll.size());
            list.addAll(coll);
            Collections.sort(list, C_COMPARATOR);
            it = list.iterator();
        } else {
            it = coll.iterator();
        }
        // join delimited names
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(it.next().includeColumnName());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static <M, E extends Enum<E>, C extends CInterface> void includeColumns(Class<E> clazz,
                                                                                   ColumnListAble<M, E, C> columnListAble, List<C> cs) {
        columnListAble.setColumnList(null);
        if (cs != null && cs.size() > 0) {
            Set<C> baseSet = new LinkedHashSet<>();
            for (C c : cs) {
                baseSet.add(c);
            }
            columnListAble.setColumnList(baseSet.isEmpty() ? null : joinDelimitedNames(baseSet, true));
        }
    }

    public static <M, E extends Enum<E>, C extends CInterface> void includeColumns(Class<E> clazz,
                                                                                   ColumnListAble<M, E, C> columnListAble, C[] cs) {
        includeColumns(clazz, columnListAble, cs == null ? new ArrayList<>() : Arrays.asList(cs));
    }

    public static <M, E extends Enum<E>, C extends CInterface> void excludeColumns(Class<E> clazz,
                                                                                   ColumnListAble<M, E, C> columnListAble, List<C> cs) {
        columnListAble.setColumnList(null);
        if (cs != null && cs.size() > 0) {
            if (!C_DATA_MAP.containsKey(clazz)) {
                C_DATA_MAP.put(clazz, cInterfaces(clazz));
            }
            Set<CInterface> baseSet = new LinkedHashSet<>();
            baseSet.addAll(C_DATA_MAP.get(clazz));
            for (C c : cs) {
                baseSet.remove(c);
            }
            columnListAble.setColumnList(baseSet.isEmpty() ? null : joinDelimitedNames(baseSet, true));
        }
    }

    public static <M, E extends Enum<E>, C extends CInterface> void excludeColumns(Class<E> clazz,
                                                                                   ColumnListAble<M, E, C> columnListAble, C[] cs) {
        excludeColumns(clazz, columnListAble, cs == null ? new ArrayList<>() : Arrays.asList(cs));
    }

}
