package org.saeon.mims.accession.response;

import lombok.Getter;
import lombok.Setter;
import org.saeon.mims.accession.exception.AccessionException;

public class AccessionError {

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String message;

    public AccessionError() {

    }

    public AccessionError(AccessionException ae) {
        this.code = ae.getCode();
        this.message = ae.getErrorMessage();

    }

    public AccessionError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public AccessionError(String message) {
        this.message = message;
    }
}
