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

    @GetMapping("/manage/{accessionID}")
    public String presentAccessionToManage(Model model, @PathVariable("accessionID") String accessionID) {
        log.info("Manage accession page requested");

        Accession accession = accessionService.getAccessionByAccessionID(accessionID);
        if (accession == null) {
            log.info("Accession does not exist. Accession number: {}", accessionID);
            return "error/manage";
        }

        log.info("Managing accession " + accession.getName());
        model.addAttribute("accession", accession);
        model.addAttribute("embargoTypes", EmbargoType.values());

        return "manage/form";
    }

    @PostMapping("/manage/{accessionID}")
    public String updateAccession(Model model, @PathVariable("accessionID") String accessionID, @ModelAttribute Accession accession) {
        log.debug("Validating accession update form");
        boolean hasErrors = false;
        if ((accession.getName() == null) ||  (accession.getName().length() == 0)) {
            model.addAttribute("nameError", "Name is mandatory, please enter it above!");
            hasErrors = true;
        }

        if ((accession.getEmbargoStateOriginal().equals(EmbargoType.EMBARGOED)) && (accession.getEmbargoExpiry().length() == 0)){
            model.addAttribute("embargoExpiryError", "Embargo expiry date is mandatory for embargoed data, please enter it above!");
            hasErrors = true;
        }
		if (hasErrors) {
            log.debug("Update form has validation errors!");
            model.addAttribute("accession", accession);
            model.addAttribute("embargoTypes", EmbargoType.values());
			return "manage/form";
		}
        log.debug("Attempting to update accession ...");
        String returnPage = "manage/success";
        if (accession != null){
            try {
                // @ModelAttribute accession is a newly created Accession generated when update form is submitted
                //       So: here we simpy update the actual accession with from the new accession
                //           This is done since the new accession won't have variables set that aren't supplied
                //           by the form.
                Accession actualAccession = accessionService.getAccessionByAccessionID(accessionID);
                actualAccession.setName(accession.getName());
                actualAccession.setEmbargoStateWithType(accession.getEmbargoStateOriginal());
                actualAccession.setEmbargoExpiry(accession.getEmbargoExpiry());
                accessionService.updateAccession(actualAccession);
                log.debug("Accession update successful");
            } catch (AccessionException e){
                log.error("Could not update accession!");
                returnPage = "error/manage";
            }
        }
        model.addAttribute("accession", accession);
        return returnPage;
    }

    @GetMapping("/manage/confirm-delete/{accessionID}")
    public String confirmAccessionDeletion(Model model, @PathVariable("accessionID") String accessionID) {
        log.info("Confirm delete accession page requested");

        Accession accession = accessionService.getAccessionByAccessionID(accessionID);
        if (accession == null) {
            log.info("Accession does not exist. AccessionID: {}", accessionID);
            return "error/manage";
        }

        log.info("Confirming accession deletion" + accession.getName());
        model.addAttribute("accession", accession);

        return "manage/delete_confirmation";
    }

    @GetMapping("/manage/delete/{accessionID}")
    public String deleteAccession(Model model, @PathVariable("accessionID") String accessionID) {
        log.info("Attempting to delete accession");

        Accession accession = accessionService.getAccessionByAccessionID(accessionID);
        String returnPage = "manage/delete_success";
        if (accession == null) {
            log.info("Accession does not exist. AccessionID: {}", accessionID);
            return "error/manage";
        } else {
            try {
                    accessionService.deleteAccession(accession);
                    log.debug("Accession deletion successful");
            } catch (AccessionException e){
                log.error("Could not delete accession!");
                returnPage = "error/manage";
            }
        }

        log.info("Confirming accession deletion" + accession.getName());
        return returnPage;
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