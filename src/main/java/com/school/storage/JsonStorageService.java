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
 * JsonStorageService — reads/writes JSON files to DATA_DIR.
 * Used by FeeService and DiscountService for hybrid storage:
 *   - If file exists and has data → use it
 *   - If file missing or empty   → caller falls back to hardcoded defaults
 */
@Service
public class JsonStorageService {

    private final ObjectMapper objectMapper;
    private final String dataDir;

    public JsonStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String envDir = System.getenv("DATA_DIR");
        this.dataDir = (envDir != null && !envDir.isBlank())
                ? envDir
                : System.getProperty("user.home") + "/school-data/";

        File dir = new File(this.dataDir);
        if (!dir.exists()) dir.mkdirs();

        System.out.println("[Storage] Data directory: " + this.dataDir);
    }

    /**
     * Read all records from a JSON file.
     * Returns empty list if file doesn't exist or is unreadable.
     */
    public <T> List<T> readAll(String fileName, TypeReference<List<T>> type) {
        File file = new File(dataDir + fileName);
        if (!file.exists()) return new ArrayList<>();
        try {
            List<T> data = objectMapper.readValue(file, type);
            return data != null ? data : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("[Storage] Error reading " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Write all records to a JSON file (overwrites existing).
     * Silently skips if storage is unavailable (e.g. Render free tier).
     */
    public <T> void writeAll(String fileName, List<T> data) {
        File file = new File(dataDir + fileName);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            System.err.println("[Storage] Could not write " + fileName + " (non-fatal): " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return new File(dataDir).canWrite();
    }
}
