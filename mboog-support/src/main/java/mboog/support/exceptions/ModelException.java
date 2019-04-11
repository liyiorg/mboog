package mboog.support.exceptions;

/**
 * @author LiYi
 */
public class ModelException extends AbstractException {

    private static final long serialVersionUID = 3570142460976540675L;

    public ModelException() {
        super();
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelException(String message) {
        super(message);
    }

    public ModelException(Throwable cause) {
        super(cause);
    }

}
