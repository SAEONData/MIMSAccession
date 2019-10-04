package org.saeon.mims.accession.controller.admin;

import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.dto.admin.AdminPasswordDto;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.saeon.mims.accession.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private AccessionService accessionService;

    @Value("${populate.password}")
    private String validPopulationPassword;

    @Value("${next.accession.number}")
    private String nextAccessionNumber;

    @GetMapping("/admin")
    public String getAdminHome(Model model) {
        model.addAttribute("adminPasswordDto", new AdminPasswordDto());
        return "admin/home";
    }

    @PostMapping("/admin/populate")
    public String populate(Model model, @ModelAttribute AdminPasswordDto adminPasswordDto) {
        //populate the admin user.
        String error;
        if (StringUtils.isNotEmpty(adminPasswordDto.getAdminPassword())) {
            if (adminPasswordDto.getAdminPassword().equalsIgnoreCase(validPopulationPassword)) {
                userService.populateAdminUser(validPopulationPassword);
                accessionService.populateNextAccessionNumber(nextAccessionNumber);
                return "admin/done";
            } else {
                error = "Incorrect password";
            }
        } else {
            error = "Blank password";

        }

        model.addAttribute("error", error);
        model.addAttribute("adminPasswordDto", new AdminPasswordDto());
        return "admin/error";
    }
}
