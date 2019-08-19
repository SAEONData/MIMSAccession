package org.saeon.mims.accession.controller.registration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.dto.user.EmailValidator;
import org.saeon.mims.accession.dto.user.PasswordMatchesValidator;
import org.saeon.mims.accession.dto.user.UserDTO;
import org.saeon.mims.accession.exception.EmailExistsException;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.response.AccessionValidationError;
import org.saeon.mims.accession.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/register")
    public String getRegisterHomePage(Model model) {
        UserDTO user = new UserDTO();
        model.addAttribute("user", user);
        return "register/register-home";
    }

    @PostMapping(value = "/register")
    public String registerNewUser(@ModelAttribute("user")
                                              UserDTO userDTO,
                                        Model model,
                                        HttpServletRequest request) {

        User registered = new User();
        List<AccessionValidationError> errors = new ArrayList<>();

        validateRegistration(userDTO, errors);

        if (errors.isEmpty()) {
            registered = createUserAccount(userDTO);
            if (registered == null) {
                errors.add(new AccessionValidationError("email", "Email already registered"));
            }
        }

        if (!errors.isEmpty()) {
            log.info("Errors on the registration form: {}", errors.size());
            model.addAttribute("errors", errors);
            model.addAttribute("user", userDTO);
            return "register/register-home";
        }
        else {
            model.addAttribute("user", registered);

            return "register/success";
        }


    }

    private void validateRegistration(UserDTO userDTO, List<AccessionValidationError> errors) {
        if (StringUtils.isBlank(userDTO.getFirstName())) {
            errors.add(new AccessionValidationError("firstName", "First name is blank"));
        }

        if (StringUtils.isBlank(userDTO.getLastName())) {
            errors.add(new AccessionValidationError("lastName", "Surname is blank"));
        }

        if (StringUtils.isBlank(userDTO.getEmail())) {
            errors.add(new AccessionValidationError("email", "Email address is blank"));

        } else if (!EmailValidator.isValid(userDTO.getEmail())) {
            errors.add(new AccessionValidationError("email", "Email address is not valid"));
        }

        if (StringUtils.isBlank(userDTO.getPassword()) || StringUtils.isBlank(userDTO.getMatchingPassword())) {
            errors.add(new AccessionValidationError("password", "Password is blank"));

        } else if (!PasswordMatchesValidator.isValid(userDTO)) {
            errors.add(new AccessionValidationError("password", "Password does not match \' Confirm password\'"));
        }
    }


    private User createUserAccount(UserDTO userDTO) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(userDTO);
        } catch (EmailExistsException e) {
            log.info("User attempted to register with existing email", e);
            return null;
        }
        return registered;
    }
}
