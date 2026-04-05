package com.zorvyn.finance_backend.repository;

import com.zorvyn.finance_backend.entity.FinancialRecord;
import com.zorvyn.finance_backend.entity.enums.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByCategoryIgnoreCase(String category);

    List<FinancialRecord> findByType(RecordType type);

    List<FinancialRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findByCategoryIgnoreCaseAndType(String category, RecordType type);

    List<FinancialRecord> findByCategoryIgnoreCaseAndDateBetween(String category, LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findByTypeAndDateBetween(RecordType type, LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findByCategoryIgnoreCaseAndTypeAndDateBetween(
            String category,
            RecordType type,
            LocalDate startDate,
            LocalDate endDate
    );

    List<FinancialRecord> findTop5ByOrderByDateDescIdDesc();

    List<FinancialRecord> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    boolean existsByUserIdAndDeletedFalse(Long userId);
}