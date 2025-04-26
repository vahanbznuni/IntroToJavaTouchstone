/*
 * Custom Exception to signal attempts to entry duplicate items into the inventory
 */
public class CorruptDataException extends Exception {
    public CorruptDataException() {
        super();
    }

    public CorruptDataException(String message) {
        super(message);
    }

    public CorruptDataException(Throwable cause) {
        super(cause);
    }

    public CorruptDataException(String message, Throwable cause) {
        super(message, cause);
    }
}