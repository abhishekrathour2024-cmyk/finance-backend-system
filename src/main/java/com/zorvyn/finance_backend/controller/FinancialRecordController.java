package com.zorvyn.finance_backend.controller;

import com.zorvyn.finance_backend.entity.FinancialRecord;
import com.zorvyn.finance_backend.entity.enums.RecordType;
import com.zorvyn.finance_backend.entity.enums.Role;
import com.zorvyn.finance_backend.service.FinancialRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    public FinancialRecordController(FinancialRecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<FinancialRecord> createRecord(@RequestBody FinancialRecord record,
                                                        @RequestParam("role") Role role) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.createRecord(record, role));
    }

    @GetMapping
    public ResponseEntity<List<FinancialRecord>> getRecords(@RequestParam("role") Role role) {
        return ResponseEntity.ok(recordService.getAllRecords(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecord> getRecordById(@PathVariable Long id,
                                                         @RequestParam("role") Role role) {
        return ResponseEntity.ok(recordService.getRecordById(id, role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinancialRecord> updateRecord(@PathVariable Long id,
                                                        @RequestBody FinancialRecord record,
                                                        @RequestParam("role") Role role) {
        return ResponseEntity.ok(recordService.updateRecord(id, record, role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id,
                                             @RequestParam("role") Role role) {
        recordService.deleteRecord(id, role);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<FinancialRecord>> filterRecords(
            @RequestParam("role") Role role,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(recordService.filterRecords(category, type, startDate, endDate, role));
    }
}
