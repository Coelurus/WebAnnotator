package cz.cuni.mff.vopalenf.annotator.api.controller.advice;

import cz.cuni.mff.vopalenf.annotator.api.model.ErrorResponse;
import cz.cuni.mff.vopalenf.annotator.api.model.ErrorResponseItem;
import cz.cuni.mff.vopalenf.annotator.exception.api.NotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .errors(Collections.singletonList(
                        ErrorResponseItem.builder()
                                .error(e.getCode().name())
                                .scope(e.getScope())
                                .message(e.getMessage())
                                .build()
                ))
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
