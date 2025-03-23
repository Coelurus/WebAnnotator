package cz.cuni.mff.vopalenf.annotator.exception.api;

import cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class APIException extends RuntimeException {
    protected final ErrorCode code;
    protected final String message;
    protected final String scope;
}
