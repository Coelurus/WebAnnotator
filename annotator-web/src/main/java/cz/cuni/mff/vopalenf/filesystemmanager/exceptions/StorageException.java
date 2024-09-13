package cz.cuni.mff.vopalenf.filesystemmanager.exceptions;

/**
 * Exception denoting problems with storage.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
