package com.bms.controller.admin;

import com.bms.mapper.BookMapper;
import com.bms.mapper.BorrowRecordMapper;
import com.bms.mapper.UserMapper;
import com.bms.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计信息 Controller
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
@Slf4j
public class StatisticsController {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        log.info("获取仪表盘统计数据");
        Map<String, Object> data = new HashMap<>();

        // 图书总数
        data.put("totalBooks", bookMapper.countTotalBooks());

        // 已借出数量（借阅中）
        data.put("totalBorrowed", borrowRecordMapper.countByStatus("BORROWING"));

        // 在馆可借 = 图书总数 - 已借出
        int totalBooks = bookMapper.countTotalBooks();
        int borrowedCount = borrowRecordMapper.countByStatus("BORROWING");
        data.put("totalAvailable", totalBooks - borrowedCount);

        // 借阅记录总数
        data.put("totalBorrowRecords", borrowRecordMapper.countAllRecords());

        return Result.success("查询成功", data);
    }

    /**
     * 获取分类分布
     */
    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> getCategoryDistribution() {
        log.info("获取图书分类分布");
        List<Map<String, Object>> data = bookMapper.getCategoryDistribution();
        return Result.success("查询成功", data);
    }

    /**
     * 获取借阅趋势（最近30天）
     */
    @GetMapping("/borrow-trend")
    public Result<List<Map<String, Object>>> getBorrowTrend(
            @RequestParam(defaultValue = "30") int days) {
        log.info("获取借阅趋势，天数：{}", days);
        List<Map<String, Object>> data = borrowRecordMapper.getBorrowTrend(days);
        return Result.success("查询成功", data);
    }

    /**
     * 获取热门图书
     */
    @GetMapping("/top-books")
    public Result<List<Map<String, Object>>> getTopBooks(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("获取热门图书，限制：{}", limit);
        List<Map<String, Object>> data = borrowRecordMapper.getTopBorrowedBooks(limit);
        return Result.success("查询成功", data);
    }

    /**
     * 获取月度统计
     */
    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStatistics() {
        log.info("获取月度统计");
        List<Map<String, Object>> data = borrowRecordMapper.getMonthlyStatistics();
        return Result.success("查询成功", data);
    }
}
