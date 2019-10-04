package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccessionNumber {

    @Id
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private Long nextAccessionNumber;

}
