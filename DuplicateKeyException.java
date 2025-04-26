/*
 * Custom Exception to signal attempts to entry duplicate items into the inventory
 */
public class DuplicateKeyException extends Exception {
    public DuplicateKeyException() {
        super();
    }

    public DuplicateKeyException(String message) {
        super(message);
    }

    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }

    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}