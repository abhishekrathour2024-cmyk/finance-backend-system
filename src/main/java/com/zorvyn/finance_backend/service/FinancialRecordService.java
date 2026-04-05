package com.zorvyn.finance_backend.service;

import com.zorvyn.finance_backend.entity.FinancialRecord;
import com.zorvyn.finance_backend.entity.User;
import com.zorvyn.finance_backend.entity.enums.RecordType;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.entity.enums.UserStatus;
import com.zorvyn.finance_backend.exception.BadRequestException;
import com.zorvyn.finance_backend.exception.ResourceNotFoundException;
import com.zorvyn.finance_backend.repository.FinancialRecordRepository;
import com.zorvyn.finance_backend.repository.UserRepository;
import com.zorvyn.finance_backend.util.RoleValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public FinancialRecordService(FinancialRecordRepository recordRepository,
                                  UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
    }

    public FinancialRecord createRecord(FinancialRecord record, Role role) {
        RoleValidator.adminOnly(role);

        validateRecord(record);

        if (record.getUser() == null || record.getUser().getId() == null) {
            throw new BadRequestException("User ID is required");
        }

        Optional<User> optionalUser = userRepository.findById(record.getUser().getId());
        if (!optionalUser.isPresent()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BadRequestException("Cannot assign record to inactive user");
        }

        record.setUser(user);
        return recordRepository.save(record);
    }

    public List<FinancialRecord> getAllRecords(Role role) {
        RoleValidator.analystOrAdmin(role);
        return recordRepository.findAll();
    }

    public FinancialRecord getRecordById(Long id, Role role) {
        RoleValidator.analystOrAdmin(role);

        Optional<FinancialRecord> optionalRecord = recordRepository.findById(id);
        if (!optionalRecord.isPresent()) {
            throw new ResourceNotFoundException("Record not found");
        }

        return optionalRecord.get();
    }

    public FinancialRecord updateRecord(Long id, FinancialRecord updatedRecord, Role role) {
        RoleValidator.adminOnly(role);

        Optional<FinancialRecord> optionalRecord = recordRepository.findById(id);
        if (!optionalRecord.isPresent()) {
            throw new ResourceNotFoundException("Record not found");
        }

        validateRecord(updatedRecord);

        FinancialRecord existingRecord = optionalRecord.get();
        existingRecord.setAmount(updatedRecord.getAmount());
        existingRecord.setType(updatedRecord.getType());
        existingRecord.setCategory(updatedRecord.getCategory());
        existingRecord.setDate(updatedRecord.getDate());
        existingRecord.setNotes(updatedRecord.getNotes());

        if (updatedRecord.getUser() != null && updatedRecord.getUser().getId() != null) {
            Optional<User> optionalUser = userRepository.findById(updatedRecord.getUser().getId());
            if (!optionalUser.isPresent()) {
                throw new ResourceNotFoundException("User not found");
            }

            User user = optionalUser.get();
            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new BadRequestException("Cannot assign record to inactive user");
            }

            existingRecord.setUser(user);
        }

        return recordRepository.save(existingRecord);
    }

    public void deleteRecord(Long id, Role role) {
        RoleValidator.adminOnly(role);

        if (!recordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found");
        }

        recordRepository.deleteById(id);
    }

    public List<FinancialRecord> filterRecords(String category,
                                               RecordType type,
                                               LocalDate startDate,
                                               LocalDate endDate,
                                               Role role) {
        RoleValidator.analystOrAdmin(role);

        boolean hasCategory = category != null && !category.trim().isEmpty();
        boolean hasType = type != null;
        boolean hasDates = startDate != null && endDate != null;

        if (hasDates && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        if (hasCategory && hasType && hasDates) {
            return recordRepository.findByCategoryIgnoreCaseAndTypeAndDateBetween(category, type, startDate, endDate);
        }
        if (hasCategory && hasType) {
            return recordRepository.findByCategoryIgnoreCaseAndType(category, type);
        }
        if (hasCategory && hasDates) {
            return recordRepository.findByCategoryIgnoreCaseAndDateBetween(category, startDate, endDate);
        }
        if (hasType && hasDates) {
            return recordRepository.findByTypeAndDateBetween(type, startDate, endDate);
        }
        if (hasCategory) {
            return recordRepository.findByCategoryIgnoreCase(category);
        }
        if (hasType) {
            return recordRepository.findByType(type);
        }
        if (hasDates) {
            return recordRepository.findByDateBetween(startDate, endDate);
        }

        return recordRepository.findAll();
    }

    public Map<String, Double> getSummary(Role role) {
        RoleValidator.dashboardAccess(role);

        List<FinancialRecord> records = recordRepository.findAll();
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (int i = 0; i < records.size(); i++) {
            FinancialRecord record = records.get(i);

            if (record.getType() == RecordType.INCOME) {
                totalIncome = totalIncome + record.getAmount();
            } else if (record.getType() == RecordType.EXPENSE) {
                totalExpense = totalExpense + record.getAmount();
            }
        }

        double netBalance = totalIncome - totalExpense;

        Map<String, Double> response = new HashMap<String, Double>();
        response.put("totalIncome", totalIncome);
        response.put("totalExpense", totalExpense);
        response.put("netBalance", netBalance);

        return response;
    }

    public Map<String, Double> getCategorySummary(Role role) {
        RoleValidator.dashboardAccess(role);

        List<FinancialRecord> records = recordRepository.findAll();
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
        return recordRepository.findTop5ByOrderByDateDescIdDesc();
    }

    private void validateRecord(FinancialRecord record) {
        if (record.getAmount() <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }
        if (record.getType() == null) {
            throw new BadRequestException("Record type is required");
        }
        if (record.getCategory() == null || record.getCategory().trim().isEmpty()) {
            throw new BadRequestException("Category is required");
        }
        if (record.getDate() == null) {
            throw new BadRequestException("Date is required");
        }
    }
}