package org.saeon.mims.accession.apiexternal;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;
import org.saeon.mims.accession.dto.odp.ODPAccessionDTO;

public interface ODPClient {
    @RequestLine("POST /dea/metadata")
    @Headers({"Content-Type: application/json", "Authorization: {access_token}"})
    Response create(@Param("access_token") String odpAccessToken, ODPAccessionDTO odpAccession);
}
