package org.saeon.mims.accession.repository;

import org.saeon.mims.accession.model.accession.AccessionNumber;
import org.springframework.data.repository.CrudRepository;

public interface AccessionNumberRepository extends CrudRepository<AccessionNumber, Integer> {

}
