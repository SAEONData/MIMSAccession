package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FileChecksum {

    @Getter
    @Setter
    @Id
    private Long id;

    @Getter
    @Setter
    private String fileName;

    @Getter
    @Setter
    private String checksum;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name="Accession_ID", nullable=false)
    private Accession accession;
}
