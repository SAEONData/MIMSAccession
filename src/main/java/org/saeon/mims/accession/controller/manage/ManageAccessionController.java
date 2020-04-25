package org.saeon.mims.accession.controller.manage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.dto.user.LoginDTO;
import org.saeon.mims.accession.exception.AccessionException;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.EmbargoType;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.saeon.mims.accession.service.user.UserService;
import org.saeon.mims.accession.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j

public class ManageAccessionController {

    private UserService userService;
    private AccessionService accessionService;

    @GetMapping("/manage/{accessionNumber}")
    public String presentAccessionToManage(Model model, @PathVariable("accessionNumber") String accessionNumber) {
        log.info("Manage accession page requested");

        Accession accession = accessionService.getAccessionByAccessionNumber(Long.parseLong(accessionNumber));
        if (accession == null) {
            log.info("Accession does not exist. Accession number: {}", accessionNumber);
            return "error/manage";
        }

        log.info("Managing accession " + accession.getName());
        log.info(accession.getEmbargoExpiry().getClass().getSimpleName());

        model.addAttribute("accession", accession);
        model.addAttribute("existingAccession", accession);
        model.addAttribute("embargoTypes", EmbargoType.values());

        return "manage/form";
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAccessionService(AccessionService accessionService) {
        this.accessionService = accessionService;
    }

}