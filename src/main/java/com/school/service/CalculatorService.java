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

        // Actual age: DOB → Joining Date (as requested)
        int actualAgeMonths = (int) Math.max(0, ChronoUnit.MONTHS.between(dob, joiningDate));

        // Display age components: DOB → Joining Date
        Period agePeriod = Period.between(dob, joiningDate);
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

        // ── Programme determination ────────────────────────────────────────────
        String govtProgram   = programService.determineProgramByAge(govtAgeMonths);
        String actualProgram = programService.determineProgramByAge(actualAgeMonths);

        result.setGovtRecommendedProgram(govtProgram);
        result.setActualAgeProgram(actualProgram);

        String selectedProgram = request.isUseGovtRecommended() ? govtProgram : actualProgram;
        result.setSelectedProgram(selectedProgram);

        // ── Duration calculation ───────────────────────────────────────────────
        // Whole months from joining month → March (mid-month joining = full month)
        LocalDate joiningMonthStart = joiningDate.withDayOfMonth(1);
        LocalDate endMonthStart     = endDate.withDayOfMonth(1);
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

        double fixedComponent   = 0.0;
        double sessionFees      = 0.0;
        double sportsFees       = 0.0;
        double celebrationFees  = 0.0;
        double totalBaseFees    = 0.0;

        if (feeOpt.isPresent()) {
            FeeStructure fee = feeOpt.get();
            fixedComponent  = fee.getFixedComponent();
            sessionFees     = fee.getSessionFees();
            sportsFees      = fee.getSportsFees();
            celebrationFees = fee.getCelebrationFees();
            // Base fees = fixed + prorated sessions (sports/celebration NOT included here)
            totalBaseFees = fixedComponent + (sessionFees * durationMonths);

            //----
            List<Discount> appliedDiscountsTemp = new ArrayList<>();
            double totalDiscountAmountTemp = 0.0;

            if (request.getDiscountIds() != null) {
                for (String discountId : request.getDiscountIds()) {
                    discountService.findById(discountId).ifPresent(appliedDiscountsTemp::add);
                }
                for (Discount d : appliedDiscountsTemp) {
                    if ("FIXED".equalsIgnoreCase(d.getDiscountType())) {
                        totalDiscountAmountTemp += d.getValue();
                    } else if ("PERCENTAGE".equalsIgnoreCase(d.getDiscountType())) {
                        totalDiscountAmountTemp += totalBaseFees * d.getValue() / 100.0;
                    }
                }
            }
            //totalBaseFees = (fixedComponent+sessionFees*10) - totalDiscountAmountTemp;
            //totalBaseFees=fixedComponent + (((totalBaseFees-fixedComponent)/10) * durationMonths);
            //totalBaseFees = fixedComponent + (sessionFees * durationMonths);


            //==
            result.setFeeConfigured(true);
        } else {
            result.setFeeConfigured(false);
        }

        result.setFixedComponent(round2(fixedComponent));
        result.setSessionFees(round2(sessionFees));
        result.setSportsFees(round2(sportsFees));
        result.setCelebrationFees(round2(celebrationFees));
        result.setTotalBaseFees(round2(totalBaseFees));

        // ── Discount application (applied only on base fees, not sports/celebration) ──
        List<Discount> appliedDiscounts = new ArrayList<>();
        double totalDiscountAmount = 0.0;

        if (request.getDiscountIds() != null) {
            for (String discountId : request.getDiscountIds()) {
                discountService.findById(discountId).ifPresent(appliedDiscounts::add);
            }
            for (Discount d : appliedDiscounts) {
                if ("FIXED".equalsIgnoreCase(d.getDiscountType())) {
                    totalDiscountAmount += d.getValue();
                } else if ("PERCENTAGE".equalsIgnoreCase(d.getDiscountType())) {
                    totalDiscountAmount += totalBaseFees * d.getValue() / 100.0;
                }
            }
        }

        double discountedFees = Math.max(0, totalBaseFees - totalDiscountAmount);
       // double discountedFees = Math.max(0, totalBaseFees);

        result.setAppliedDiscounts(appliedDiscounts);
        result.setTotalDiscountAmount(round2(totalDiscountAmount));
        result.setDiscountedFees(round2(discountedFees));

        // ── Payment schedule ───────────────────────────────────────────────────
        // variable = discounted fees minus the fixed component
        double variable = Math.max(0, discountedFees - fixedComponent);

        double inst1Var = round2(variable * 0.40);
        double inst2Var = round2(variable * 0.30);
        double inst3Var = round2(variable * 0.30);

        // Booking = fixed component (user can edit this down in the UI)
        // If user pays less than fixed, the shortfall is added to installment 1
        // Backend returns full fixed as default; UI handles partial payment logic
        result.setBookingAmount(round2(fixedComponent));

        result.setInstallment1Variable(inst1Var);
        result.setInstallment1(inst1Var);           // UI adds any booking shortfall on top

        result.setInstallment2Variable(inst2Var);
        result.setInstallment2(round2(inst2Var + sportsFees));     // + sports fees

        result.setInstallment3Variable(inst3Var);
        result.setInstallment3(round2(inst3Var + celebrationFees)); // + celebration fees

        // Total payable = discounted base fees + sports + celebration
        result.setTotalPayable(round2(discountedFees + sportsFees + celebrationFees));

        return result;
    }

    /** Round to 2 decimal places. */
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}