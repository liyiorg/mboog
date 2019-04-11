package mboog.support.exceptions;

/**
 * @author LiYi
 */
public class NoKeyException extends AbstractException {

    private static final long serialVersionUID = -6994353941424702174L;

    public NoKeyException() {
        super();
    }

    public NoKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoKeyException(String message) {
        super(message);
    }

    public NoKeyException(Throwable cause) {
        super(cause);
    }

}
