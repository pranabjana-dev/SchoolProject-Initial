package com.school.controller;

import com.school.model.Program;
import com.school.service.ProgramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private final ProgramService service;

    public ProgramController(ProgramService service) {
        this.service = service;
    }

    @GetMapping
    public List<Program> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<Program> getActive() {
        return service.getActive();
    }

    @PostMapping
    public Program create(@RequestBody Program program) {
        return service.save(program);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Program> update(@PathVariable String id,
                                          @RequestBody Program program) {
        program.setId(id);
        return ResponseEntity.ok(service.save(program));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        return deleted
                ? ResponseEntity.ok("Deleted successfully")
                : ResponseEntity.notFound().build();
    }
}
