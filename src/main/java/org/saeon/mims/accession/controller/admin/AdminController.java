package org.saeon.mims.accession.controller.admin;

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
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private AccessionService accessionService;

    @Value("${populate.password}")
    private String validPopulationPassword;

    @Value("${next.accession.number}")
    private String nextAccessionNumber;

    @Value("${admin.user}")
    private String adminUserEmail;

    @GetMapping("/admin")
    public String getAdminHome(Model model) {
        model.addAttribute("adminPasswordDto", new AdminPasswordDto());
        return "admin/home";
    }

    @GetMapping("/admin/populate")
    public String populate(Model model, HttpServletRequest request) {
        //populate the admin user.
        String error;
        String authToken = AppUtils.getAuthTokenFromRequest(request);
        if (StringUtils.isNotEmpty(authToken)) {
            User user = userService.getCurrentUser(authToken);
            if (user != null && user.getEmail().equalsIgnoreCase(adminUserEmail)) {
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
}
