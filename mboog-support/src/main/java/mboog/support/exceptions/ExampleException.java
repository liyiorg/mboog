package mboog.support.exceptions;

/**
 * @author LiYi
 */
public class ExampleException extends AbstractException {

    private static final long serialVersionUID = 6117517996762691536L;

    public ExampleException() {
        super();
    }

    public ExampleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExampleException(String message) {
        super(message);
    }

    public ExampleException(Throwable cause) {
        super(cause);
    }

}
