package mboog.support.bean;

/**
 * @author LiYi
 */
public class SqlImprovement {

    public static final int MYSQL_REPLACE = 1;

    public static final int MYSQL_ON_DUPLICATE_KEY_UPDATE = 2;

    public static final int POSTGRESQL_ON_CONFLICT_DO_UPDATE = 3;

    public static final int POSTGRESQL_ON_CONFLICT_DO_NOTHING = 4;

    public static final int ORACLE_MERGE_INTO_USING_DUAL = 5;
}
