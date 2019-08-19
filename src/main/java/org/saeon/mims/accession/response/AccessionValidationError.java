package org.saeon.mims.accession.response;

import lombok.Getter;
import lombok.Setter;

public class AccessionValidationError extends AccessionError {

    @Getter @Setter private String fieldName;

    public AccessionValidationError() {
        super();
    }

    public AccessionValidationError(int code, String fieldName, String message) {
        super(code, message);
        this.fieldName = fieldName;
    }

    public AccessionValidationError(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

}
