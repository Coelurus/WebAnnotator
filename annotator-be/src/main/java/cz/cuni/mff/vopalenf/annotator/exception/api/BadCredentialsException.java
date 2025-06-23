package cz.cuni.mff.vopalenf.annotator.exception.api;

import lombok.Getter;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.BAD_CREDENTIALS;

/**
 * Exception to be thrown when invalid credentials are inputed
 */
@Getter
public class BadCredentialsException extends APIException {
    /**
     * Constructs a new BadCredentialsException with the specified message and
     * scope.
     *
     * @param message
     *            the detail message
     * @param scope
     *            the scope of the error
     */
    public BadCredentialsException(final String message, final String scope) {
        super(BAD_CREDENTIALS, message, scope);
    }
}
