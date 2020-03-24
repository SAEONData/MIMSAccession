package org.saeon.mims.accession.util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class SaltingAPassword {

    public static void main(String... args) {
        //To salt a password, do this:
        String saltedPassword = BCrypt.hashpw("your password here", BCrypt.gensalt());

        //To compare a password to a salted password:
        boolean isValidPassword = BCrypt.checkpw("your password here", saltedPassword);

        System.out.println(saltedPassword);
        System.out.println("Is the password valid? : " + (isValidPassword ? "Yes" : "No"));
    }

}
