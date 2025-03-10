package cz.cuni.mff.vopalenf.annotator.exception.api;

import lombok.Getter;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.BAD_REQUEST;

/**
 * Exception to be thrown when client error happens during request
 */
@Getter
public class BadRequestException extends APIException {
    public BadRequestException(final String message, final String scope) {
        super(BAD_REQUEST, message, scope);
    }
}