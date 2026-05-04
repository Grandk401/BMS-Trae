/**
 * 图书管理员 - 借阅记录控制器
 * <p>
 * 提供借阅记录的 CRUD 操作 REST API。
 * 仅图书管理员可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.librarian;

import com.bms.annotation.OperationLogging;
import com.bms.common.Result;
import com.bms.common.validation.ValidationGroup;
import com.bms.entity.BorrowRecord;
import com.bms.service.BorrowRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理员 - 借阅记录 REST API 控制器
 */
@RestController
@RequestMapping("/librarian/borrow-records")
@CrossOrigin
@Slf4j
public class LibrarianBorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 查询所有借阅记录
     *
     * @return 借阅记录列表
     */
    @GetMapping
    public Result<List<BorrowRecord>> getAllRecords() {
        log.info("图书管理员 - 查询所有借阅记录");
        List<BorrowRecord> records = borrowRecordService.getAllRecords();
        return Result.success("查询成功", records);
    }

    /**
     * 根据 ID 查询借阅记录
     *
     * @param id 借阅记录 ID
     * @return 借阅记录详情
     */
    @GetMapping("/{id}")
    public Result<BorrowRecord> getRecordById(@PathVariable Integer id) {
        log.info("图书管理员 - 查询借阅记录: recordId={}", id);
        BorrowRecord record = borrowRecordService.getRecordById(id);
        return Result.success("查询成功", record);
    }

    /**
     * 添加新借阅记录
     *
     * @param record 借阅记录对象
     * @return 添加后的借阅记录
     */
    @PostMapping
    @OperationLogging(module = "BORROW_RECORD", type = "ADD", description = "添加借阅记录")
    public Result<BorrowRecord> addRecord(@Validated(ValidationGroup.Add.class) @RequestBody BorrowRecord record) {
        log.info("图书管理员 - 添加借阅记录: bookId={}, userId={}", record.getBookId(), record.getUserId());
        BorrowRecord savedRecord = borrowRecordService.addRecord(record);
        return Result.success("借阅成功", savedRecord);
    }

    /**
     * 更新借阅记录（归还图书）
     *
     * @param id     借阅记录 ID
     * @param record 更新后的借阅记录对象
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}")
    @OperationLogging(module = "BORROW_RECORD", type = "UPDATE", description = "更新借阅记录")
    public Result<BorrowRecord> updateRecord(@PathVariable Integer id, @Validated(ValidationGroup.Update.class) @RequestBody BorrowRecord record) {
        log.info("图书管理员 - 更新借阅记录: recordId={}", id);
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
    @OperationLogging(module = "BORROW_RECORD", type = "DELETE", description = "删除借阅记录")
    public Result<String> deleteRecord(@PathVariable Integer id) {
        log.info("图书管理员 - 删除借阅记录: recordId={}", id);
        borrowRecordService.deleteRecord(id);
        return Result.success("删除成功", null);
    }

    /**
     * 同意续借申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/approve-renew")
    public Result<BorrowRecord> approveRenew(@PathVariable Integer id) {
        log.info("图书管理员 - 同意续借申请: recordId={}", id);
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
        log.info("图书管理员 - 拒绝续借申请: recordId={}", id);
        BorrowRecord record = borrowRecordService.rejectRenew(id);
        return Result.success("拒绝续借成功", record);
    }
}
