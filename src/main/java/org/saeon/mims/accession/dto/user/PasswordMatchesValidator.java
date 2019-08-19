package org.saeon.mims.accession.dto.user;

import org.thymeleaf.util.StringUtils;

public class PasswordMatchesValidator {

    public static boolean isValid(Object obj) {
        UserDTO user = (UserDTO) obj;
        return !StringUtils.isEmptyOrWhitespace(user.getPassword()) && user.getPassword().equals(user.getMatchingPassword());
    }
}
