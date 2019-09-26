package org.saeon.mims.accession.repository;

import org.saeon.mims.accession.model.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    User findDistinctByEmail(String email);
    User findDistinctByAuthToken(String authToken);

}
