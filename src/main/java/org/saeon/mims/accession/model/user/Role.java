package org.saeon.mims.accession.model.user;

import lombok.Getter;

public enum Role {

    USER(1, "USER", "User"),
    ADMIN(2, "ADMIN", "Administrator"),
    SUPERADMIN(3, "SUPERADMIN", "All Access");

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private String prettyName;

    Role(int id, String name, String prettyName) {
        this.id = id;
        this.name = name;
        this.prettyName = prettyName;
    }
}
