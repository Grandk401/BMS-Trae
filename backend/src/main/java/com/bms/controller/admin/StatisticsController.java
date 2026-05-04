package com.bms.controller.admin;

import com.bms.common.Result;
import com.bms.service.BookStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Slf4j
public class StatisticsController {

    @Autowired
    private BookStatisticsService bookStatisticsService;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStatistics() {
        log.info("获取仪表盘统计数据");
        return Result.success("获取成功", bookStatisticsService.getDashboardStatistics());
    }

    @GetMapping("/category-distribution")
    public Result<List<Map<String, Object>>> getCategoryDistribution() {
        log.info("获取图书分类分布");
        return Result.success("获取成功", bookStatisticsService.getCategoryDistribution());
    }

    @GetMapping("/borrow-trend")
    public Result<List<Map<String, Object>>> getBorrowTrend(@RequestParam(defaultValue = "7") int days) {
        log.info("获取近{}天借阅趋势", days);
        return Result.success("获取成功", bookStatisticsService.getBorrowTrend(days));
    }

    @GetMapping("/top-books")
    public Result<List<Map<String, Object>>> getTopBorrowedBooks(@RequestParam(defaultValue = "10") int limit) {
        log.info("获取借阅次数最多的{}本书", limit);
        return Result.success("获取成功", bookStatisticsService.getTopBorrowedBooks(limit));
    }

    @GetMapping("/monthly")
    public Result<List<Map<String, Object>>> getMonthlyStatistics() {
        log.info("获取月度统计数据");
        return Result.success("获取成功", bookStatisticsService.getMonthlyStatistics());
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDashboardStatistics() throws IOException {
        log.info("导出核心运营指标报表");
        
        Map<String, Object> dashboard = bookStatisticsService.getDashboardStatistics();
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("运营指标");
        
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        
        Font dataFont = workbook.createFont();
        
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        
        int rowNum = 0;
        
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("核心运营指标报表");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));
        
        Row dateRow = sheet.createRow(rowNum++);
        Cell dateLabelCell = dateRow.createCell(0);
        dateLabelCell.setCellValue("导出日期");
        dateLabelCell.setCellStyle(dataStyle);
        Cell dateValueCell = dateRow.createCell(1);
        dateValueCell.setCellValue(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dateValueCell.setCellStyle(dataStyle);
        
        rowNum++;
        
        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("统计项");
        headerCell1.setCellStyle(headerStyle);
        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("数值");
        headerCell2.setCellStyle(headerStyle);
        
        String[][] data = {
            {"图书总数", String.valueOf(dashboard.get("totalBooks"))},
            {"已借出数量", String.valueOf(dashboard.get("totalBorrowed"))},
            {"在馆可借数量", String.valueOf(dashboard.get("totalAvailable"))},
            {"借阅记录总数", String.valueOf(dashboard.get("totalBorrowRecords"))}
        };
        
        for (String[] row : data) {
            Row dataRow = sheet.createRow(rowNum++);
            Cell cell1 = dataRow.createCell(0);
            cell1.setCellValue(row[0]);
            cell1.setCellStyle(dataStyle);
            Cell cell2 = dataRow.createCell(1);
            cell2.setCellValue(row[1]);
            cell2.setCellStyle(dataStyle);
        }
        
        int charWidth = 256;
        int padding = 3;
        
        String[] col0Values = {"导出日期", "统计项", "图书总数", "已借出数量", "在馆可借数量", "借阅记录总数"};
        int col0MaxLen = 0;
        for (String val : col0Values) {
            int len = 0;
            for (char c : val.toCharArray()) {
                len += (c >= '\u4e00' && c <= '\u9fff') ? 2 : 1;
            }
            col0MaxLen = Math.max(col0MaxLen, len);
        }
        sheet.setColumnWidth(0, (col0MaxLen + padding) * charWidth);
        
        String[] col1Values = {LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), "数值", 
            String.valueOf(dashboard.get("totalBooks")), String.valueOf(dashboard.get("totalBorrowed")),
            String.valueOf(dashboard.get("totalAvailable")), String.valueOf(dashboard.get("totalBorrowRecords"))};
        int col1MaxLen = 0;
        for (String val : col1Values) {
            int len = 0;
            for (char c : val.toCharArray()) {
                len += (c >= '\u4e00' && c <= '\u9fff') ? 2 : 1;
            }
            col1MaxLen = Math.max(col1MaxLen, len);
        }
        sheet.setColumnWidth(1, (col1MaxLen + padding) * charWidth);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        byte[] dataBytes = outputStream.toByteArray();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        String filename = "运营指标报表_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        headers.setContentDispositionFormData("attachment", filename);
        
        return new ResponseEntity<>(dataBytes, headers, HttpStatus.OK);
    }
}
