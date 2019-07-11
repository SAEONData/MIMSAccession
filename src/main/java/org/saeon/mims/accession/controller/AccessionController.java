package org.saeon.mims.accession.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.saeon.mims.accession.request.IngestRequest;
import org.saeon.mims.accession.util.HashUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

@RestController(value = "/")
@Slf4j
public class AccessionController {

    @Value("${base.folder}")
    private String basefolder;

    @GetMapping
    public String get() {
        log.info("Base folder: {}", basefolder);
        return "OK";

    }

    @PostMapping(value = "/ingest", headers = "Accept=application/json")
    public ResponseEntity register(@RequestBody IngestRequest ingestRequest) {
        String fileFolder = ingestRequest.getIngestObject();
        File file = new File(basefolder + "\\" + fileFolder);
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
                e.printStackTrace();
            }
        } else if (file.isDirectory()){
            try {
//                String md5 = HashUtils.hashDirectory(file.getAbsolutePath(), true);
                String md5 = HashUtils.prepareMD5HashMapForDirectory(file.getAbsolutePath());
                log.info("MD5 for folder [{}] is {}", file.getName(), md5);
                body.append(md5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("File is not a directory or file. Probably does not exist. Returning error");
            return ResponseEntity.status(422).body(new Gson().toJson("Ingest object is not a file or folder"));
        }

        return ResponseEntity.ok(new Gson().toJson(body.toString()));
    }

}
