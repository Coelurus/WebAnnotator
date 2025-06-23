package cz.cuni.mff.vopalenf.annotator.exception.api;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.RUNTIME_EXCEPTION;

/**
 * Exception to be thrown when a server error occurs
 */
public class ServerException extends APIException {
    /**
     * Constructs a new ServerException with the specified message and scope.
     *
     * @param message   the detail message
     * @param scope     the scope of the error
     */
    public ServerException(final String message, final String scope) {
        super(RUNTIME_EXCEPTION, message, scope);
    }
}
