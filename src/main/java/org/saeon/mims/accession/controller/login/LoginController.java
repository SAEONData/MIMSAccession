package org.saeon.mims.accession.controller.login;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.dto.user.LoginDTO;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.saeon.mims.accession.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class LoginController {

    @Autowired private UserService userService;

    @Autowired
    private AccessionService accessionService;

    @Value("${admin.user}")
    private String adminUserEmail;

    private static final int MAX_SESSION_INTERVAL = 60 * 60; //1 hour

    @GetMapping(value = "/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new LoginDTO());
        return "login/login";
    }

    @PostMapping(value = "/login")
    public String signinAdmin(@ModelAttribute LoginDTO details, Model model, HttpServletRequest request, HttpServletResponse response) {
        log.debug("Signin attempt by username: " + details);
        if (StringUtils.isNotEmpty(details.getEmail()) && StringUtils.isNotEmpty(details.getPassword())) {
            log.debug("Details not null && not empty");
            User user = userService.getUserByEmail(details.getEmail());

            if (user != null) {
                log.debug("Validated user from DB: " + user.toString());

                if (BCrypt.checkpw(details.getPassword(), user.getPassword())) {
                    request.getSession().setMaxInactiveInterval(MAX_SESSION_INTERVAL);
                    user.setAuthToken(request.getSession().getId());
                    Cookie cookie = new Cookie("mims-accession", request.getSession().getId());
                    cookie.setMaxAge(-1);
                    response.addCookie(cookie);
                    userService.updateUser(user);
                    log.info("User {} logged in with sessionid {}", user.getId(), user.getAuthToken().substring(0, 5) + "....");

                    String returnPage = StringUtils.isNotEmpty(details.getGotoLink()) ? details.getGotoLink() : "home";
                    model.addAttribute("signedIn", user != null);
                    model.addAttribute("user", user);
                    model.addAttribute("showPopulate", !accessionService.isAccessionNumberPopulated());
                    model.addAttribute("adminUser", user == null ? false : user.getEmail().equalsIgnoreCase(adminUserEmail));
                    return returnPage;

                } else {
                    model.addAttribute("error", "Password incorrect");
                    return "login/login";
                }

            } else {
                log.debug("Username not found in database");
                model.addAttribute("error", "Username not found");
                return "login/login";

            }
        } else {
            model.addAttribute("error", "Fields not provided");
            return "login/login";

        }
    }
}
