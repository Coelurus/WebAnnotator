package cz.cuni.mff.vopalenf.annotator.exception.api;

import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Exception denoting problems with API. Contains an error code, message, and
 * scope.
 */
@AllArgsConstructor
@Getter
@Builder
public class APIException extends RuntimeException {
    /**
     * Error code representing the type of error.
     */
    protected final ErrorCode code;
    /**
     * Human-readable message describing the error.
     */
    protected final String message;
    /**
     * Scope of the error, providing additional context.
     */
    protected final String scope;
}
