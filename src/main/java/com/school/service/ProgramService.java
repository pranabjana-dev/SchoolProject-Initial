package com.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.school.model.Program;
import com.school.storage.JsonStorageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgramService {

    private static final String FILE = "programs.json";
    private final JsonStorageService storage;

    public ProgramService(JsonStorageService storage) {
        this.storage = storage;
        initializeDefaultPrograms();
    }

    /**
     * Seed the default age-based programs if none exist.
     * These match the govt-defined age criteria.
     */
    private void initializeDefaultPrograms() {
        List<Program> existing = getAll();
        if (!existing.isEmpty()) return;

        List<Program> defaults = Arrays.asList(
            new Program(
                UUID.randomUUID().toString(), "Toddler",
                0, 20,
                "Introductory program for very young children",
                "Up to 20 months", true, 1),
            new Program(
                UUID.randomUUID().toString(), "Play Group",
                21, 24,
                "Play-based learning for early childhood development",
                "21 to 24 months", true, 2),
            new Program(
                UUID.randomUUID().toString(), "Nursery",
                25, 36,
                "Foundation year with structured play and early literacy",
                "25 to 36 months", true, 3),
            new Program(
                UUID.randomUUID().toString(), "LKG",
                37, 48,
                "Lower Kindergarten — formal pre-school learning begins",
                "37 to 48 months", true, 4),
            new Program(
                UUID.randomUUID().toString(), "UKG",
                49, 60,
                "Upper Kindergarten — prepares children for Grade 1",
                "49 to 60 months", true, 5)
        );

        storage.writeAll(FILE, defaults);
        System.out.println("[ProgramService] Default programs initialized.");
    }

    /** Get all programs sorted by display order. */
    public List<Program> getAll() {
        return storage.readAll(FILE, new TypeReference<List<Program>>() {})
                .stream()
                .sorted(Comparator.comparingInt(Program::getDisplayOrder))
                .collect(Collectors.toList());
    }

    /** Get only active programs. */
    public List<Program> getActive() {
        return getAll().stream().filter(Program::isActive).collect(Collectors.toList());
    }

    /** Create or update a program. */
    public Program save(Program program) {
        List<Program> all = new ArrayList<>(getAll());
        if (program.getId() == null || program.getId().isBlank()) {
            program.setId(UUID.randomUUID().toString());
            all.add(program);
        } else {
            all.replaceAll(p -> p.getId().equals(program.getId()) ? program : p);
        }
        storage.writeAll(FILE, all);
        return program;
    }

    /** Delete a program by ID. */
    public boolean delete(String id) {
        List<Program> all = new ArrayList<>(getAll());
        boolean removed = all.removeIf(p -> p.getId().equals(id));
        if (removed) storage.writeAll(FILE, all);
        return removed;
    }

    /** Find program by ID. */
    public Optional<Program> findById(String id) {
        return getAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    /**
     * Determine the program name for a given age in months.
     * Uses the active programs sorted by maxAgeMonths ascending.
     * Returns the first program whose maxAgeMonths >= ageInMonths.
     */
    public String determineProgramByAge(int ageInMonths) {
        List<Program> sorted = getActive().stream()
                .sorted(Comparator.comparingInt(Program::getMaxAgeMonths))
                .collect(Collectors.toList());

        for (Program program : sorted) {
            if (ageInMonths <= program.getMaxAgeMonths()) {
                return program.getName();
            }
        }
        return "Above Programme Age Range";
    }
}
