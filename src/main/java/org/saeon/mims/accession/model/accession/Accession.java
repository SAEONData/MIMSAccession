package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Accession {

    @Getter
    @Setter
    @Id
    private Long id;

    @Getter @Setter
    private Long accessionNumber;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private Long parentAccessionNumber;

    @Getter @Setter
    private String homeFolder;

    @OneToMany(cascade=ALL, mappedBy="accession")
    @Getter
    @Setter
    private Set<FileChecksum> files;

    @Getter
    @Setter
    private EmbargoType embargoState;

    @Getter
    @Setter
    private Date embargoExpiry;


}
