package org.saeon.mims.accession.service.accession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.saeon.mims.accession.exception.AccessionException;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.AccessionNumber;
import org.saeon.mims.accession.repository.AccessionNumberRepository;
import org.saeon.mims.accession.repository.AccessionRepository;
import org.saeon.mims.accession.repository.FileChecksumRepository;
import org.saeon.mims.accession.request.IngestRequest;
import org.saeon.mims.accession.util.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
@Slf4j
public class AccessionService {

    @Value("${base.folder}")
    private String baseFolder;

    @Autowired private AccessionRepository accessionRepository;
    @Autowired private AccessionNumberRepository accessionNumberRepository;
    @Autowired private FileChecksumRepository fileChecksumRepository;

    public Accession ingestAccession(Accession accession) throws IOException {
        String fileFolder = accession.getHomeFolder();
        File file = new File(baseFolder + File.separator + fileFolder);
        log.debug("File name: {}", file.getAbsolutePath());
        log.debug("Type: {}", file.isDirectory() ? "Directory" : file.isFile() ? "File" : "Unknown");
        log.debug("Access Rights: {} | {} | {}",
                file.canRead() ? "Read Allowed" : "Read Denied",
                file.canWrite() ? "Write Allowed" : "Write Denied",
                file.canExecute() ? "Execute Allowed" : "Execute Denied");

        if (file.isDirectory()) {
            HashUtils.generateMD5Checksum(file, accession, fileChecksumRepository);
            accessionRepository.save(accession);
            accession.setAccessionNumber(getNextAccessionNumber());
            accessionRepository.save(accession);

        }

        //todo-acc need to move the accession to the correct folder structure.

        return accession;
    }

    private synchronized Long getNextAccessionNumber() {
        AccessionNumber an = accessionNumberRepository.findById(1).orElse(null);
        if (an != null) {
            Long accNum = an.getNextAccessionNumber();
            an.setNextAccessionNumber(an.getNextAccessionNumber() + 1);
            accessionNumberRepository.save(an);
            return accNum;
        } else {
            //throw new AccessionException
        }
        return 1L;
    }

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

    public void populateNextAccessionNumber(String nextAccessionNumber) {
        Long accNum = Long.parseLong(nextAccessionNumber);
        //todo-acc error handling for parsing the string
        AccessionNumber an = accessionNumberRepository.findById(1).orElse(null);
        if (an == null) {
            an = new AccessionNumber();
            an.setId(1);
            an.setNextAccessionNumber(accNum);
            accessionNumberRepository.save(an);
        }
    }
}
