package com.school.controller;

import com.school.model.FeeStructure;
import com.school.service.FeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService service;

    public FeeController(FeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<FeeStructure> getAll() {
        return service.getAll();
    }

    @GetMapping("/years")
    public List<String> getAcademicYears() {
        return service.getAcademicYears();
    }

    @GetMapping("/year/{year}")
    public List<FeeStructure> getByYear(@PathVariable String year) {
        return service.getByAcademicYear(year);
    }

    @PostMapping
    public FeeStructure create(@RequestBody FeeStructure fee) {
        return service.save(fee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeStructure> update(@PathVariable String id,
                                               @RequestBody FeeStructure fee) {
        fee.setId(id);
        return ResponseEntity.ok(service.save(fee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        return deleted
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}
