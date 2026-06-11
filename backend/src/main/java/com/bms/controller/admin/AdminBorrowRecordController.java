/**
 * 系统管理员 - 借阅记录控制器
 * <p>
 * 提供借阅记录的完整 CRUD 操作 REST API。
 * 仅系统管理员可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.admin;

import com.bms.common.Result;
import com.bms.common.validation.ValidationGroup;
import com.bms.entity.BorrowRecord;
import com.bms.service.BorrowRecordService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统管理员 - 借阅记录 REST API 控制器
 */
@RestController
@RequestMapping("/admin/borrow-records")
@CrossOrigin
@Slf4j
public class AdminBorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 分页查询所有借阅记录
     *
     * @param pageNum     页码
     * @param pageSize    每页条数
     * @param statusGroup 状态分组（ALL/RETURNED/NOT_RETURNED/OVERDUE）
     * @return 分页结果
     */
    @GetMapping
    public Result<PageInfo<BorrowRecord>> getAllRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String statusGroup) {
        log.info("系统管理员 - 分页查询借阅记录: pageNum={}, pageSize={}, statusGroup={}", pageNum, pageSize, statusGroup);
        PageInfo<BorrowRecord> pageInfo = borrowRecordService.getAllRecordsPage(pageNum, pageSize, statusGroup);
        return Result.success("查询成功", pageInfo);
    }

    /**
     * 根据 ID 查询借阅记录
     *
     * @param id 借阅记录 ID
     * @return 借阅记录详情
     */
    @GetMapping("/{id}")
    public Result<BorrowRecord> getRecordById(@PathVariable Integer id) {
        log.info("系统管理员 - 查询借阅记录: recordId={}", id);
        BorrowRecord record = borrowRecordService.getRecordById(id);
        return Result.success("查询成功", record);
    }

    /**
     * 搜索借阅记录（支持分页）
     *
     * @param pageNum          页码
     * @param pageSize         每页条数
     * @param bookName         图书名
     * @param username         用户名
     * @param borrowDateStart  借阅日期开始
     * @param borrowDateEnd    借阅日期结束
     * @param dueDateStart     应归还日期开始
     * @param dueDateEnd       应归还日期结束
     * @param statusGroup      状态分组（ALL/RETURNED/NOT_RETURNED/OVERDUE）
     * @return 分页结果
     */
    @GetMapping("/search")
    public Result<PageInfo<BorrowRecord>> searchRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime borrowDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime borrowDateEnd,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDateEnd,
            @RequestParam(required = false) String statusGroup) {
        log.info("系统管理员 - 分页搜索借阅记录: pageNum={}, pageSize={}, bookName={}, username={}, statusGroup={}",
                pageNum, pageSize, bookName, username, statusGroup);
        PageInfo<BorrowRecord> pageInfo = borrowRecordService.searchRecordsPage(
                pageNum, pageSize, bookName, username, borrowDateStart, borrowDateEnd, dueDateStart, dueDateEnd, statusGroup);
        return Result.success("查询成功", pageInfo);
    }

    /**
     * 查询逾期借阅记录
     *
     * @return 逾期借阅记录列表
     */
    @GetMapping("/overdue")
    public Result<List<BorrowRecord>> getOverdueRecords() {
        log.info("系统管理员 - 查询逾期借阅记录");
        List<BorrowRecord> records = borrowRecordService.getOverdueRecords();
        return Result.success("查询成功", records);
    }

    /**
     * 同意借阅申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/approve")
    public Result<BorrowRecord> approveBorrow(@PathVariable Integer id) {
        log.info("系统管理员 - 同意借阅申请: recordId={}", id);
        BorrowRecord record = borrowRecordService.approveBorrow(id);
        return Result.success("同意借阅成功", record);
    }

    /**
     * 拒绝借阅申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/reject")
    public Result<BorrowRecord> rejectBorrow(@PathVariable Integer id) {
        log.info("系统管理员 - 拒绝借阅申请: recordId={}", id);
        BorrowRecord record = borrowRecordService.rejectBorrow(id);
        return Result.success("拒绝借阅成功", record);
    }

    /**
     * 确认归还
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/return")
    public Result<BorrowRecord> confirmReturn(@PathVariable Integer id) {
        log.info("系统管理员 - 确认归还: recordId={}", id);
        BorrowRecord record = borrowRecordService.confirmReturn(id);
        return Result.success("确认归还成功", record);
    }

    /**
     * 标记逾期未归还
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/mark-overdue")
    public Result<BorrowRecord> markOverdue(@PathVariable Integer id) {
        log.info("系统管理员 - 标记逾期未归还: recordId={}", id);
        BorrowRecord record = borrowRecordService.markOverdue(id);
        return Result.success("标记逾期成功", record);
    }

    /**
     * 同意续借申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/approve-renew")
    public Result<BorrowRecord> approveRenew(@PathVariable Integer id) {
        log.info("系统管理员 - 同意续借申请: recordId={}", id);
        BorrowRecord record = borrowRecordService.approveRenew(id);
        return Result.success("同意续借成功", record);
    }

    /**
     * 拒绝续借申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/reject-renew")
    public Result<BorrowRecord> rejectRenew(@PathVariable Integer id) {
        log.info("系统管理员 - 拒绝续借申请: recordId={}", id);
        BorrowRecord record = borrowRecordService.rejectRenew(id);
        return Result.success("拒绝续借成功", record);
    }

    /**
     * 添加新借阅记录（管理员直接添加）
     *
     * @param record 借阅记录对象
     * @return 添加后的借阅记录
     */
    @PostMapping
    public Result<BorrowRecord> addRecord(@Validated(ValidationGroup.Add.class) @RequestBody BorrowRecord record) {
        log.info("系统管理员 - 添加借阅记录: bookId={}, userId={}", record.getBookId(), record.getUserId());
        BorrowRecord savedRecord = borrowRecordService.addRecord(record);
        return Result.success("借阅成功", savedRecord);
    }

    /**
     * 更新借阅记录
     *
     * @param id     借阅记录 ID
     * @param record 更新后的借阅记录对象
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}")
    public Result<BorrowRecord> updateRecord(@PathVariable Integer id, @Validated(ValidationGroup.Update.class) @RequestBody BorrowRecord record) {
        log.info("系统管理员 - 更新借阅记录: recordId={}", id);
        record.setId(id);
        BorrowRecord updatedRecord = borrowRecordService.updateRecord(record);
        return Result.success("更新成功", updatedRecord);
    }

    /**
     * 删除借阅记录
     *
     * @param id 借阅记录 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteRecord(@PathVariable Integer id) {
        log.info("系统管理员 - 删除借阅记录: recordId={}", id);
        borrowRecordService.deleteRecord(id);
        return Result.success("删除成功", null);
    }
}