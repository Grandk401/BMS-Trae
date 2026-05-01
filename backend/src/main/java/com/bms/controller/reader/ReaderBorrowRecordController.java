/**
 * 普通读者 - 借阅记录控制器
 * <p>
 * 提供个人借阅记录查询和借阅操作 REST API。
 * 仅普通读者可访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.reader;

import com.bms.common.Result;
import com.bms.dto.BorrowRecordWithBookDTO;
import com.bms.entity.BorrowRecord;
import com.bms.entity.User;
import com.bms.service.BorrowRecordService;
import com.bms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 普通读者 - 借阅记录 REST API 控制器
 */
@RestController
@RequestMapping("/reader/borrow-records")
@CrossOrigin
@Slf4j
public class ReaderBorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private UserService userService;

    /**
     * 查询当前用户的借阅记录（包含图书信息）
     *
     * @return 借阅记录列表（包含图书信息）
     */
    @GetMapping
    public Result<List<BorrowRecordWithBookDTO>> getMyRecords() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("普通读者 - 查询个人借阅记录: username={}", username);
        
        User user = userService.getUserByUsername(username);
        List<BorrowRecordWithBookDTO> records = borrowRecordService.getRecordsWithBookByUserId(user.getId());
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
        log.info("普通读者 - 查询借阅记录: recordId={}", id);
        BorrowRecord record = borrowRecordService.getRecordById(id);
        return Result.success("查询成功", record);
    }

    /**
     * 申请借阅图书
     * 
     * 用户ID从SecurityContext获取，保证安全性
     *
     * @param bookId 图书ID
     * @return 添加后的借阅记录
     */
    @PostMapping("/apply/{bookId}")
    public Result<BorrowRecord> applyBorrow(@PathVariable Integer bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        
        log.info("普通读者 - 申请借阅: username={}, userId={}, bookId={}", username, user.getId(), bookId);
        
        BorrowRecord savedRecord = borrowRecordService.applyBorrow(bookId, user.getId());
        return Result.success("借阅申请成功，请等待管理员审核", savedRecord);
    }

    /**
     * 申请续借图书
     *
     * @param id 借阅记录ID
     * @return 更新后的借阅记录
     */
    @PutMapping("/{id}/renew")
    public Result<BorrowRecord> applyRenew(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        
        log.info("普通读者 - 申请续借: username={}, userId={}, recordId={}", username, user.getId(), id);
        
        BorrowRecord record = borrowRecordService.applyRenew(id, user.getId());
        return Result.success("续借申请成功，请等待管理员审核", record);
    }
}