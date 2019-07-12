package org.saeon.mims.accession.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class HashUtils {

//    public static String hashDirectory(String directoryPath, boolean includeHiddenFiles) throws IOException {
//        File directory = new File(directoryPath);
//        StringBuilder sb = new StringBuilder();
//
//        if (!directory.isDirectory())
//        {
//            throw new IllegalArgumentException("Not a directory");
//        }
//
//        Vector<FileInputStream> fileStreams = new Vector<>();
//        collectFiles(directory, fileStreams, includeHiddenFiles, sb);
//
//        try (SequenceInputStream sequenceInputStream = new SequenceInputStream(fileStreams.elements())) {
//            sb.append("\n").append(DigestUtils.md5Hex(sequenceInputStream));
//            return sb.toString();
//        }
//    }
//
//    private static void collectFiles(File directory,
//                                     List<FileInputStream> fileInputStreams,
//                                     boolean includeHiddenFiles,
//                                     StringBuilder sb) throws IOException {
//
//        File[] files = directory.listFiles();
//
//        if (files != null) {
//            Arrays.sort(files, Comparator.comparing(File::getName));
//
//            for (File file : files) {
//                if (includeHiddenFiles || !Files.isHidden(file.toPath())) {
//                    if (file.isDirectory()) {
//                        //need a new vector here to calculate the md5 of each folder
//                        collectFiles(file, fileInputStreams, includeHiddenFiles, sb);
//                    } else {
//                        fileInputStreams.add(new FileInputStream(file));
//                        String md5 = DigestUtils.md5Hex(new FileInputStream(file)).toUpperCase();
//                        log.info("MD5 for file [{}] is {}", file.getName(), md5);
//                        sb.append("\n").append(file.getName()).append(": ").append(md5);
//                    }
//                }
//            }
//        }
//    }

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
                md5List.put(dir, DigestUtils.md5Hex(sequenceInputStream));
            } catch (IOException e) {

            }
        });

        boolean writtenFile = writeMD5ChecksumFile(absolutePath, md5List);

        String overallMD5 = "";
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
}
