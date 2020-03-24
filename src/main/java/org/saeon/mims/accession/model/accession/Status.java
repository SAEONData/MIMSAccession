package org.saeon.mims.accession.model.accession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@AllArgsConstructor
public enum Status {

    NOT_STARTED(1, "Not started"),
    PENDING(2, "Pending"),
    IN_PROGRESS(3, "In progress"),
    FAILED(4, "Failed"),
    COMPLETED(5, "Accessioned");

    @Getter
    @Setter
    private int id;

    @Getter @Setter
    private String name;

    public static Status getById(int status) {
        return Arrays.asList(Status.values()).stream().filter(f -> f.getId() == status).findFirst().orElse(Status.NOT_STARTED);
    }

}
