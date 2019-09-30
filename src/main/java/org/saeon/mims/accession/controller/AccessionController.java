package org.saeon.mims.accession.controller;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.saeon.mims.accession.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller(value = "/")
@Slf4j
public class AccessionController {

    @Autowired
    private AccessionService accessionService;

    @Autowired
    private UserService userService;

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping(value = "/")
    public String getHTML() {
        log.info("Home page requested");
        return "home";
    }

}
