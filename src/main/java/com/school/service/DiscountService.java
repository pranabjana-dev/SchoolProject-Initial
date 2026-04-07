package com.school.service;

import com.school.model.Discount;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DiscountService — in-memory storage with FIXED stable IDs for defaults.
 * Fixed IDs ensure discount lookups survive Render free-tier cold restarts.
 */
@Service
public class DiscountService {

    // Stable IDs — never change these; browser chips depend on them surviving restarts
    public static final String ID_LOYALTY    = "disc-default-loyalty-001";
    public static final String ID_EARLY_BIRD = "disc-default-earlybird-001";

    private final List<Discount> store = new ArrayList<>();

    public DiscountService() {
        initializeDefaultDiscounts();
    }

    private void initializeDefaultDiscounts() {
        store.add(makeDiscount(ID_LOYALTY,
                "Loyalty Discount",    "LOYALTY",    "FIXED", 6000,
                "ALL", "Returning student loyalty discount"));
        store.add(makeDiscount(ID_EARLY_BIRD,
                "Early Bird Discount", "EARLY_BIRD", "FIXED", 3000,
                "ALL", "Early admission discount offer"));
        System.out.println("[DiscountService] Default discounts loaded (stable IDs).");
    }

    private Discount makeDiscount(String id, String name, String category, String type,
                                   double value, String year, String desc) {
        Discount d = new Discount();
        d.setId(id);
        d.setName(name);
        d.setCategory(category);
        d.setDiscountType(type);
        d.setValue(value);
        d.setAcademicYear(year);
        d.setDescription(desc);
        d.setActive(true);
        d.setCreatedAt(System.currentTimeMillis());
        return d;
    }

    public List<Discount> getAll() {
        return Collections.unmodifiableList(store);
    }

    public List<Discount> getActive() {
        return store.stream().filter(Discount::isActive).collect(Collectors.toList());
    }

    public Optional<Discount> findById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return store.stream()
                .filter(d -> id.equals(d.getId()))
                .findFirst();
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
