package org.saeon.mims.accession.controller;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.response.AccessionValidationError;
import org.saeon.mims.accession.service.AccessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Controller(value = "/")
@Slf4j
public class AccessionController {

    @Autowired
    private AccessionService accessionService;

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping(value = "/")
    public String getHTML() {
        log.info("Home page requested");
        return "home";
    }

    @GetMapping(value = "/ingest")
    public String getIngestHome() {
        log.info("Ingest home page requested");
        return "ingest/home";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<AccessionValidationError> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<AccessionValidationError> errors2 = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors2.add(new AccessionValidationError(400, fieldName, errorMessage));
        });

        return errors2;
    }

}
