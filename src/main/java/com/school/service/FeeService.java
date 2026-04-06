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
        initializeDefaultFees();
    }

    /** Seed default fee structures for AY 2026-27 if none exist. */
    private void initializeDefaultFees() {
        List<FeeStructure> existing = getAll();
        if (!existing.isEmpty()) return;
        long now = System.currentTimeMillis();
        List<FeeStructure> defaults = Arrays.asList(
            makeFee("Toddler",   "2026-27", 10000, 4000, 600, 2400, now),
            makeFee("Play Group","2026-27", 10000, 4200, 600, 2400, now),
            makeFee("Nursery",   "2026-27", 10000, 4500, 600, 2400, now),
            makeFee("LKG",       "2026-27", 10000, 5100, 600, 2400, now),
            makeFee("UKG",       "2026-27", 10000, 5600, 600, 2400, now)
        );
        storage.writeAll(FILE, defaults);
        System.out.println("[FeeService] Default fee structures initialized for AY 2026-27.");
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

    /** Get all fee structures. */
    public List<FeeStructure> getAll() {
        long now = System.currentTimeMillis();
	List<FeeStructure> defaults = Arrays.asList(
            makeFee("Toddler",   "2026-27", 10000, 4000, 600, 2400, now),
            makeFee("Play Group","2026-27", 10000, 4200, 600, 2400, now),
            makeFee("Nursery",   "2026-27", 10000, 4500, 600, 2400, now),
            makeFee("LKG",       "2026-27", 10000, 5100, 600, 2400, now),
            makeFee("UKG",       "2026-27", 10000, 5600, 600, 2400, now)
        );
	return defaults;
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

        Optional<FeeStructure> exact = all.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName())
                          && academicYear.equals(f.getAcademicYear()))
                .findFirst();
        if (exact.isPresent()) return exact;

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
