package org.saeon.mims.accession.model.accession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@AllArgsConstructor
public enum EmbargoType {

    OPEN(1, "Open"),
    EMBARGOED(2, "Embargoed"),
    RESTRICTED(3, "Restricted");

    @Getter @Setter
    private int id;

    @Getter @Setter
    private String name;

    public static EmbargoType getById(int embargoType) {
        return Arrays.asList(EmbargoType.values()).stream().filter(f -> f.getId() == embargoType).findFirst().orElse(EmbargoType.OPEN);
    }
}
