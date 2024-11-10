package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * Response object returned by server when exception is thrown
 */
@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ErrorResponse {

    /**
     * Http status code
     */
    int status;

    /**
     * List of errors
     */
    List<ErrorResponseItem> errors;

    /**
     * Stack trace of errors
     */
    String stackTrace;
}
