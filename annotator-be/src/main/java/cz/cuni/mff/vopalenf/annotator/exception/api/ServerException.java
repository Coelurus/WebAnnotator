package cz.cuni.mff.vopalenf.annotator.exception.api;

import static cz.cuni.mff.vopalenf.annotator.api.model.error.ErrorCode.RUNTIME_EXCEPTION;

public class ServerException extends APIException {
    public ServerException(final String message, final String scope) {
        super(RUNTIME_EXCEPTION, message, scope);
    }
}
