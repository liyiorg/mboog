package mboog.support.exceptions;

/**
 * @author LiYi
 */
public class BatchException extends AbstractException {

    private static final long serialVersionUID = 5034235211937185364L;

    public BatchException() {
        super();
    }

    public BatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchException(String message) {
        super(message);
    }

    public BatchException(Throwable cause) {
        super(cause);
    }

}
