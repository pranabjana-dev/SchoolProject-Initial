package com.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.school.model.Discount;
import com.school.storage.JsonStorageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiscountService {

    private static final String FILE = "discounts.json";
    private final JsonStorageService storage;

    public DiscountService(JsonStorageService storage) {
        this.storage = storage;
    }

    /** Get all discounts. */
    public List<Discount> getAll() {
        return storage.readAll(FILE, new TypeReference<List<Discount>>() {});
    }

    /** Get only active discounts. */
    public List<Discount> getActive() {
        return getAll().stream().filter(Discount::isActive).collect(Collectors.toList());
    }

    /** Find discount by ID. */
    public Optional<Discount> findById(String id) {
        return getAll().stream().filter(d -> id.equals(d.getId())).findFirst();
    }

    /** Create or update a discount. */
    public Discount save(Discount discount) {
        List<Discount> all = new ArrayList<>(getAll());

        if (discount.getId() == null || discount.getId().isBlank()) {
            discount.setId(UUID.randomUUID().toString());
            discount.setCreatedAt(System.currentTimeMillis());
            all.add(discount);
        } else {
            all.replaceAll(d -> d.getId().equals(discount.getId()) ? discount : d);
        }
        storage.writeAll(FILE, all);
        return discount;
    }

    /** Delete a discount by ID. */
    public boolean delete(String id) {
        List<Discount> all = new ArrayList<>(getAll());
        boolean removed = all.removeIf(d -> id.equals(d.getId()));
        if (removed) storage.writeAll(FILE, all);
        return removed;
    }

    /** Toggle active status. */
    public Optional<Discount> toggleActive(String id) {
        Optional<Discount> found = findById(id);
        found.ifPresent(d -> {
            d.setActive(!d.isActive());
            save(d);
        });
        return found;
    }

    /**
     * Calculate the discount amount for a given list of discount IDs
     * against a total base fee.
     */
    public double calculateTotal(List<String> ids, double totalBaseFees) {
        if (ids == null || ids.isEmpty()) return 0.0;
        double total = 0.0;
        for (String id : ids) {
            Optional<Discount> d = findById(id);
            if (d.isPresent()) {
                Discount disc = d.get();
                if ("FIXED".equalsIgnoreCase(disc.getDiscountType())) {
                    total += disc.getValue();
                } else if ("PERCENTAGE".equalsIgnoreCase(disc.getDiscountType())) {
                    total += totalBaseFees * disc.getValue() / 100.0;
                }
            }
        }
        return total;
    }
}
