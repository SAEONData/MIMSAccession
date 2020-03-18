package org.saeon.mims.accession.dto.odp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.saeon.mims.accession.model.accession.Accession;

@ToString
public class ODPAccessionDTO {

    @Getter
    @Setter
    private String collection_key;

    @Getter
    @Setter
    private String schema_key;

    @Getter
    @Setter
    private ODPMetadataDTO metadata;

    public ODPAccessionDTO() {}

    public ODPAccessionDTO(Accession accession, String collectionKey, String schemaKey) {
        this.collection_key = collectionKey;
        this.schema_key = schemaKey;
        this.metadata = new ODPMetadataDTO(accession);
    }

}
