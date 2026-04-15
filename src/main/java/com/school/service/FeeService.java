package com.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.school.model.FeeStructure;
import com.school.storage.JsonStorageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FeeService — hybrid storage.
 * On startup: loads from JSON file if data exists, otherwise seeds hardcoded defaults.
 * All writes go to JSON file (best-effort; non-fatal if storage unavailable).
 * In-memory list is always the source of truth at runtime.
 */
@Service
public class FeeService {

    private static final String FILE = "fees.json";

    private final JsonStorageService storage;
    private final List<FeeStructure> store = new ArrayList<>();

    public FeeService(JsonStorageService storage) {
        this.storage = storage;
        loadOrSeedDefaults();
    }

    private void loadOrSeedDefaults() {
        List<FeeStructure> fromFile = storage.readAll(FILE, new TypeReference<List<FeeStructure>>() {});
        if (fromFile != null && !fromFile.isEmpty()) {
            store.addAll(fromFile);
            System.out.println("[FeeService] Loaded " + store.size() + " fee structures from file.");
        } else {
            seedDefaults();
            System.out.println("[FeeService] No file data — using hardcoded defaults.");
        }
    }

    private void seedDefaults() {
        long now = System.currentTimeMillis();
        store.add(makeFee("Toddler",    "2026-27", 10000, 4000, 600, 2400, now));
        store.add(makeFee("Play Group", "2026-27", 10000, 4200, 600, 2400, now));
        store.add(makeFee("Nursery",    "2026-27", 10000, 4500, 600, 2400, now));
        store.add(makeFee("LKG",        "2026-27", 10000, 5100, 600, 2400, now));
        store.add(makeFee("UKG",        "2026-27", 10000, 5600, 600, 2400, now));
    }

    private FeeStructure makeFee(String programName, String academicYear,
                                  double fixed, double session,
                                  double sports, double celebration, long ts) {
        FeeStructure f = new FeeStructure();
        f.setId(UUID.randomUUID().toString());
        f.setProgramName(programName);
        f.setProgramId(programName);
        f.setAcademicYear(academicYear);
        f.setFixedComponent(fixed);
        f.setSessionFees(session);
        f.setSportsFees(sports);
        f.setCelebrationFees(celebration);
        f.setUpdatedAt(ts);
        return f;
    }

    public List<FeeStructure> getAll() {
        return Collections.unmodifiableList(store);
    }

    public List<FeeStructure> getByAcademicYear(String year) {
        return store.stream()
                .filter(f -> year.equals(f.getAcademicYear()))
                .collect(Collectors.toList());
    }

    public Optional<FeeStructure> getForProgram(String programName, String academicYear) {
        Optional<FeeStructure> exact = store.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName())
                          && academicYear.equals(f.getAcademicYear()))
                .findFirst();
        if (exact.isPresent()) return exact;

        return store.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName()))
                .max(Comparator.comparingLong(FeeStructure::getUpdatedAt));
    }

    public FeeStructure save(FeeStructure fee) {
        fee.setUpdatedAt(System.currentTimeMillis());
        if (fee.getId() == null || fee.getId().isBlank()) {
            fee.setId(UUID.randomUUID().toString());
            store.add(fee);
        } else {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).getId().equals(fee.getId())) {
                    store.set(i, fee);
                    persist();
                    return fee;
                }
            }
            store.add(fee);
        }
        persist();
        return fee;
    }

    public boolean delete(String id) {
        boolean removed = store.removeIf(f -> f.getId().equals(id));
        if (removed) persist();
        return removed;
    }

    public List<String> getAcademicYears() {
        return store.stream()
                .map(FeeStructure::getAcademicYear)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private void persist() {
        storage.writeAll(FILE, new ArrayList<>(store));
    }
}
