package cz.cuni.mff.vopalenf.annotator.exception;

/**
 * Exception denoting problems with storage.
 */
public class StorageException extends RuntimeException {

    /**
     * Constructs a new StorageException with the specified message.
     *
     * @param message the detail message
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new StorageException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
