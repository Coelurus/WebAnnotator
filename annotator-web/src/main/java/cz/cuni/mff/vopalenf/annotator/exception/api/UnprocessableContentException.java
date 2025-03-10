package cz.cuni.mff.vopalenf.annotator.exception.api;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.UNPROCESSABLE_CONTENT;

/**
 * Exception to be thrown when the content type is understood by server but is unable to process inner instructions
 */
public class UnprocessableContentException extends APIException {
    public UnprocessableContentException(final String message, final String scope) {
        super(UNPROCESSABLE_CONTENT, message, scope);
    }
}