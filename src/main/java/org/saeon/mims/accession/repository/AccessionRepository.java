package org.saeon.mims.accession.repository;

import org.saeon.mims.accession.model.accession.Accession;
import org.springframework.data.repository.CrudRepository;

public interface AccessionRepository extends CrudRepository<Accession, Long> {

    Accession findDistinctByAccessionNumber(Long accessionNumber);
    Accession findDistinctByUuid(String uuid);
}
