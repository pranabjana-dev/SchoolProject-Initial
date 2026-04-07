package com.school.service;

import com.school.model.FeeStructure;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * FeeService — in-memory storage (no file system).
 * Data is seeded from hardcoded defaults on startup.
 * Changes persist for the lifetime of the process only.
 */
@Service
public class FeeService {

    private final List<FeeStructure> store = new ArrayList<>();

    public FeeService() {
        initializeDefaultFees();
    }

    private void initializeDefaultFees() {
        long now = System.currentTimeMillis();
        store.add(makeFee("Toddler",    "2026-27", 10000, 4000, 600, 2400, now));
        store.add(makeFee("Play Group", "2026-27", 10000, 4200, 600, 2400, now));
        store.add(makeFee("Nursery",    "2026-27", 10000, 4500, 600, 2400, now));
        store.add(makeFee("LKG",        "2026-27", 10000, 5100, 600, 2400, now));
        store.add(makeFee("UKG",        "2026-27", 10000, 5600, 600, 2400, now));
        System.out.println("[FeeService] Default fee structures loaded into memory.");
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
        // Exact match first
        Optional<FeeStructure> exact = store.stream()
                .filter(f -> programName.equalsIgnoreCase(f.getProgramName())
                          && academicYear.equals(f.getAcademicYear()))
                .findFirst();
        if (exact.isPresent()) return exact;

        // Fallback: latest for this program regardless of year
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
                    return fee;
                }
            }
            // Not found — add as new
            store.add(fee);
        }
        return fee;
    }

    public boolean delete(String id) {
        return store.removeIf(f -> f.getId().equals(id));
    }

    public List<String> getAcademicYears() {
        return store.stream()
                .map(FeeStructure::getAcademicYear)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
