package cz.cuni.mff.vopalenf.filesystemmanager.exception;

/**
 * Exception denoting problem with locating file
 */
public class StorageFileNotFoundException extends StorageException {

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}