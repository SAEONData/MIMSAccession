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

    @Autowired private UserService userService;
    @Autowired private AccessionService accessionService;

    @GetMapping(value = "/ingest/home")
    public String getIngestHome(Model model, HttpServletRequest request) {
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        if (StringUtils.isNotEmpty(authToken)) {
            User user = userService.getCurrentUser(authToken);
            if (user != null) {
                model.addAttribute("basefolder", baseFolder);
                model.addAttribute("accession", new Accession());
                model.addAttribute("embargoTypes", EmbargoType.values());
                return "ingest/home";
            }

        }

        model.addAttribute("user", new LoginDTO());
        model.addAttribute("goto", "/ingest/home");
        return "login/login";

    }

    @GetMapping(value = "/ingest/accession")
    public String presentAccession(Model model, @ModelAttribute Accession accession) {
        model.addAttribute("basefolder", baseFolder);
        model.addAttribute("accession", new Accession());
        model.addAttribute("embargoTypes", EmbargoType.values());
        return "ingest/form";
    }

    @PostMapping(value = "/ingest/accession")
    public String attemptAccession(Model model, @ModelAttribute Accession accession) {
        log.debug("Accession attempted");
        String returnPage = "ingest/success";
        if (accession != null) {
            try {
                accessionService.ingestAccession(accession);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AccessionException e) {
                log.error("Could not accession collection", e);
                model.addAttribute("error", e.getErrorMessage());
                switch (e.getCode()) {
                    case 400: //bad request
                        returnPage = "ingest/400";
                        break;
                    case 403: //forbidden
                        returnPage = "ingest/403";
                        break;
                    case 404:
                        returnPage = "ingest/404";
                        break;
                    case 422: //unprocessable entity
                        returnPage = "ingest/422";
                        break;
                    case 500: //unexpected server error
                        returnPage = "ingest/500";
                        break;
                }
            }

        }

        model.addAttribute("accession", accession);
        return returnPage;
    }

    @GetMapping("/accession/list")
    public String getAccessionList(Model model, HttpServletRequest request) {
        List<Accession> accessions = accessionService.getAllAccessions();
        String returnPage = "/accession/list";
        model.addAttribute("accessionList", accessions);
        return returnPage;
    }

}
