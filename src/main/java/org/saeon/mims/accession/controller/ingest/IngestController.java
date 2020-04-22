package org.saeon.mims.accession.controller.ingest;

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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class IngestController {

    @Value("${base.folder}")
    private String baseFolder;

    private UserService userService;
    private AccessionService accessionService;

    @GetMapping(value = "/ingest/home")
    public String getIngestHome(Model model, HttpServletRequest request) {
        log.info("Accession home page requested");
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        if (StringUtils.isNotEmpty(authToken)) {
            User user = userService.getCurrentUser(authToken);
            if (user != null) {
                model.addAttribute("basefolder", baseFolder);
                model.addAttribute("accession", new Accession());
                model.addAttribute("embargoTypes", EmbargoType.values());
                return "ingest/home";
            } else {
                log.info("Accession home page failed. Auth token not associated with a valid user");
            }

        } else {
            log.info("Accession home page failed. Auth token not provided");
        }


        model.addAttribute("user", new LoginDTO());
        model.addAttribute("goto", "/ingest/home");
        return "login/login";

    }

    @GetMapping(value = "/ingest/accession")
    public String presentAccession(Model model, @ModelAttribute Accession accession) {
        log.info("Create accession page requested");
        model.addAttribute("basefolder", baseFolder);
        model.addAttribute("accession", new Accession());
        model.addAttribute("embargoTypes", EmbargoType.values());
        return "ingest/form";
    }

    @PostMapping(value = "/ingest/accession")
    public String attemptAccession(Model model, @ModelAttribute Accession accession) {
        boolean hasErrors = false;

        log.debug("Validating accession input form");
        if ((accession.getName() == null) ||  (accession.getName().length() == 0)) {
            model.addAttribute("nameError", "Name is mandatory, please enter it above!");
            hasErrors = true;
        }
        if ((accession.getHomeFolder() == null) ||  (accession.getHomeFolder().length() == 0)) {
            model.addAttribute("homeFolderError", "Home folder is mandatory, please enter it above!");
            hasErrors = true;
        }
        if ((accession.getEmbargoState().equals(EmbargoType.EMBARGOED)) && (accession.getEmbargoExpiry() == null )){
            model.addAttribute("embargoExpiryError", "Embargo expiry date is mandatory for embargoed data, please enter it above!");
            hasErrors = true;
        }
		if (hasErrors) {
            log.debug("Form has validation errors!");
            model.addAttribute("basefolder", baseFolder);
            model.addAttribute("accession", new Accession());
            model.addAttribute("embargoTypes", EmbargoType.values());
			return "ingest/form";
		}

        log.debug("Attempting accession ...");
        String returnPage = "ingest/success";
        if (accession != null) {
            try {
                log.info("Attempting accession");
                accessionService.ingestAccession(accession);
                log.info("Accession successful");
            } catch (IOException e) {
                log.error("File/folder issue", e);
            } catch (AccessionException e) {
                log.error("Could not accession collection", e);
                model.addAttribute("error", e.getErrorMessage());
                switch (e.getCode()) {
                    case 400: //bad request
                        log.info("ODP returned 400");
                        returnPage = "ingest/400";
                        break;
                    case 403: //forbidden
                        log.info("ODP returned 403");
                        returnPage = "ingest/403";
                        break;
                    case 404:
                        log.info("ODP could not be reached");
                        returnPage = "ingest/404";
                        break;
                    case 422: //unprocessable entity
                        log.info("ODP returned 422");
                        returnPage = "ingest/422";
                        break;
                    case 500: //unexpected server error
                        log.info("ODP returned 500");
                        returnPage = "ingest/500";
                        break;
                    case 503: //service unavailable
                        log.info("ODP returned 503");
                        returnPage = "ingest/503";
                        break;
                    default:
                        log.error("An error occurred while accessioning: {}", e.getMessage(), e);
                        returnPage = "error/ingest";
                        break;
                }
            }

        }

        model.addAttribute("accession", accession);
        return returnPage;
    }

    @GetMapping("/accession/list")
    public String getAccessionList(Model model, HttpServletRequest request) {
        log.info("Get list of accessions page requested");
        List<Accession> accessions = accessionService.getAllAccessions();
        String returnPage = "accession/list";
        model.addAttribute("accessionList", accessions);
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
