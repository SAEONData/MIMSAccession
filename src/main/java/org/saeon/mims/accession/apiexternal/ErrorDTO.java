package org.saeon.mims.accession.apiexternal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class ErrorDTO {

    @Getter
    @Setter
    private List<Detail> detail;
}
