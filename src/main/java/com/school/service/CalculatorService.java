package com.school.service;

import com.school.model.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CalculatorService — all fee calculation business logic lives here.
 *
 * Key rules:
 * 1. Govt age = DOB → June 1 of the academic year start
 * 2. Actual age = DOB → today's date
 * 3. Proration = joining month → March (full months, mid-month counts as full)
 * 4. Academic year = June→March (e.g., June 2024 – March 2025 = "2024-25")
 */
@Service
public class CalculatorService {

    private final ProgramService programService;
    private final FeeService feeService;
    private final DiscountService discountService;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    public CalculatorService(ProgramService programService,
                             FeeService feeService,
                             DiscountService discountService) {
        this.programService = programService;
        this.feeService = feeService;
        this.discountService = discountService;
    }

    public FeeCalculationResult calculate(FeeCalculationRequest request) {
        FeeCalculationResult result = new FeeCalculationResult();

        // ── Parse input dates ──────────────────────────────────────────────────
        LocalDate dob = LocalDate.parse(request.getDateOfBirth());
        LocalDate joiningDate = LocalDate.parse(request.getJoiningDate());
        LocalDate today = LocalDate.now();

        result.setStudentName(request.getStudentName());
        result.setDateOfBirth(request.getDateOfBirth());
        result.setJoiningDate(request.getJoiningDate());

        // ── Academic year determination ────────────────────────────────────────
        // Academic year starts in June. E.g.:
        //   Joining Oct 2024 → AY = 2024-25, ends March 31, 2025
        //   Joining Feb 2025 → AY = 2024-25, ends March 31, 2025
        //   Joining June 2025 → AY = 2025-26, ends March 31, 2026
        int ayStart = (joiningDate.getMonthValue() >= 6)
                ? joiningDate.getYear()
                : joiningDate.getYear() - 1;

        String academicYear = ayStart + "-" + String.valueOf(ayStart + 1).substring(2);
        LocalDate endDate = LocalDate.of(ayStart + 1, 3, 31);
        LocalDate june1 = LocalDate.of(ayStart, 6, 1);

        result.setAcademicYear(academicYear);
        result.setEndDate(endDate.format(DISPLAY_FMT));

        // ── Age calculations ───────────────────────────────────────────────────
        // Govt age: DOB → June 1 of the academic year start
        int govtAgeMonths = (int) Math.max(0, ChronoUnit.MONTHS.between(dob, june1));

        // Actual age: DOB → today
        int actualAgeMonths = (int) Math.max(0, ChronoUnit.MONTHS.between(dob, today));

        // Display age components (always from DOB → today)
        Period agePeriod = Period.between(dob, today);
        result.setAgeYears(agePeriod.getYears());
        result.setAgeMonths(agePeriod.getMonths());
        result.setAgeDays(agePeriod.getDays());
        result.setTotalAgeMonths(actualAgeMonths);
        result.setAgeDisplay(
                agePeriod.getYears() + " yr" + (agePeriod.getYears() != 1 ? "s" : "") +
                ", " + agePeriod.getMonths() + " mo" + (agePeriod.getMonths() != 1 ? "s" : "") +
                ", " + agePeriod.getDays() + " day" + (agePeriod.getDays() != 1 ? "s" : "")
        );

        result.setGovtAgeMonths(govtAgeMonths);
        result.setGovtJune1Date(june1.format(DISPLAY_FMT));
        result.setActualAgeMonths(actualAgeMonths);

        // ── Program determination ──────────────────────────────────────────────
        String govtProgram = programService.determineProgramByAge(govtAgeMonths);
        String actualProgram = programService.determineProgramByAge(actualAgeMonths);

        result.setGovtRecommendedProgram(govtProgram);
        result.setActualAgeProgram(actualProgram);

        String selectedProgram = request.isUseGovtRecommended() ? govtProgram : actualProgram;
        result.setSelectedProgram(selectedProgram);

        // ── Duration calculation ───────────────────────────────────────────────
        // Count whole months from joining month → March (inclusive both ends)
        // Mid-month joining: the whole joining month counts
        LocalDate joiningMonthStart = joiningDate.withDayOfMonth(1);
        LocalDate endMonthStart = endDate.withDayOfMonth(1);
        int durationMonths = (int) ChronoUnit.MONTHS.between(joiningMonthStart, endMonthStart) + 1;

        result.setDurationMonths(durationMonths);
        result.setDurationDescription(
                joiningDate.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH)
                + " " + joiningDate.getYear()
                + " → March " + (ayStart + 1)
                + " (" + durationMonths + " months)"
        );

        // ── Fee structure lookup ───────────────────────────────────────────────
        Optional<FeeStructure> feeOpt = feeService.getForProgram(selectedProgram, academicYear);

        double fixedComponent = 0.0;
        double sessionFees = 0.0;
        double totalBaseFees = 0.0;

        if (feeOpt.isPresent()) {
            FeeStructure fee = feeOpt.get();
            fixedComponent = fee.getFixedComponent();
            sessionFees = fee.getSessionFees();
            totalBaseFees = fixedComponent + (sessionFees * durationMonths);
            result.setFeeConfigured(true);
        } else {
            result.setFeeConfigured(false);
        }

        result.setFixedComponent(round2(fixedComponent));
        result.setSessionFees(round2(sessionFees));
        result.setTotalBaseFees(round2(totalBaseFees));

        // ── Discount application ───────────────────────────────────────────────
        List<Discount> appliedDiscounts = new ArrayList<>();
        double totalDiscountAmount = 0.0;

        if (request.getDiscountIds() != null) {
            for (String discountId : request.getDiscountIds()) {
                discountService.findById(discountId).ifPresent(d -> {
                    appliedDiscounts.add(d);
                });
            }
            final double baseFees = totalBaseFees;
            for (Discount d : appliedDiscounts) {
                if ("FIXED".equalsIgnoreCase(d.getDiscountType())) {
                    totalDiscountAmount += d.getValue();
                } else if ("PERCENTAGE".equalsIgnoreCase(d.getDiscountType())) {
                    totalDiscountAmount += baseFees * d.getValue() / 100.0;
                }
            }
        }

        double discountedFees = Math.max(0, totalBaseFees - totalDiscountAmount);

        result.setAppliedDiscounts(appliedDiscounts);
        result.setTotalDiscountAmount(round2(totalDiscountAmount));
        result.setDiscountedFees(round2(discountedFees));

        // ── Payment schedule ───────────────────────────────────────────────────
        // Booking = fixed component (from discounted total, fixed stays fixed)
        // Remaining = discountedFees - fixedComponent (the variable part after discount)
        // Inst1 = 40%, Inst2 = 30%, Inst3 = 30% of remaining
        double remaining = Math.max(0, discountedFees - fixedComponent);

        result.setBookingAmount(round2(fixedComponent));
        result.setInstallment1(round2(remaining * 0.40));
        result.setInstallment2(round2(remaining * 0.30));
        result.setInstallment3(round2(remaining * 0.30));
        result.setTotalPayable(round2(discountedFees));

        return result;
    }

    /** Round to 2 decimal places. */
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
