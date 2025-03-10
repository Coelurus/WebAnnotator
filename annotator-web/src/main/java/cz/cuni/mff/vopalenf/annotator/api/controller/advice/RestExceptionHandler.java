package cz.cuni.mff.vopalenf.annotator.api.controller.advice;

import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorResponseItem;
import cz.cuni.mff.vopalenf.annotator.exception.api.APIException;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadCredentialsException;
import cz.cuni.mff.vopalenf.annotator.exception.api.BadRequestException;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import cz.cuni.mff.vopalenf.annotator.exception.api.ServerException;
import cz.cuni.mff.vopalenf.annotator.exception.api.UnprocessableContentException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

/**
 * Class handling catching exceptions from BE and transforming them into response REST-like object
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handler for NotFoundException
     *
     * @param e Thrown exception
     * @return Error response with information about exception
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = handleException(e, HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handler for BadRequestException
     *
     * @param e Thrown exception
     * @return Error response with information about exception
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = handleException(e, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handler for UnprocessableContentException
     *
     * @param e Thrown exception
     * @return Error response with information about exception
     */
    @ExceptionHandler(UnprocessableContentException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableContentException(UnprocessableContentException e) {
        ErrorResponse errorResponse = handleException(e, HttpStatus.UNPROCESSABLE_ENTITY);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Handler for UnprocessableContentException
     *
     * @param e Thrown exception
     * @return Error response with information about exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        ErrorResponse errorResponse = handleException(e, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handler for ServerException
     *
     * @param e Thrown exception
     * @return Error response with information about exception
     */
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException e) {
        ErrorResponse errorResponse = handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Handles creating ErrorResponse object
     *
     * @param e      Caught exception thrown in project
     * @param status Http status of response
     * @return Error response containing information about exception
     */
    private ErrorResponse handleException(APIException e, HttpStatus status) {
        return ErrorResponse.builder()
                .status(status.value())
                .errors(Collections.singletonList(
                        ErrorResponseItem.builder()
                                .error(e.getCode().name())
                                .scope(e.getScope())
                                .message(e.getMessage())
                                .build()
                ))
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .build();
    }
}
