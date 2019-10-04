package org.saeon.mims.accession.repository;

import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.FileChecksum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileChecksumRepository extends CrudRepository<FileChecksum, Long> {

    List<FileChecksum> findAllByAccession(Accession accession);
    FileChecksum findTop1ByChecksum(String checksum);

}
