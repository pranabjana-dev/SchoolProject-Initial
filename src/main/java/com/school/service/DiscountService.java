package com.school.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.school.model.Discount;
import com.school.storage.JsonStorageService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DiscountService — hybrid storage.
 * On startup: loads from JSON file if data exists, otherwise seeds hardcoded defaults.
 * All writes go to JSON file (best-effort; non-fatal if storage unavailable).
 * In-memory list is always the source of truth at runtime.
 */
@Service
public class DiscountService {

    private static final String FILE = "discounts.json";

    private final JsonStorageService storage;
    private final List<Discount> store = new ArrayList<>();

    public DiscountService(JsonStorageService storage) {
        this.storage = storage;
        loadOrSeedDefaults();
    }

    private void loadOrSeedDefaults() {
        List<Discount> fromFile = storage.readAll(FILE, new TypeReference<List<Discount>>() {});
        if (fromFile != null && !fromFile.isEmpty()) {
            store.addAll(fromFile);
            System.out.println("[DiscountService] Loaded " + store.size() + " discounts from file.");
        } else {
            seedDefaults();
            System.out.println("[DiscountService] No file data — using hardcoded defaults.");
        }
    }

    private void seedDefaults() {
        long now = System.currentTimeMillis();
        store.add(makeDiscount("Loyalty Discount",    "LOYALTY",    "FIXED", 6000,
                               "ALL", "Returning student loyalty discount", now));
        store.add(makeDiscount("Early Bird Discount", "EARLY_BIRD", "FIXED", 3000,
                               "ALL", "Early admission discount offer",     now));
    }

    private Discount makeDiscount(String name, String category, String type,
                                   double value, String year, String desc, long ts) {
        Discount d = new Discount();
        d.setId(UUID.randomUUID().toString());
        d.setName(name);
        d.setCategory(category);
        d.setDiscountType(type);
        d.setValue(value);
        d.setAcademicYear(year);
        d.setDescription(desc);
        d.setActive(true);
        d.setCreatedAt(ts);
        return d;
    }

    public List<Discount> getAll() {
        return Collections.unmodifiableList(store);
    }

    public List<Discount> getActive() {
        return store.stream().filter(Discount::isActive).collect(Collectors.toList());
    }

    public Optional<Discount> findById(String id) {
        return store.stream().filter(d -> id.equals(d.getId())).findFirst();
    }

    public Discount save(Discount discount) {
        if (discount.getId() == null || discount.getId().isBlank()) {
            discount.setId(UUID.randomUUID().toString());
            discount.setCreatedAt(System.currentTimeMillis());
            store.add(discount);
        } else {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).getId().equals(discount.getId())) {
                    store.set(i, discount);
                    persist();
                    return discount;
                }
            }
            store.add(discount);
        }
        persist();
        return discount;
    }

    public boolean delete(String id) {
        boolean removed = store.removeIf(d -> id.equals(d.getId()));
        if (removed) persist();
        return removed;
    }

    public Optional<Discount> toggleActive(String id) {
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equals(id)) {
                Discount d = store.get(i);
                d.setActive(!d.isActive());
                persist();
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

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

    private void persist() {
        storage.writeAll(FILE, new ArrayList<>(store));
    }
}
