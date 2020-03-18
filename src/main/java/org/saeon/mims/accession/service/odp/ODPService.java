package org.saeon.mims.accession.service.odp;

import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.Response;
import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.apiexternal.ODPClient;
import org.saeon.mims.accession.dto.odp.ODPAccessionDTO;
import org.saeon.mims.accession.model.accession.Accession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.SocketException;

@Service
@Slf4j
public class ODPService {

    @Value("${odp.api.key}")
    private String apiKey;

    @Value("${odp.collection.key}")
    private String collectionKey;

    @Value("${odp.schema.key}")
    private String schemaKey;

    @Value("${odp.external.url}")
    private String odpUrl;

    public int addAccessionToODP(Accession accession) throws SocketException {
        ODPAccessionDTO accessionDTO = new ODPAccessionDTO(accession, collectionKey, schemaKey);

        ODPClient odpClient = Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .logger(new feign.slf4j.Slf4jLogger(ODPClient.class))
                .logLevel(Logger.Level.FULL)
                .target(ODPClient.class, odpUrl);
        try {
            Response response = odpClient.create(apiKey, accessionDTO);
            log.info("Response status: {}", response.status());
            return response.status();

        } catch (FeignException e) {
            return 404;
        }

    }
}
