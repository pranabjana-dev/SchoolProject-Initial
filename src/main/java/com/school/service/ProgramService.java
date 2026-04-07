package com.school.service;

import com.school.model.Program;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ProgramService — in-memory storage (no file system).
 * Default age-based programmes are seeded on startup.
 * Changes persist for the lifetime of the process only.
 */
@Service
public class ProgramService {

    private final List<Program> store = new ArrayList<>();

    public ProgramService() {
        initializeDefaultPrograms();
    }

    private void initializeDefaultPrograms() {
        store.add(new Program(UUID.randomUUID().toString(), "Toddler",
                0, 20, "Introductory programme for very young children",
                "Up to 20 months", true, 1));
        store.add(new Program(UUID.randomUUID().toString(), "Play Group",
                21, 24, "Play-based learning for early childhood development",
                "21 to 24 months", true, 2));
        store.add(new Program(UUID.randomUUID().toString(), "Nursery",
                25, 36, "Foundation year with structured play and early literacy",
                "25 to 36 months", true, 3));
        store.add(new Program(UUID.randomUUID().toString(), "LKG",
                37, 48, "Lower Kindergarten — formal pre-school learning begins",
                "37 to 48 months", true, 4));
        store.add(new Program(UUID.randomUUID().toString(), "UKG",
                49, 60, "Upper Kindergarten — prepares children for Grade 1",
                "49 to 60 months", true, 5));
        System.out.println("[ProgramService] Default programmes loaded into memory.");
    }

    public List<Program> getAll() {
        return store.stream()
                .sorted(Comparator.comparingInt(Program::getDisplayOrder))
                .collect(Collectors.toList());
    }

    public List<Program> getActive() {
        return store.stream()
                .filter(Program::isActive)
                .sorted(Comparator.comparingInt(Program::getDisplayOrder))
                .collect(Collectors.toList());
    }

    public Program save(Program program) {
        if (program.getId() == null || program.getId().isBlank()) {
            program.setId(UUID.randomUUID().toString());
            store.add(program);
        } else {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).getId().equals(program.getId())) {
                    store.set(i, program);
                    return program;
                }
            }
            store.add(program);
        }
        return program;
    }

    public boolean delete(String id) {
        return store.removeIf(p -> p.getId().equals(id));
    }

    public Optional<Program> findById(String id) {
        return store.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public String determineProgramByAge(int ageInMonths) {
        return store.stream()
                .filter(Program::isActive)
                .sorted(Comparator.comparingInt(Program::getMaxAgeMonths))
                .filter(p -> ageInMonths <= p.getMaxAgeMonths())
                .findFirst()
                .map(Program::getName)
                .orElse("Above Programme Age Range");
    }
}
