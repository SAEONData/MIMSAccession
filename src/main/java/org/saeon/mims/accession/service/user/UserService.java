package org.saeon.mims.accession.service.user;

import org.saeon.mims.accession.dto.user.UserDTO;
import org.saeon.mims.accession.exception.EmailExistsException;
import org.saeon.mims.accession.model.user.Role;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerNewUserAccount(UserDTO userDTO) throws EmailExistsException {
        User user = userRepository.findDistinctByEmail(userDTO.getEmail());
        if (user != null) {
            throw new EmailExistsException("Email " + userDTO.getEmail() + " already exists");
        }

        user = new User(userDTO);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        user.setRoles(roles);
        userRepository.save(user);

        return user;

    }
}
