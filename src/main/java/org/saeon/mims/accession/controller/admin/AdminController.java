package org.saeon.mims.accession.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.dto.admin.AdminPasswordDto;
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
public class AdminController {

    private UserService userService;
    private AccessionService accessionService;

    @Value("${next.accession.number}")
    private String nextAccessionNumber;

    @Value("${admin.user}")
    private String adminUserEmail;

    @GetMapping("/admin/populate")
    public String populate(Model model, HttpServletRequest request) {
        //populate the admin user.
        log.info("Populate db requested");
        String error;
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        if (StringUtils.isNotEmpty(authToken)) {
            User user = userService.getCurrentUser(authToken);
            if (user != null && user.getEmail().equalsIgnoreCase(adminUserEmail)) {
                log.info("User is validated as admin. Population proceeding");
                accessionService.populateNextAccessionNumber(nextAccessionNumber);
                return "admin/done";
            } else {
                error = "User not verified";
            }
        } else {
            error = "Sign in first";

        }

        model.addAttribute("error", error);
        model.addAttribute("adminPasswordDto", new AdminPasswordDto());
        return "admin/error";
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
