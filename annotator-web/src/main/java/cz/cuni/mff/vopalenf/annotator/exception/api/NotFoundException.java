package cz.cuni.mff.vopalenf.annotator.exception.api;

import lombok.Getter;

import static cz.cuni.mff.vopalenf.annotator.api.model.ErrorCode.DATA_NOT_FOUND;

@Getter
public class NotFoundException extends APIException {
    public NotFoundException(final String message, final String scope) {
        super(DATA_NOT_FOUND, message, scope);
    }
}