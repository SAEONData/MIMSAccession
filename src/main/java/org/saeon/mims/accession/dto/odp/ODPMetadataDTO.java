package org.saeon.mims.accession.dto.odp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.saeon.mims.accession.model.accession.Accession;

@ToString
public class ODPMetadataDTO {

    @Getter
    @Setter
    private String accession_number;

    @Getter
    @Setter
    private String uuid;

    public ODPMetadataDTO() {}

    public ODPMetadataDTO(Accession accession) {
        this.accession_number = String.valueOf(accession.getAccessionNumber());
        this.uuid = accession.getUuid();
    }

}
