package org.saeon.mims.accession.dto.user;

import lombok.Getter;
import lombok.Setter;

public class LoginDTO {

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String gotoLink;

}
