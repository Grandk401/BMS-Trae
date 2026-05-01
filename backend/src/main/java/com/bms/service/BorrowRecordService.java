/**
 * 借阅记录服务层
 * <p>
 * 提供借阅记录相关的业务逻辑处理，包括借阅记录的增删改查功能。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.service;

import com.bms.dto.BorrowRecordWithBookDTO;
import com.bms.entity.Book;
import com.bms.entity.BorrowRecord;
import com.bms.entity.BorrowStatus;
import com.bms.exception.BusinessException;
import com.bms.mapper.BookMapper;
import com.bms.mapper.BorrowRecordMapper;
import com.bms.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 借阅记录业务服务类
 */
@Service
@Slf4j
public class BorrowRecordService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private BookMapper bookMapper;

    /**
     * 最大借阅数量限制
     */
    private static final int MAX_BORROW_LIMIT = 3;

    /**
     * 借阅期限（天）
     */
    private static final int BORROW_DAYS = 45;

    /**
     * 获取所有借阅记录（关联用户和图书信息）
     *
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getAllRecords() {
        log.info("查询所有借阅记录");
        List<BorrowRecord> records = borrowRecordMapper.findAllWithInfo();
        log.info("查询到借阅记录数量: {}", records.size());
        return records;
    }

    /**
     * 根据 ID 获取借阅记录（关联用户和图书信息）
     *
     * @param id 借阅记录 ID
     * @return 借阅记录详情
     */
    public BorrowRecord getRecordById(Integer id) {
        log.info("查询借阅记录详情: recordId={}", id);
        BorrowRecord record = borrowRecordMapper.findByIdWithInfo(id);
        if (record == null) {
            log.warn("查询借阅记录失败: 记录不存在, recordId={}", id);
            throw BusinessException.recordNotFound();
        }
        log.info("查询借阅记录成功: recordId={}, bookId={}, userId={}", id, record.getBookId(), record.getUserId());
        return record;
    }

    /**
     * 读者申请借阅
     * 
     * 业务校验：
     * 1. 库存必须大于0
     * 2. 最大借阅数为3
     * 
     * @param bookId 图书ID
     * @param userId 用户ID
     * @return 借阅记录
     */
    @Transactional
    public BorrowRecord applyBorrow(Integer bookId, Integer userId) {
        log.info("读者申请借阅: bookId={}, userId={}", bookId, userId);

        // 1. 查询图书信息
        Book book = bookMapper.findById(bookId);
        if (book == null) {
            log.warn("借阅失败: 图书不存在, bookId={}", bookId);
            throw BusinessException.bookNotFound();
        }

        // 2. 检查库存
        if (book.getStock() <= 0) {
            log.warn("借阅失败: 库存不足, bookId={}, stock={}", bookId, book.getStock());
            throw BusinessException.stockInsufficient();
        }

        // 3. 检查最大借阅数量
        int activeCount = borrowRecordMapper.countActiveBorrowsByUserId(userId);
        if (activeCount >= MAX_BORROW_LIMIT) {
            log.warn("借阅失败: 已达到最大借阅数量限制, userId={}, activeCount={}, maxLimit={}", 
                    userId, activeCount, MAX_BORROW_LIMIT);
            throw BusinessException.maxBorrowLimitExceeded();
        }

        // 4. 扣减库存
        book.setStock(book.getStock() - 1);
        bookMapper.update(book);
        log.info("扣减库存成功: bookId={}, stock={}", bookId, book.getStock());

        // 5. 创建借阅记录（待审核状态）
        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(BORROW_DAYS));
        record.setStatus(BorrowStatus.PENDING.name());

        int rows = borrowRecordMapper.insert(record);
        if (rows <= 0) {
            log.warn("创建借阅记录失败: bookId={}, userId={}", bookId, userId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("借阅申请成功: recordId={}, bookId={}, userId={}", record.getId(), bookId, userId);
        return record;
    }

    /**
     * 管理员同意借阅申请
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord approveBorrow(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员同意借阅申请: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("同意借阅失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("同意借阅失败: 记录状态不是待审核, recordId={}, status={}", recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        record.setStatus(BorrowStatus.BORROWING.name());
        record.setOperatorId(operatorId);
        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("同意借阅失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("同意借阅成功: recordId={}, status=BORROWING, operatorId={}", recordId, operatorId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 管理员拒绝借阅申请
     *
     * 拒绝后需要恢复库存
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord rejectBorrow(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员拒绝借阅申请: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("拒绝借阅失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("拒绝借阅失败: 记录状态不是待审核, recordId={}, status={}", recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        Book book = bookMapper.findById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.update(book);
            log.info("恢复库存成功: bookId={}, stock={}", record.getBookId(), book.getStock());
        }

        record.setStatus(BorrowStatus.REJECTED.name());
        record.setOperatorId(operatorId);
        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("拒绝借阅失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("拒绝借阅成功: recordId={}, status=REJECTED", recordId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 管理员确认归还
     *
     * 业务逻辑：
     * 1. 如果已过应归还日期，状态设为"已逾期但已归还"
     * 2. 否则状态设为"已归还"
     * 3. 恢复库存
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord confirmReturn(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员确认归还: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("确认归还失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        String currentStatus = record.getStatus();
        if (!BorrowStatus.BORROWING.name().equals(currentStatus) &&
            !BorrowStatus.OVERDUE.name().equals(currentStatus)) {
            log.warn("确认归还失败: 记录状态不是借阅中或已逾期, recordId={}, status={}", recordId, currentStatus);
            throw BusinessException.invalidStatus();
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isOverdue = record.getDueDate() != null && record.getDueDate().isBefore(now);

        Book book = bookMapper.findById(record.getBookId());
        if (book != null) {
            book.setStock(book.getStock() + 1);
            bookMapper.update(book);
            log.info("归还恢复库存成功: bookId={}, stock={}", record.getBookId(), book.getStock());
        }

        // 更新状态
        String newStatus = isOverdue ? BorrowStatus.OVERDUE_RETURNED.name() : BorrowStatus.RETURNED.name();
        record.setStatus(newStatus);
        record.setReturnDate(now);
        record.setOperatorId(operatorId);
        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("确认归还失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("确认归还成功: recordId={}, status={}, isOverdue={}, operatorId={}", recordId, newStatus, isOverdue, operatorId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 管理员标记逾期未归还
     * 
     * 将借阅中的记录标记为逾期
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord markOverdue(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员标记逾期未归还: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("标记逾期失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!BorrowStatus.BORROWING.name().equals(record.getStatus())) {
            log.warn("标记逾期失败: 记录状态不是借阅中, recordId={}, status={}", recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        if (!record.getDueDate().isBefore(LocalDateTime.now())) {
            log.warn("标记逾期失败: 尚未逾期, recordId={}, dueDate={}", recordId, record.getDueDate());
            throw BusinessException.invalidOperation("尚未逾期，无法标记为逾期");
        }

        record.setStatus(BorrowStatus.OVERDUE.name());
        record.setOperatorId(operatorId);
        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("标记逾期失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("标记逾期成功: recordId={}, status=OVERDUE, operatorId={}", recordId, operatorId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 根据用户ID获取借阅记录（关联用户和图书信息）
     *
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getRecordsByUserId(Integer userId) {
        log.info("查询用户借阅记录: userId={}", userId);
        List<BorrowRecord> records = borrowRecordMapper.findByUserIdWithInfo(userId);
        log.info("查询到用户借阅记录数量: {}", records.size());
        return records;
    }

    /**
     * 更新借阅记录
     *
     * @param record 更新后的借阅记录对象
     * @return 更新后的借阅记录
     */
    public BorrowRecord updateRecord(BorrowRecord record) {
        log.info("更新借阅记录: recordId={}, status={}", record.getId(), record.getStatus());

        BorrowRecord existingRecord = borrowRecordMapper.findById(record.getId());
        if (existingRecord == null) {
            log.warn("更新借阅记录失败: 记录不存在, recordId={}", record.getId());
            throw BusinessException.recordNotFound();
        }

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("更新借阅记录失败: recordId={}", record.getId());
            throw BusinessException.dbOperationFailure();
        }
        log.info("更新借阅记录成功: recordId={}", record.getId());
        return borrowRecordMapper.findByIdWithInfo(record.getId());
    }

    /**
     * 删除借阅记录
     *
     * @param id 借阅记录 ID
     */
    public void deleteRecord(Integer id) {
        log.info("删除借阅记录: recordId={}", id);

        BorrowRecord existingRecord = borrowRecordMapper.findById(id);
        if (existingRecord == null) {
            log.warn("删除借阅记录失败: 记录不存在, recordId={}", id);
            throw BusinessException.recordNotFound();
        }

        int rows = borrowRecordMapper.deleteById(id);
        if (rows <= 0) {
            log.warn("删除借阅记录失败: recordId={}", id);
            throw BusinessException.dbOperationFailure();
        }
        log.info("删除借阅记录成功: recordId={}", id);
    }

    /**
     * 添加借阅记录（管理员直接添加）
     *
     * @param record 借阅记录对象
     * @return 添加后的借阅记录
     */
    @Transactional
    public BorrowRecord addRecord(BorrowRecord record) {
        log.info("管理员添加借阅记录: bookId={}, userId={}", record.getBookId(), record.getUserId());

        // 检查图书存在性
        Book book = bookMapper.findById(record.getBookId());
        if (book == null) {
            log.warn("添加借阅记录失败: 图书不存在, bookId={}", record.getBookId());
            throw BusinessException.bookNotFound();
        }

        // 检查库存
        if (book.getStock() <= 0) {
            log.warn("添加借阅记录失败: 库存不足, bookId={}", record.getBookId());
            throw BusinessException.stockInsufficient();
        }

        // 扣减库存
        book.setStock(book.getStock() - 1);
        bookMapper.update(book);

        // 设置借阅日期和应还日期
        if (record.getBorrowDate() == null) {
            record.setBorrowDate(LocalDateTime.now());
        }
        if (record.getDueDate() == null) {
            record.setDueDate(record.getBorrowDate().plusDays(BORROW_DAYS));
        }
        if (record.getStatus() == null) {
            record.setStatus(BorrowStatus.BORROWING.name());
        }

        // 插入借阅记录
        int rows = borrowRecordMapper.insert(record);
        if (rows <= 0) {
            log.warn("添加借阅记录失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("添加借阅记录成功: recordId={}", record.getId());
        return borrowRecordMapper.findByIdWithInfo(record.getId());
    }

    /**
     * 搜索借阅记录
     *
     * @param bookName  图书名（模糊匹配）
     * @param username  用户名（模糊匹配）
     * @param borrowDateStart 借阅日期开始
     * @param borrowDateEnd   借阅日期结束
     * @param dueDateStart    应归还日期开始
     * @param dueDateEnd      应归还日期结束
     * @return 借阅记录列表
     */
    public List<BorrowRecord> searchRecords(String bookName, String username,
                                            LocalDateTime borrowDateStart, LocalDateTime borrowDateEnd,
                                            LocalDateTime dueDateStart, LocalDateTime dueDateEnd) {
        log.info("搜索借阅记录: bookName={}, username={}", bookName, username);
        
        // 获取所有记录
        List<BorrowRecord> allRecords = borrowRecordMapper.findAllRecordsWithInfo();
        
        // 在内存中进行过滤
        List<BorrowRecord> filteredRecords = allRecords.stream()
                .filter(record -> {
                    // 图书名过滤
                    if (bookName != null && !bookName.isEmpty()) {
                        String bookNameField = record.getBookName();
                        if (bookNameField == null || !bookNameField.contains(bookName)) {
                            return false;
                        }
                    }
                    // 用户名过滤
                    if (username != null && !username.isEmpty()) {
                        String usernameField = record.getUsername();
                        if (usernameField == null || !usernameField.contains(username)) {
                            return false;
                        }
                    }
                    // 借阅日期开始过滤
                    if (borrowDateStart != null) {
                        LocalDateTime borrowDate = record.getBorrowDate();
                        if (borrowDate == null || borrowDate.isBefore(borrowDateStart)) {
                            return false;
                        }
                    }
                    // 借阅日期结束过滤
                    if (borrowDateEnd != null) {
                        LocalDateTime borrowDate = record.getBorrowDate();
                        if (borrowDate == null || borrowDate.isAfter(borrowDateEnd)) {
                            return false;
                        }
                    }
                    // 应归还日期开始过滤
                    if (dueDateStart != null) {
                        LocalDateTime dueDate = record.getDueDate();
                        if (dueDate == null || dueDate.isBefore(dueDateStart)) {
                            return false;
                        }
                    }
                    // 应归还日期结束过滤
                    if (dueDateEnd != null) {
                        LocalDateTime dueDate = record.getDueDate();
                        if (dueDate == null || dueDate.isAfter(dueDateEnd)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(java.util.stream.Collectors.toList());
        
        log.info("搜索到借阅记录数量: {}", filteredRecords.size());
        return filteredRecords;
    }

    /**
     * 查询逾期的借阅记录
     *
     * @return 逾期借阅记录列表
     */
    public List<BorrowRecord> getOverdueRecords() {
        log.info("查询逾期借阅记录");
        List<BorrowRecord> records = borrowRecordMapper.findOverdueRecords(LocalDateTime.now());
        log.info("查询到逾期借阅记录数量: {}", records.size());
        return records;
    }

    /**
     * 根据用户ID获取借阅记录（包含图书信息）
     *
     * @param userId 用户ID
     * @return 借阅记录列表（包含图书信息）
     */
    public List<BorrowRecordWithBookDTO> getRecordsWithBookByUserId(Integer userId) {
        log.info("查询用户借阅记录（含图书信息）: userId={}", userId);
        List<BorrowRecord> records = borrowRecordMapper.findByUserIdWithInfo(userId);

        List<BorrowRecordWithBookDTO> result = new ArrayList<>();
        for (BorrowRecord record : records) {
            BorrowRecordWithBookDTO dto = new BorrowRecordWithBookDTO();
            dto.setId(record.getId());
            dto.setBookId(record.getBookId());
            dto.setUserId(record.getUserId());
            dto.setBorrowDate(record.getBorrowDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setStatus(record.getStatus());
            dto.setRemark(record.getRemark());
            dto.setUsername(record.getUsername());
            
            // 查询图书信息
            Book book = bookMapper.findById(record.getBookId());
            if (book != null) {
                dto.setBookTitle(book.getTitle());
                dto.setBookIsbn(book.getIsbn());
            }
            
            result.add(dto);
        }

        log.info("查询到用户借阅记录数量: {}", result.size());
        return result;
    }

    /**
     * 读者申请续借
     *
     * 业务逻辑：
     * 1. 只能对借阅中的记录申请续借
     * 2. 续借后状态变为待续借审核
     *
     * @param recordId 借阅记录ID
     * @param userId 用户ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord applyRenew(Integer recordId, Integer userId) {
        log.info("读者申请续借: recordId={}, userId={}", recordId, userId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("续借申请失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!record.getUserId().equals(userId)) {
            log.warn("续借申请失败: 无权操作此记录, recordId={}, userId={}, recordUserId={}",
                    recordId, userId, record.getUserId());
            throw new BusinessException("无权操作此记录");
        }

        if (!BorrowStatus.BORROWING.name().equals(record.getStatus()) &&
            !BorrowStatus.OVERDUE.name().equals(record.getStatus())) {
            log.warn("续借申请失败: 记录状态不是借阅中或已逾期, recordId={}, status={}",
                    recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        record.setStatus(BorrowStatus.RENEW_PENDING.name());
        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("续借申请失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("续借申请成功: recordId={}, status=RENEW_PENDING", recordId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 管理员审批同意续借
     *
     * 业务逻辑：
     * 1. 将应归还日期延长45天
     * 2. 续借次数加1
     * 3. 状态恢复为借阅中
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord approveRenew(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员同意续借: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("同意续借失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!BorrowStatus.RENEW_PENDING.name().equals(record.getStatus())) {
            log.warn("同意续借失败: 记录状态不是待续借审核, recordId={}, status={}",
                    recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        // 延长应归还日期45天
        record.setDueDate(record.getDueDate().plusDays(BORROW_DAYS));
        // 续借次数加1
        int renewCount = record.getRenewalCount() == null ? 1 : record.getRenewalCount() + 1;
        record.setRenewalCount(renewCount);
        // 状态恢复为借阅中
        record.setStatus(BorrowStatus.BORROWING.name());
        record.setOperatorId(operatorId);

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("同意续借失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("同意续借成功: recordId={}, status=BORROWING, dueDate={}, renewalCount={}, operatorId={}",
                recordId, record.getDueDate(), renewCount, operatorId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }

    /**
     * 管理员审批拒绝续借
     *
     * 业务逻辑：
     * 1. 状态变为续借被拒绝
     * 2. 记录操作员ID
     *
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord rejectRenew(Integer recordId) {
        Integer operatorId = UserContext.getUserId();
        log.info("管理员拒绝续借: recordId={}, operatorId={}", recordId, operatorId);

        BorrowRecord record = borrowRecordMapper.findById(recordId);
        if (record == null) {
            log.warn("拒绝续借失败: 记录不存在, recordId={}", recordId);
            throw BusinessException.recordNotFound();
        }

        if (!BorrowStatus.RENEW_PENDING.name().equals(record.getStatus())) {
            log.warn("拒绝续借失败: 记录状态不是待续借审核, recordId={}, status={}",
                    recordId, record.getStatus());
            throw BusinessException.invalidStatus();
        }

        record.setStatus(BorrowStatus.RENEW_REJECTED.name());
        record.setOperatorId(operatorId);

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("拒绝续借失败: 更新状态失败, recordId={}", recordId);
            throw BusinessException.dbOperationFailure();
        }

        log.info("拒绝续借成功: recordId={}, status=RENEW_REJECTED, operatorId={}", recordId, operatorId);
        return borrowRecordMapper.findByIdWithInfo(recordId);
    }
}