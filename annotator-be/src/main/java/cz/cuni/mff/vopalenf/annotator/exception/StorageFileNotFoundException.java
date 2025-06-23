package cz.cuni.mff.vopalenf.annotator.exception;

/**
 * Exception denoting problem with locating file
 */
public class StorageFileNotFoundException extends StorageException {

    /**
     * Constructs a new StorageFileNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public StorageFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new StorageFileNotFoundException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}