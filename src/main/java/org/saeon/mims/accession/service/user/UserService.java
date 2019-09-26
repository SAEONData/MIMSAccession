package org.saeon.mims.accession.service.user;

import org.saeon.mims.accession.dto.user.UserDTO;
import org.saeon.mims.accession.exception.EmailExistsException;
import org.saeon.mims.accession.model.user.Role;
import org.saeon.mims.accession.model.user.User;
import org.saeon.mims.accession.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

    public User getCurrentUser(String authToken) {
        return userRepository.findDistinctByAuthToken(authToken);
    }

    public void populateAdminUser(String password) {
        User user = userRepository.findDistinctByEmail("mims.admin@ocean.gov.za");
        if (user == null) {
            user = new User();
            user.setEmail("mims.admin@ocean.gov.za");
            user.setFirstName("MIMS");
            user.setLastName("Admin");
            String hashedPw = BCrypt.hashpw(password, BCrypt.gensalt());
            user.setPassword(hashedPw);
            userRepository.save(user);
        }

    }

    public User getUserByEmail(String email) {
        return userRepository.findDistinctByEmail(email);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
