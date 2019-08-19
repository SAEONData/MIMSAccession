package org.saeon.mims.accession.dto.user;

import lombok.Getter;
import lombok.Setter;

public class UserDTO {

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String matchingPassword;

    @Getter
    @Setter
    private String email;

}
