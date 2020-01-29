package com.jasper.model.file;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class FileLoader {

    private final static Logger LOG = LoggerFactory.getLogger(FileLoader.class);

    @Nonnull
    //relative path from this location.
    public static String loadFile(@Nonnull String inputFile) {

        URL resource = FileLoader.class.getResource("../../../../" + inputFile);
        StringBuilder contentOfFile = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                contentOfFile.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentOfFile.toString();
    }
}