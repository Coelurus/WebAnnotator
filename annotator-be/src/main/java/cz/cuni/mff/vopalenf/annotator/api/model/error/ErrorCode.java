package cz.cuni.mff.vopalenf.annotator.api.model.error;

/**
 * Enum of possible error codes
 */
public enum ErrorCode {
    /**
     * Error code indicating that the requested data was not found.
     */
    DATA_NOT_FOUND,
    /**
     * Error code indicating that the request was malformed or invalid.
     */
    BAD_REQUEST,
    /**
     * Error code indicating that the request could not be processed due to unprocessable content.
     */
    UNPROCESSABLE_CONTENT,
    /**
     * Error code indicating that the user is not authenticated or the credentials are invalid.
     */
    BAD_CREDENTIALS,
    /**
     * Error code indicating that server run into an unexpected condition that prevented it from fulfilling the request.
     */
    RUNTIME_EXCEPTION,
    /**
     * Error code indicating that the user does not have permission to access the requested resource.
     */
    FORBIDDEN,
}
