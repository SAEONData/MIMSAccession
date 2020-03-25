package org.saeon.mims.accession.controller.download;

import lombok.extern.slf4j.Slf4j;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.EmbargoType;
import org.saeon.mims.accession.service.accession.AccessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Slf4j
public class DownloadController {

    @Value("${data.folder}")
    private String dataFolder;

    private final String folderStructure = "/V0.0/data/0-Data/";
    private final String downloadFileName = "sip.zip";

    @Autowired
    private AccessionService accessionService;

    @GetMapping("/download/{accessionNumber}")
    public ResponseEntity downloadFileFromLocal(@PathVariable("accessionNumber") String accessionNumber) {
        log.info("Download accession requested: {}", accessionNumber);

        Accession accession = accessionService.getAccessionByAccessionNumber(Long.parseLong(accessionNumber));

        if (accession == null) {
            log.info("Accession does not exist. Accession number: {}", accessionNumber);
            return ResponseEntity.status(404).body("Accession " + accessionNumber + " not found");
        } else if (accession.getEmbargoState().equals(EmbargoType.RESTRICTED)) {
            log.info("Accession is restricted. Accession number: {}", accessionNumber);
            return ResponseEntity.status(403).body("Accession " + accessionNumber + " is restricted");
        } else if (accession.getEmbargoState().equals(EmbargoType.EMBARGOED) && (System.currentTimeMillis() < accession.getEmbargoExpiry().getTime())) {
            log.info("Accession is embargoed. Accession number: {}, embargo expiry: {}", accessionNumber, accession.getEmbargoExpiry());
            return ResponseEntity.status(403).body("Accession " + accessionNumber + " is embargoed");
        }

        log.info("Accession safe to download. Accession number: {}", accessionNumber);
        String baseFolder = accession.getArchiveFolder();

        String fullPath = dataFolder + File.separator + baseFolder + File.separator + folderStructure + File.separator + downloadFileName;
        log.info("Full path to accession number [{}]: {}", accessionNumber, fullPath);

        Path path = Paths.get(fullPath);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        log.info("Returning sip for accession number {}", accessionNumber);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
