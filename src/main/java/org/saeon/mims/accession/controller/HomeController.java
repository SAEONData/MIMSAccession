package org.saeon.mims.accession.controller;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.saeon.mims.accession.service.user.UserService;
import org.saeon.mims.accession.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class HomeController {

    @Autowired
    private AccessionService accessionService;

    @Autowired
    private UserService userService;

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping(value = "/")
    public String getHTML(Model model, HttpServletRequest request) {
        log.debug("Home page requested");
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        User user = userService.getUserByAuthToken(authToken);

        model.addAttribute("signedIn", user != null);

        return "home";
    }

}
