package com.school.controller;

import com.school.model.Discount;
import com.school.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @GetMapping
    public List<Discount> getAll() {
        return service.getAll();
    }

    @GetMapping("/active")
    public List<Discount> getActive() {
        return service.getActive();
    }

    @PostMapping
    public Discount create(@RequestBody Discount discount) {
        return service.save(discount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Discount> update(@PathVariable String id,
                                           @RequestBody Discount discount) {
        discount.setId(id);
        return ResponseEntity.ok(service.save(discount));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Discount> toggle(@PathVariable String id) {
        return service.toggleActive(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        boolean deleted = service.delete(id);
        return deleted
                ? ResponseEntity.ok("Deleted")
                : ResponseEntity.notFound().build();
    }
}
