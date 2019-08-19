package org.saeon.mims.accession.model.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_privilege", joinColumns = @JoinColumn(name = "id"))
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private List<Role> roles;

}
