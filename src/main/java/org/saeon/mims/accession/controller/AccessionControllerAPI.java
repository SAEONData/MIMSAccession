package org.saeon.mims.accession.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.exception.AccessionException;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.request.IngestRequest;
import org.saeon.mims.accession.response.AccessionError;
import org.saeon.mims.accession.response.AccessionValidationError;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class AccessionControllerAPI {

    @Autowired
    private AccessionService accessionService;

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping(value = "/api/")
    public String get() {
        log.info("Base folder: {}", basefolder);
        return "OK";

    }

    @PostMapping(value = "/api/ingest", headers = "Accept=application/json")
    public ResponseEntity register(@Valid @RequestBody IngestRequest ingestRequest) {
        log.info("New request: {}", ingestRequest);

        //validate the ingestRequest, including 'does the file actually exist?'

        Accession accession;
        try {
            accession = accessionService.ingestAccessionAPI(ingestRequest, basefolder);
        } catch (AccessionException e) {
            return ResponseEntity.status(e.getCode()).body(new Gson().toJson(new AccessionError(e)));
        }

        return ResponseEntity.ok(new Gson().toJson(accession));
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
