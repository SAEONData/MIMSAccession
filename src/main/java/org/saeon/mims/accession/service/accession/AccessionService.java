package org.saeon.mims.accession.service.accession;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.saeon.mims.accession.exception.AccessionException;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.AccessionNumber;
import org.saeon.mims.accession.model.accession.Status;
import org.saeon.mims.accession.repository.AccessionNumberRepository;
import org.saeon.mims.accession.repository.AccessionRepository;
import org.saeon.mims.accession.repository.FileChecksumRepository;
import org.saeon.mims.accession.request.IngestRequest;
import org.saeon.mims.accession.service.odp.ODPService;
import org.saeon.mims.accession.util.CopyFileVisitor;
import org.saeon.mims.accession.util.HashUtils;
import org.saeon.mims.accession.util.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class AccessionService {

    @Value("${base.folder}")
    private String baseIngestFolder;

    @Value("${data.folder}")
    private String baseDataFolder;

    @Value("${use.odp}")
    private boolean useODP;

    @Autowired private AccessionRepository accessionRepository;
    @Autowired private AccessionNumberRepository accessionNumberRepository;
    @Autowired private FileChecksumRepository fileChecksumRepository;
    @Autowired private ODPService odpService;

    public Accession ingestAccession(Accession accession) throws IOException, AccessionException {
        try {
            accession.setStatus(Status.PENDING);
            accession.setUuid(UUID.randomUUID().toString());
            accessionRepository.save(accession);
            log.debug("Accession uuid: {}", accession.getUuid());
            String fileFolder = accession.getHomeFolder();
            File file = new File(baseIngestFolder + File.separator + fileFolder);
            log.debug("File name: {}", file.getAbsolutePath());
            log.debug("Type: {}", file.isDirectory() ? "Directory" : file.isFile() ? "File" : "Unknown");
            log.debug("Access Rights: {} | {} | {}",
                    file.canRead() ? "Read Allowed" : "Read Denied",
                    file.canWrite() ? "Write Allowed" : "Write Denied",
                    file.canExecute() ? "Execute Allowed" : "Execute Denied");

            if (file.isDirectory()) {
                log.info("Accession starting");
                accession.setStatus(Status.IN_PROGRESS);
                HashUtils.generateMD5Checksum(file, accession, fileChecksumRepository);
                accessionRepository.save(accession);
                synchronized (accession) {
                    accession.setAccessionNumber(getNextAccessionNumber());
                    log.info("Setting accesion number: {}", accession.getAccessionNumber());
                }
                accessionRepository.save(accession);

            } else {
                accession.setStatus(Status.FAILED);
                accessionRepository.save(accession);
                throw new AccessionException(400, "The accession requested is not a directory");
            }


            log.info("Connect to ODP? {}", useODP ? "Yes" : "No");
            if (useODP) {
                try {
                    log.info("Contacting ODP");
                    int responseStatus = odpService.addAccessionToODP(accession);
                    log.info("Response from ODP: {}", responseStatus);

                } catch (SocketException e) {
                    accession.setStatus(Status.FAILED);
                    accessionRepository.save(accession);
                    throw new AccessionException(404, "ODP Server is unavailable. Please try again later.");
                }
            }

            //create the top-level folder
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String topLevelFolder = sdf.format(new Date()) + "-" + accession.getUuid();

            accession.setArchiveFolder(topLevelFolder);

            String nextFolder = baseDataFolder + "/" + topLevelFolder + "/" + "V0.0";

            Path newDirectoryPath = Paths.get(nextFolder);
            try {
                Files.createDirectories(newDirectoryPath);
                Path aboutDirectoryPath = Paths.get(newDirectoryPath.toString(), "about");
                Path dataDirectoryPath = Paths.get(newDirectoryPath.toString(), "data");
                Files.createDirectories(aboutDirectoryPath);
                Files.createDirectories(dataDirectoryPath);

                Path sipDirectoryPath = Paths.get(dataDirectoryPath.toString(), "0-Data");
                Path aipDirectoryPath = Paths.get(dataDirectoryPath.toString(), "1-Data");
                Files.createDirectories(sipDirectoryPath);
                Files.createDirectories(aipDirectoryPath);

                //new thread possibly?
                log.info("Copying from {} to {}", file.toPath().toString(), aipDirectoryPath.toString());
                Files.walkFileTree(file.toPath(), new CopyFileVisitor(aipDirectoryPath));
                log.info("Files copied");

                //time to zip for the SIP
                //also think about threading this bitch
                String outputZipFile = sipDirectoryPath.toString() + File.separator + "sip.zip";
                new ZipUtils().createSIPZip(file, outputZipFile);

                accession.setStatus(Status.COMPLETED);
                accessionRepository.save(accession);

            } catch (NoSuchFileException ex) {
                accession.setStatus(Status.FAILED);
                accessionRepository.save(accession);
                log.error("Error creating base folder", ex);
                throw ex;
            }

            return accession;
        } catch (Exception e) {
            if (e instanceof AccessionException) {
                throw (AccessionException) e;
            } else {
                throw new AccessionException(500, "An error occurred during accessioning.", e);
            }
        }
    }

    public Accession updateAccession(Accession accession) throws  AccessionException {
        try {
            accessionRepository.save(accession);
        } catch (Exception e){
            log.error("Coult not update accession");
            if (e instanceof AccessionException) {
                throw (AccessionException) e;
            } else {
                throw new AccessionException(500, "An error occured while tryingto update accession");
            }
        }
        return accession;
    }

    public boolean isAccessionNumberPopulated() {
        AccessionNumber an = accessionNumberRepository.findById(1).orElse(null);
        return an != null;
    }

    private Long getNextAccessionNumber() throws AccessionException {
        Long accNum = 1L;
        synchronized (accessionNumberRepository) {
            AccessionNumber an = accessionNumberRepository.findById(1).orElse(null);
            if (an != null) {
                accNum = an.getNextAccessionNumber();
                an.setNextAccessionNumber(an.getNextAccessionNumber() + 1);
                accessionNumberRepository.save(an);
                return accNum;
            } else {
                throw new AccessionException(500, "Next accession number is not populated");
            }

        }
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

    public List<Accession> getAllAccessions() {
        Iterable<Accession> accessions = accessionRepository.findAll();
        return StreamSupport.stream(accessions.spliterator(), false).collect(Collectors.toList());
    }

    public Accession getAccessionByAccessionNumber(Long accessionNumber) {
        return accessionRepository.findDistinctByAccessionNumber(accessionNumber);
    }
}
