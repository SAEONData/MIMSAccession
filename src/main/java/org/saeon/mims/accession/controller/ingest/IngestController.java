package org.saeon.mims.accession.controller.ingest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.user.UserService;
import org.saeon.mims.accession.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Slf4j
public class IngestController {

    @Autowired private UserService userService;

    private static final int MAX_SESSION_INTERVAL = 60 * 60; //1 hour

    @GetMapping(value = "/ingest")
    public String getIngestHome(Model model, HttpServletRequest request) {
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        if (StringUtils.isNotEmpty(authToken)) {
            User user = userService.getCurrentUser(authToken);
            if (user == null) {
                model.addAttribute("user", new User());
                return "ingest/signin";
            } else {

                return "ingest/home";
            }
        } else {
            model.addAttribute("user", new User());
            return "ingest/signin";
        }

    }

    @PostMapping(value = "/ingest/signin")
    public String signinAdmin(@ModelAttribute User details, Model model, HttpServletRequest request, HttpServletResponse response) {
        log.info("Signin attempt by username: " + details);
        if (StringUtils.isNotEmpty(details.getEmail()) && StringUtils.isNotEmpty(details.getPassword())) {
            log.info("Details not null && not empty");
            User user = userService.getUserByEmail(details.getEmail());

            if (user != null) {
                log.info("Validated user from DB: " + user.toString());

                if (BCrypt.checkpw(details.getPassword(), user.getPassword())) {
                    request.getSession().setMaxInactiveInterval(MAX_SESSION_INTERVAL);
                    user.setAuthToken(request.getSession().getId());
                    Cookie cookie = new Cookie("sanctuarytycoon", request.getSession().getId());
                    cookie.setMaxAge(-1);
                    response.addCookie(cookie);
                    userService.updateUser(user);
                    log.info("User {} logged in with sessionid {}", user.getId(), user.getAuthToken().substring(0, 5) + "....");

                    return "ingest/home";
                } else {
                    model.addAttribute("error", "Password incorrect");
                    return "ingest/signin";
                }

            } else {
                log.info("Username not found in database");
                model.addAttribute("error", "Username not found");
                return "ingest/signin";

            }
        } else {
            model.addAttribute("error", "Fields not provided");
            return "ingest/signin";

        }
    }
}
