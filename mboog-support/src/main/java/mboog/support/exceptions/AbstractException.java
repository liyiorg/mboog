package mboog.support.exceptions;

import org.apache.ibatis.exceptions.PersistenceException;

/**
 * @author LiYi
 */
public class AbstractException extends PersistenceException {

    private static final long serialVersionUID = -1087064438395590672L;

    public AbstractException() {
        super();
    }

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractException(Throwable cause) {
        super(cause);
    }

}
