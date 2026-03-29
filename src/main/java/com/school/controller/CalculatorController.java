package com.school.controller;

import com.school.model.FeeCalculationRequest;
import com.school.model.FeeCalculationResult;
import com.school.service.CalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/calculate")
public class CalculatorController {

    private final CalculatorService service;

    public CalculatorController(CalculatorService service) {
        this.service = service;
    }

    /**
     * POST /api/calculate
     * Accepts a FeeCalculationRequest and returns full computed fee breakdown.
     */
    @PostMapping
    public ResponseEntity<?> calculate(@RequestBody FeeCalculationRequest request) {
        if (request.getDateOfBirth() == null || request.getDateOfBirth().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Date of birth is required"));
        }
        if (request.getJoiningDate() == null || request.getJoiningDate().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Joining date is required"));
        }
        try {
            FeeCalculationResult result = service.calculate(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Calculation failed: " + e.getMessage()));
        }
    }
}
