package org.saeon.mims.accession.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.saeon.mims.accession.apiexternal.ErrorDTO;

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

    public AccessionException(int code, ErrorDTO errorDTO) {
        this.code = code;
        if (errorDTO != null && CollectionUtils.isEmpty(errorDTO.getDetail())) {
            this.setErrorMessage(errorDTO.getDetail().get(0).getMsg());

        }
    }
}
