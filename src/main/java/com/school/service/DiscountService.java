package com.school.service;

import com.school.model.Discount;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DiscountService — in-memory storage (no file system).
 * Data is seeded from hardcoded defaults on startup.
 * Changes persist for the lifetime of the process only.
 */
@Service
public class DiscountService {

    private final List<Discount> store = new ArrayList<>();

    public DiscountService() {
        initializeDefaultDiscounts();
    }

    private void initializeDefaultDiscounts() {
        long now = System.currentTimeMillis();
        store.add(makeDiscount("Loyalty Discount",    "LOYALTY",    "FIXED", 6000,
                               "ALL", "Returning student loyalty discount", now));
        store.add(makeDiscount("Early Bird Discount", "EARLY_BIRD", "FIXED", 3000,
                               "ALL", "Early admission discount offer",     now));
        System.out.println("[DiscountService] Default discounts loaded into memory.");
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
                    return discount;
                }
            }
            store.add(discount);
        }
        return discount;
    }

    public boolean delete(String id) {
        return store.removeIf(d -> id.equals(d.getId()));
    }

    public Optional<Discount> toggleActive(String id) {
        for (int i = 0; i < store.size(); i++) {
            if (store.get(i).getId().equals(id)) {
                Discount d = store.get(i);
                d.setActive(!d.isActive());
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
}
