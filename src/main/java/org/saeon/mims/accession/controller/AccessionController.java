package org.saeon.mims.accession.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/")
@Slf4j
public class AccessionController {

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping
    public String get() {
        log.info("Base folder: {}", basefolder);
        return "OK";

    }

}
