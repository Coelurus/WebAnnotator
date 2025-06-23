package cz.cuni.mff.vopalenf.annotator.exception.api;

import lombok.Getter;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.DATA_NOT_FOUND;

/**
 * Exception to be thrown when nonexistent data is requested
 */
@Getter
public class NotFoundException extends APIException {
    /**
     * Constructs a new NotFoundException with the specified message and scope.
     *
     * @param message
     *            the detail message
     * @param scope
     *            the scope of the error
     */
    public NotFoundException(final String message, final String scope) {
        super(DATA_NOT_FOUND, message, scope);
    }
}