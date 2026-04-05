package com.zorvyn.finance_backend.controller;

import com.zorvyn.finance_backend.entity.FinancialRecord;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getSummary(role));
    }

    @GetMapping("/total-income")
    public ResponseEntity<Double> getTotalIncome(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getTotalIncome(role));
    }

    @GetMapping("/total-expense")
    public ResponseEntity<Double> getTotalExpense(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getTotalExpense(role));
    }

    @GetMapping("/net-balance")
    public ResponseEntity<Double> getNetBalance(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getNetBalance(role));
    }

    @GetMapping("/category-summary")
    public ResponseEntity<Map<String, Double>> getCategorySummary(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getCategorySummary(role));
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<FinancialRecord>> getRecentActivity(@RequestParam("role") Role role) {
        return ResponseEntity.ok(dashboardService.getRecentActivity(role));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<Map<String, Double>> getMonthlyTrends(
            @RequestParam("role") Role role,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends(startDate, endDate, role));
    }
}
