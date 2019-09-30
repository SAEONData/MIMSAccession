package org.saeon.mims.accession.service.accession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.saeon.mims.accession.exception.AccessionException;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.repository.AccessionRepository;
import org.saeon.mims.accession.request.IngestRequest;
import org.saeon.mims.accession.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@Slf4j
public class AccessionService {

    @Autowired private AccessionRepository accessionRepository;

    public Accession ingestAccessionAPI(IngestRequest ingestRequest, String basefolder) throws AccessionException {
        Accession accession = new Accession();

        String fileFolder = ingestRequest.getIngestObject();
        File file = new File(basefolder + File.separator + fileFolder);
        StringBuilder body = new StringBuilder();
        log.info("File name: {}", file.getAbsolutePath());
        log.info("Type: {}", file.isDirectory() ? "Directory" : file.isFile() ? "File" : "Unknown");
        log.info("Access Rights: {} | {} | {}",
                file.canRead() ? "Read Allowed" : "Read Denied",
                file.canWrite() ? "Write Allowed" : "Write Denied",
                file.canExecute() ? "Execute Allowed" : "Execute Denied");

        if (file.isFile()) {
            String md5;
            try {
                md5 = DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
                log.info("MD5 checksum: {}", md5);
                body.append(md5);
            } catch (IOException e) {
                throw new AccessionException(500, "MD5Checksum for file [" + file.getName() + "] could not be calculated\n", e);
            }

        } else if (file.isDirectory()) {
            try {
                String md5 = HashUtils.prepareMD5HashMapForDirectory(file.getAbsolutePath());
                log.info("MD5 for folder [{}] is {}", file.getName(), md5);
                body.append(md5);
            } catch (IOException e) {
                throw new AccessionException(500, "MD5Checksum for folder [" + file.getName() + "] could not be calculated", e);
            }
        } else {
            log.info("File is not a directory or file. Probably does not exist. Returning error");
            throw new AccessionException(422, "Ingest object is not a file or folder");
        }

        return accession;
    }
}
