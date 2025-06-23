package cz.cuni.mff.vopalenf.annotator.exception.api;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.UNPROCESSABLE_CONTENT;

/**
 * Exception to be thrown when the content type is understood by server but is unable to process inner instructions
 */
public class UnprocessableContentException extends APIException {
    /**
     * Constructs a new UnprocessableContentException with the specified message and scope.
     *
     * @param message the detail message
     * @param scope   the scope of the error
     */
    public UnprocessableContentException(final String message, final String scope) {
        super(UNPROCESSABLE_CONTENT, message, scope);
    }
}