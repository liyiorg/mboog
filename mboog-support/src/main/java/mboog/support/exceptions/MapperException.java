package mboog.support.exceptions;

/**
 * @author LiYi
 */
public class MapperException extends AbstractException {

    private static final long serialVersionUID = 6345421071481243178L;

    public MapperException() {
        super();
    }

    public MapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public MapperException(String message) {
        super(message);
    }

    public MapperException(Throwable cause) {
        super(cause);
    }

}
