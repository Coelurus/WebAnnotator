package cz.cuni.mff.vopalenf.annotator.exception.api;

import lombok.Getter;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.BAD_CREDENTIALS;

/**
 * Exception to be thrown when invalid credentials are inputed
 */
@Getter
public class BadCredentialsException extends APIException {
    public BadCredentialsException(final String message, final String scope) {
        super(BAD_CREDENTIALS, message, scope);
    }
}
