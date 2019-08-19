package org.saeon.mims.accession.exception;

import lombok.Getter;
import lombok.Setter;

public class AccessionException extends Exception {

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String errorMessage;

    public AccessionException() {
        super();
    }

    public AccessionException(int code, String errorMessage) {
        super(errorMessage);
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public AccessionException(int code, String errorMessage, Exception e) {
        super(errorMessage);
        this.setStackTrace(e.getStackTrace());
        this.code = code;
        this.errorMessage = errorMessage;
    }
}
