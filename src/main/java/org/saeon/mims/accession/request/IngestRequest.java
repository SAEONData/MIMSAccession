package org.saeon.mims.accession.request;

import lombok.Getter;
import lombok.Setter;
import org.saeon.mims.accession.model.accession.EmbargoType;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class IngestRequest {

    @Getter @Setter @NotBlank(message = "Ingest object must be provided") private String ingestObject;
    @Getter @Setter private Long parentAccessionNumber;
    @Getter @Setter private int embargoType;
    @Getter @Setter private @NotBlank(message = "Accession must have a name") String name;

    @Override
    public String toString() {
        return new StringBuilder()
                .append("\n\tAccession name: ").append(name)
                .append("\tIngest folder: ").append(ingestObject)
                .append("\n\tParent Accession Number: ").append(parentAccessionNumber)
                .append("\n\tEmbargo type: ").append(EmbargoType.getById(embargoType).getName())
                .toString();
    }

}
