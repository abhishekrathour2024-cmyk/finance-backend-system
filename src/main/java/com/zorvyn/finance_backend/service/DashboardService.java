package com.zorvyn.finance_backend.service;

import com.zorvyn.finance_backend.entity.FinancialRecord;
import com.zorvyn.finance_backend.entity.enums.RecordType;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.exception.BadRequestException;
import com.zorvyn.finance_backend.repository.FinancialRecordRepository;
import com.zorvyn.finance_backend.util.RoleValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class DashboardService {

    private final FinancialRecordRepository financialRecordRepository;

    public DashboardService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    public double getTotalIncome(Role role) {
        RoleValidator.dashboardAccess(role);

        List<FinancialRecord> records = financialRecordRepository.findAll();
        double totalIncome = 0.0;

        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);

            if (record.getType() == RecordType.INCOME) {
                totalIncome = totalIncome + record.getAmount();
            }
        }

        return totalIncome;
    }

    public double getTotalExpense(Role role) {
        RoleValidator.dashboardAccess(role);

        List<FinancialRecord> records = financialRecordRepository.findAll();
        double totalExpense = 0.0;

        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);

            if (record.getType() == RecordType.EXPENSE) {
                totalExpense = totalExpense + record.getAmount();
            }
        }

        return totalExpense;
    }

    public double getNetBalance(Role role) {
        RoleValidator.dashboardAccess(role);
        return getTotalIncome(role) - getTotalExpense(role);
    }

    public Map<String, Double> getSummary(Role role) {
        RoleValidator.dashboardAccess(role);

        Map<String, Double> response = new HashMap<String, Double>();
        response.put("totalIncome", getTotalIncome(role));
        response.put("totalExpense", getTotalExpense(role));
        response.put("netBalance", getNetBalance(role));

        return response;
    }

    public Map<String, Double> getCategorySummary(Role role) {
        RoleValidator.dashboardAccess(role);

        List<FinancialRecord> records = financialRecordRepository.findAll();
        Map<String, Double> result = new LinkedHashMap<String, Double>();

        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);
            String category = record.getCategory();

            if (result.containsKey(category)) {
                result.put(category, result.get(category) + record.getAmount());
            } else {
                result.put(category, record.getAmount());
            }
        }

        return result;
    }

    public List<FinancialRecord> getRecentActivity(Role role) {
        RoleValidator.dashboardAccess(role);
        return financialRecordRepository.findTop5ByOrderByDateDescIdDesc();
    }

    public Map<String, Double> getMonthlyTrends(LocalDate startDate, LocalDate endDate, Role role) {
        RoleValidator.dashboardAccess(role);

        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        List<FinancialRecord> records = financialRecordRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);
        Map<String, Double> trends = new LinkedHashMap<String, Double>();

        YearMonth current = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        while (!current.isAfter(endMonth)) {
            trends.put(current.toString(), 0.0);
            current = current.plusMonths(1);
        }

        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);
            String monthKey = YearMonth.from(record.getDate()).toString();

            if (trends.containsKey(monthKey)) {
                if (record.getType() == RecordType.INCOME) {
                    trends.put(monthKey, trends.get(monthKey) + record.getAmount());
                } else if (record.getType() == RecordType.EXPENSE) {
                    trends.put(monthKey, trends.get(monthKey) - record.getAmount());
                }
            }
        }

        return trends;
    }
}