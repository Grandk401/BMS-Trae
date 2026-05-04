package com.bms.service;

import com.bms.mapper.BookMapper;
import com.bms.mapper.BorrowRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BookStatisticsService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalBooks", bookMapper.countTotalBooks());
        stats.put("totalBorrowed", bookMapper.countBorrowedBooks());
        stats.put("totalAvailable", bookMapper.countAvailableBooks());
        stats.put("totalBorrowRecords", borrowRecordMapper.countAllRecords());

        return stats;
    }

    public List<Map<String, Object>> getCategoryDistribution() {
        return bookMapper.getCategoryDistribution();
    }

    public List<Map<String, Object>> getBorrowTrend(int days) {
        return borrowRecordMapper.getBorrowTrend(days);
    }

    public List<Map<String, Object>> getTopBorrowedBooks(int limit) {
        return borrowRecordMapper.getTopBorrowedBooks(limit);
    }

    public List<Map<String, Object>> getMonthlyStatistics() {
        return borrowRecordMapper.getMonthlyStatistics();
    }
}
