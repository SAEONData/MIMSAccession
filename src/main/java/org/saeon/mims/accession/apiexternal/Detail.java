package org.saeon.mims.accession.apiexternal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class Detail {
    @Getter
    @Setter
    private List<String> loc;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private String type;
}
