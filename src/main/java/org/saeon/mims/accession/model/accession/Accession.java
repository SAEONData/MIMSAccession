package org.saeon.mims.accession.model.accession;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.saeon.mims.accession.util.AppUtils;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
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
    @Enumerated(value = EnumType.STRING)
    private EmbargoType embargoState;

    @Getter
    @Setter
    private Date embargoExpiry;

    /******************************************
     *
     * UTILITY METHODS
     *
     ******************************************/

    public void setEmbargoExpiry(String date) {
        if (StringUtils.isNotEmpty(date)) {
            LocalDate d = AppUtils.convertStringToDate(date);
            this.embargoExpiry = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
    }

    public void setEmbargoState(String embargoType) {
        this.embargoState = EmbargoType.getById(Integer.parseInt(embargoType));
    }


}
