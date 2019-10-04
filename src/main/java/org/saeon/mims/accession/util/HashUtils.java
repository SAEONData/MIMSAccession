package org.saeon.mims.accession.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.saeon.mims.accession.model.accession.Accession;
import org.saeon.mims.accession.model.accession.FileChecksum;
import org.saeon.mims.accession.repository.FileChecksumRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class HashUtils {

    public static String prepareMD5HashMapForDirectory(String absolutePath) throws IOException {
        //put fileinputstream into hashmap
        HashMap<String, String> md5List = new HashMap<>();
        HashMap<String, Vector<FileInputStream>> directoryContents = new HashMap<>();
        File directory = new File(absolutePath);
        directoryContents.put(directory.getName(), new Vector<>());

        //recursively fill the hashmap
        fillHashMap(md5List, directoryContents, directory);

        directoryContents.forEach((dir,contents)->{
            try (SequenceInputStream sequenceInputStream = new SequenceInputStream(contents.elements())) {
                md5List.put(dir, DigestUtils.md5Hex(sequenceInputStream).toUpperCase());
            } catch (IOException e) {

            }
        });

        boolean writtenFile = writeMD5ChecksumFile(absolutePath, md5List);

        String overallMD5 = md5List.get(directory.getName());
        return overallMD5;

    }

    private static boolean writeMD5ChecksumFile(String path, HashMap<String, String> md5List) throws IOException {
        String fileName = path + "/MD5CHECKSUM";
        AtomicBoolean success = new AtomicBoolean(true);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName), "utf-8"))) {
            md5List.forEach((file, md5) -> {
                try {
                    writer.write(file + ": " + md5);
                    writer.newLine();
                } catch (IOException e) {
                    success.set(false);
                    e.printStackTrace();
                }
            });

        }

        return success.get();
    }

    private static void fillHashMap(HashMap<String, String> md5List, HashMap<String, Vector<FileInputStream>> directoryContents, File directory) throws IOException {
        File[] files = directory.listFiles();

        for (File file: files) {
            if (file.isDirectory()) {
                directoryContents.put(file.getName(), new Vector<>());
                fillHashMap(md5List, directoryContents, file);
            } else {
                directoryContents.get(directory.getName()).add(new FileInputStream(file));
                md5List.put(file.getName(), DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase());
            }
        }

    }

    public static void generateMD5Checksum(File file, Accession accession, FileChecksumRepository fileChecksumRepository) throws IOException {
        //for each file/directory in the collection, create new FileChecksum
        //exclude MD5CHECKSUM if it exists

        HashMap<String, String> md5List = new HashMap<>();
        HashMap<String, Vector<FileInputStream>> directoryContents = new HashMap<>();
        directoryContents.put(file.getName(), new Vector<>());

        //recursively fill the hashmap
        fillHashMap(md5List, directoryContents, file);

        directoryContents.forEach((dir,contents)->{
            try (SequenceInputStream sequenceInputStream = new SequenceInputStream(contents.elements())) {
                md5List.put(dir, DigestUtils.md5Hex(sequenceInputStream).toUpperCase());
            } catch (IOException e) {

            }
        });

        writeMD5ChecksumFile(file.getAbsolutePath(), md5List);

        //iterate over md5List and fill out the FileChecksum fields
        accession.setFiles(new ArrayList<>());
        md5List.forEach((fileName, md5) -> {
            if (!fileName.equalsIgnoreCase("MD5CHECKSUM")) {

                FileChecksum fcs = fileChecksumRepository.findTop1ByChecksum(md5);
                if (fcs == null) {
                    fcs = new FileChecksum();
                    fcs.setAccession(new ArrayList<>());
                }
                fcs.getAccession().add(accession);
                fcs.setChecksum(md5);
                fcs.setFileName(fileName);
                accession.getFiles().add(fcs);

            }
        });

    }
}
