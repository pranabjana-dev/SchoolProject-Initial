package com.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.school.model.FeeStructure;
import com.school.storage.JsonStorageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private static final String FILE = "fees.json";
    private final JsonStorageService storage;

    public FeeService(JsonStorageService storage) {
        this.storage = storage;
    }

    /** Get all fee structures. */
    public List<FeeStructure> getAll() {
        return storage.readAll(FILE, new TypeReference<List<FeeStructure>>() {});
    }

    /** Get fee structures for a specific academic year. */
    public List<FeeStructure> getByAcademicYear(String year) {
        return getAll().stream()
                .filter(f -> year.equals(f.getAcademicYear()))
                .collect(Collectors.toList());
    }

    /**
     * Get fee structure for a specific program and academic year.
     * Falls back to the latest configured fee if no exact year match.
     */
    public Optional<FeeStructure> getForProgram(String programName, String academicYear) {
        List<FeeStructure> all = getAll();

        // Exact match on program + academic year
        Optional<FeeStructure> exact = all.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName())
                          && academicYear.equals(f.getAcademicYear()))
                .findFirst();
        if (exact.isPresent()) return exact;

        // Fallback: latest fee for this program regardless of year
        return all.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName()))
                .max(Comparator.comparingLong(FeeStructure::getUpdatedAt));
    }

    /** Create or update a fee structure. */
    public FeeStructure save(FeeStructure fee) {
        List<FeeStructure> all = new ArrayList<>(getAll());
        fee.setUpdatedAt(System.currentTimeMillis());

        if (fee.getId() == null || fee.getId().isBlank()) {
            fee.setId(UUID.randomUUID().toString());
            all.add(fee);
        } else {
            all.replaceAll(f -> f.getId().equals(fee.getId()) ? fee : f);
        }
        storage.writeAll(FILE, all);
        return fee;
    }

    /** Delete a fee structure. */
    public boolean delete(String id) {
        List<FeeStructure> all = new ArrayList<>(getAll());
        boolean removed = all.removeIf(f -> f.getId().equals(id));
        if (removed) storage.writeAll(FILE, all);
        return removed;
    }

    /** Get all distinct academic years configured. */
    public List<String> getAcademicYears() {
        return getAll().stream()
                .map(FeeStructure::getAcademicYear)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
