package cz.cuni.mff.vopalenf.annotator.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ErrorResponseItem {

    /**
     * Information about what happened
     */
    String error;
    
    /**
     * Information about where it happened
     */
    String scope;

    /**
     * Closer information about error
     */
    String message;
}
