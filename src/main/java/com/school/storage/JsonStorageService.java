package com.school.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple JSON file storage.
 * Data is stored in ${user.home}/school-data/ directory.
 *
 * On Render free tier, the filesystem resets on redeploy.
 * For persistence across deploys, see README for upgrade options.
 */
@Service
public class JsonStorageService {

    private final ObjectMapper objectMapper;
    private final String dataDir;

    public JsonStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Use DATA_DIR env var if set (for Docker/cloud), else use home dir
        String envDir = System.getenv("DATA_DIR");
        this.dataDir = (envDir != null && !envDir.isBlank())
                ? envDir
                : System.getProperty("user.home") + "/school-data/";

        File dir = new File(this.dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("[Storage] Data directory: " + this.dataDir);
    }

    /**
     * Read all records from a JSON file.
     */
    public <T> List<T> readAll(String fileName, TypeReference<List<T>> type) {
        File file = new File(dataDir + fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            System.err.println("[Storage] Error reading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Write all records to a JSON file (overwrites existing).
     */
    public <T> void writeAll(String fileName, List<T> data) {
        File file = new File(dataDir + fileName);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException("[Storage] Failed to write " + fileName, e);
        }
    }
}
