package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
public class FileChecksum {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String fileName;

    @Getter
    @Setter
    private String checksum;

    @ManyToMany
    @JoinTable(
            name = "accession_files",
            joinColumns = {@JoinColumn(name = "filechecksum_id")},
            inverseJoinColumns = {@JoinColumn(name = "accession_id")}
    )
    @Getter
    @Setter
    private List<Accession> accession;
}
