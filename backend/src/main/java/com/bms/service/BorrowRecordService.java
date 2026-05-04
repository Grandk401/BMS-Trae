/**
 * 借阅记录服务层
 * <p>
 * 提供借阅记录相关的业务逻辑处理，包括借阅申请、审核、归还、逾期处理等功能。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.service;

import com.bms.common.constant.BorrowStatus;
import com.bms.dto.BorrowRecordWithBookDTO;
import com.bms.entity.Book;
import com.bms.entity.BorrowRecord;
import com.bms.exception.BusinessException;
import com.bms.mapper.BookMapper;
import com.bms.mapper.BorrowRecordMapper;
import com.bms.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;

    /**
     * 逾期罚款每天金额（元）
     */
    private static final BigDecimal DAILY_FINE = new BigDecimal("0.2");

    /**
     * 逾期罚款封顶金额（元）
     */
    private static final BigDecimal MAX_FINE = new BigDecimal("10.0");

    /**
     * 获取所有借阅记录（关联用户和图书信息）
     *
     * @return 借阅记录列表
     */
    public List<BorrowRecord> getAllRecords() {
        log.info("查询所有借阅记录");
        return borrowRecordMapper.findAllWithInfo();
    }

    /**
     * 根据 ID 获取借阅记录
     *
     * @param id 借阅记录 ID
     * @return 借阅记录详情
     */
    public BorrowRecord getRecordById(Integer id) {
        log.info("查询借阅记录: recordId={}", id);
        BorrowRecord record = borrowRecordMapper.findByIdWithInfo(id);
        if (record == null) {
            log.warn("查询借阅记录失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }
        return record;
    }

    /**
     * 根据用户ID查询借阅记录（关联图书信息）
     *
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    public List<BorrowRecordWithBookDTO> getRecordsWithBookByUserId(Integer userId) {
        log.info("查询用户借阅记录: userId={}", userId);
        List<BorrowRecord> records = borrowRecordMapper.findByUserIdWithInfo(userId);
        return convertToDTOList(records);
    }

    /**
     * 搜索借阅记录
     *
     * @param bookName      图书名
     * @param username      用户名
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
        return borrowRecordMapper.findAllWithInfo();
    }

    /**
     * 查询逾期借阅记录
     *
     * @return 逾期借阅记录列表
     */
    public List<BorrowRecord> getOverdueRecords() {
        log.info("查询逾期借阅记录");
        return borrowRecordMapper.findByStatus(BorrowStatus.OVERDUE.name());
    }

    /**
     * 申请借阅图书
     *
     * @param bookId 图书ID
     * @param userId 用户ID
     * @return 借阅记录
     */
    @Transactional
    public BorrowRecord applyBorrow(Integer bookId, Integer userId) {
        log.info("申请借阅: bookId={}, userId={}", bookId, userId);

        Book book = bookMapper.findById(bookId);
        if (book == null) {
            log.warn("申请借阅失败: 图书不存在, bookId={}", bookId);
            throw BusinessException.bookNotFound();
        }

        if (book.getStock() <= 0) {
            log.warn("申请借阅失败: 图书库存不足, bookId={}", bookId);
            throw BusinessException.operationFailure("该图书已无库存");
        }

        int activeBorrows = borrowRecordMapper.countActiveBorrowsByUserId(userId);
        if (activeBorrows >= 5) {
            log.warn("申请借阅失败: 用户借阅数量已达上限, userId={}", userId);
            throw BusinessException.operationFailure("您的借阅数量已达上限（5本）");
        }

        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));
        record.setStatus(BorrowStatus.PENDING.name());
        record.setRenewalCount(0);

        int rows = borrowRecordMapper.insert(record);
        if (rows <= 0) {
            log.warn("申请借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("申请借阅成功: recordId={}", record.getId());
        return record;
    }

    /**
     * 同意借阅申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord approveBorrow(Integer id) {
        log.info("同意借阅申请: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("同意借阅失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("同意借阅失败: 记录状态不是待审核, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能同意待审核的借阅申请");
        }

        record.setStatus(BorrowStatus.BORROWING.name());
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("同意借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("同意借阅成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 拒绝借阅申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord rejectBorrow(Integer id) {
        log.info("拒绝借阅申请: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("拒绝借阅失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("拒绝借阅失败: 记录状态不是待审核, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能拒绝待审核的借阅申请");
        }

        record.setStatus(BorrowStatus.REJECTED.name());

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("拒绝借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("拒绝借阅成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 确认归还
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord confirmReturn(Integer id) {
        log.info("确认归还: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("确认归还失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.BORROWING.name().equals(record.getStatus()) && 
            !BorrowStatus.OVERDUE.name().equals(record.getStatus())) {
            log.warn("确认归还失败: 记录状态不是借阅中或逾期, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能确认借阅中或逾期的图书归还");
        }

        String status = BorrowStatus.OVERDUE.name().equals(record.getStatus()) 
            ? BorrowStatus.OVERDUE_RETURNED.name() 
            : BorrowStatus.RETURNED.name();

        record.setStatus(status);
        record.setReturnDate(LocalDateTime.now());

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("确认归还失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("确认归还成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 标记逾期
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord markOverdue(Integer id) {
        log.info("标记逾期: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("标记逾期失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.BORROWING.name().equals(record.getStatus())) {
            log.warn("标记逾期失败: 记录状态不是借阅中, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能标记借阅中的记录为逾期");
        }

        record.setStatus(BorrowStatus.OVERDUE.name());
        record.setOverdueDays(0);
        record.setFine(BigDecimal.ZERO);

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("标记逾期失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("标记逾期成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 申请续借
     *
     * @param id     借阅记录 ID
     * @param userId 用户ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord applyRenew(Integer id, Integer userId) {
        log.info("申请续借: recordId={}, userId={}", id, userId);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("申请续借失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!record.getUserId().equals(userId)) {
            log.warn("申请续借失败: 无权操作他人借阅记录, recordId={}, userId={}", id, userId);
            throw BusinessException.operationFailure("无权操作他人借阅记录");
        }

        if (!BorrowStatus.BORROWING.name().equals(record.getStatus())) {
            log.warn("申请续借失败: 记录状态不是借阅中, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能对借阅中的图书申请续借");
        }

        if (record.getRenewalCount() >= 3) {
            log.warn("申请续借失败: 续借次数已达上限, recordId={}", id);
            throw BusinessException.operationFailure("续借次数已达上限（3次）");
        }

        record.setStatus(BorrowStatus.PENDING.name());
        record.setRenewalCount(record.getRenewalCount() + 1);

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("申请续借失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("申请续借成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 同意续借申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord approveRenew(Integer id) {
        log.info("同意续借申请: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("同意续借失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("同意续借失败: 记录状态不是待审核, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能同意待审核的续借申请");
        }

        record.setStatus(BorrowStatus.BORROWING.name());
        record.setDueDate(LocalDateTime.now().plusDays(30));

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("同意续借失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("同意续借成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 拒绝续借申请
     *
     * @param id 借阅记录 ID
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord rejectRenew(Integer id) {
        log.info("拒绝续借申请: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("拒绝续借失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        if (!BorrowStatus.PENDING.name().equals(record.getStatus())) {
            log.warn("拒绝续借失败: 记录状态不是待审核, recordId={}, status={}", id, record.getStatus());
            throw BusinessException.operationFailure("只能拒绝待审核的续借申请");
        }

        record.setStatus(BorrowStatus.BORROWING.name());
        record.setRenewalCount(record.getRenewalCount() - 1);

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("拒绝续借失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("拒绝续借成功: recordId={}", id);
        return borrowRecordMapper.findByIdWithInfo(id);
    }

    /**
     * 添加借阅记录（管理员直接添加）
     *
     * @param record 借阅记录对象
     * @return 添加后的借阅记录
     */
    @Transactional
    public BorrowRecord addRecord(BorrowRecord record) {
        log.info("添加借阅记录: bookId={}, userId={}", record.getBookId(), record.getUserId());

        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30));
        record.setRenewalCount(0);
        record.setOverdueDays(0);
        record.setFine(BigDecimal.ZERO);

        int rows = borrowRecordMapper.insert(record);
        if (rows <= 0) {
            log.warn("添加借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("添加借阅成功: recordId={}", record.getId());
        return borrowRecordMapper.findByIdWithInfo(record.getId());
    }

    /**
     * 更新借阅记录
     *
     * @param record 借阅记录对象
     * @return 更新后的借阅记录
     */
    @Transactional
    public BorrowRecord updateRecord(BorrowRecord record) {
        log.info("更新借阅记录: recordId={}", record.getId());

        BorrowRecord existing = borrowRecordMapper.findById(record.getId());
        if (existing == null) {
            log.warn("更新借阅失败: 记录不存在, recordId={}", record.getId());
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        int rows = borrowRecordMapper.update(record);
        if (rows <= 0) {
            log.warn("更新借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("更新借阅成功: recordId={}", record.getId());
        return borrowRecordMapper.findByIdWithInfo(record.getId());
    }

    /**
     * 删除借阅记录
     *
     * @param id 借阅记录 ID
     */
    @Transactional
    public void deleteRecord(Integer id) {
        log.info("删除借阅记录: recordId={}", id);

        BorrowRecord record = borrowRecordMapper.findById(id);
        if (record == null) {
            log.warn("删除借阅失败: 记录不存在, recordId={}", id);
            throw BusinessException.operationFailure("借阅记录不存在");
        }

        int rows = borrowRecordMapper.deleteById(id);
        if (rows <= 0) {
            log.warn("删除借阅失败: 数据库操作失败");
            throw BusinessException.dbOperationFailure();
        }

        log.info("删除借阅成功: recordId={}", id);
    }

    /**
     * 自动标记逾期借阅记录
     * <p>
     * 扫描所有借阅中状态的记录，判断是否已过应归还日期，若是则标记为逾期。
     * </p>
     *
     * @return 标记的逾期记录数量
     */
    @Transactional
    public int autoMarkOverdue() {
        log.info("自动标记逾期借阅记录开始");

        LocalDateTime now = LocalDateTime.now();
        List<BorrowRecord> overdueRecords = borrowRecordMapper.findOverdueRecords(now);

        int count = 0;
        for (BorrowRecord record : overdueRecords) {
            if (BorrowStatus.BORROWING.name().equals(record.getStatus())) {
                record.setStatus(BorrowStatus.OVERDUE.name());
                record.setOverdueDays(0);
                record.setFine(BigDecimal.ZERO);
                borrowRecordMapper.update(record);
                count++;
                log.info("标记逾期: recordId={}, bookId={}, userId={}", 
                    record.getId(), record.getBookId(), record.getUserId());
            }
        }

        log.info("自动标记逾期借阅记录完成，共标记 {} 条", count);
        return count;
    }

    /**
     * 自动计算逾期罚款
     * <p>
     * 对所有逾期未归还的记录计算罚款：
     * - 从被标记为逾期当天开始计算
     * - 每天罚款0.2元
     * - 单本书封顶10元
     * - 一旦到达封顶则自动禁用该用户账户
     * </p>
     *
     * @return int数组，[0]为处理的记录数，[1]为触发账户禁用的数量
     */
    @Transactional
    public int[] autoCalculateOverdueFines() {
        log.info("自动计算逾期罚款开始");

        List<BorrowRecord> overdueRecords = borrowRecordMapper.findAllOverdueNotReturned();
        
        int processedCount = 0;
        int disabledCount = 0;
        List<Integer> disabledUserIds = new ArrayList<>();

        for (BorrowRecord record : overdueRecords) {
            processedCount++;
            
            int newOverdueDays = record.getOverdueDays() + 1;
            BigDecimal newFine = DAILY_FINE.multiply(BigDecimal.valueOf(newOverdueDays))
                .setScale(2, RoundingMode.HALF_UP);
            
            // 罚款封顶处理
            if (newFine.compareTo(MAX_FINE) > 0) {
                newFine = MAX_FINE;
            }

            // 更新罚款信息
            borrowRecordMapper.updateOverdueFine(newOverdueDays, newFine, record.getId());
            
            log.info("更新逾期罚款: recordId={}, overdueDays={}, fine={}", 
                record.getId(), newOverdueDays, newFine);

            // 到达封顶金额且用户尚未被禁用
            if (newFine.compareTo(MAX_FINE) == 0 && !disabledUserIds.contains(record.getUserId())) {
                userMapper.updateEnabled(record.getUserId(), false);
                disabledUserIds.add(record.getUserId());
                disabledCount++;
                log.info("逾期罚款到达封顶，禁用用户账户: userId={}", record.getUserId());
            }
        }

        log.info("自动计算逾期罚款完成，共处理 {} 条记录，其中 {} 条触发账户禁用", processedCount, disabledCount);
        return new int[]{processedCount, disabledCount};
    }

    /**
     * 将借阅记录列表转换为带图书信息的DTO列表
     *
     * @param records 借阅记录列表
     * @return DTO列表
     */
    private List<BorrowRecordWithBookDTO> convertToDTOList(List<BorrowRecord> records) {
        List<Integer> bookIds = records.stream()
            .map(BorrowRecord::getBookId)
            .distinct()
            .collect(Collectors.toList());

        Map<Integer, Book> bookMap = bookMapper.findByIds(bookIds).stream()
            .collect(Collectors.toMap(Book::getId, book -> book));

        return records.stream()
            .map(record -> {
                BorrowRecordWithBookDTO dto = new BorrowRecordWithBookDTO();
                dto.setId(record.getId());
                dto.setBookId(record.getBookId());
                dto.setUserId(record.getUserId());
                dto.setBorrowDate(record.getBorrowDate());
                dto.setDueDate(record.getDueDate());
                dto.setReturnDate(record.getReturnDate());
                dto.setStatus(record.getStatus());
                dto.setRemark(record.getRemark());
                dto.setOperatorId(record.getOperatorId());
                dto.setRenewalCount(record.getRenewalCount());
                dto.setOverdueDays(record.getOverdueDays());
                dto.setFine(record.getFine());
                dto.setUsername(record.getUsername());
                
                Book book = bookMap.get(record.getBookId());
                if (book != null) {
                    dto.setBookName(book.getTitle());
                    dto.setBookTitle(book.getTitle());
                    dto.setIsbn(book.getIsbn());
                    dto.setBookIsbn(book.getIsbn());
                    dto.setAuthor(book.getAuthor());
                    dto.setCategory(book.getCategory());
                } else {
                    dto.setBookName(record.getBookName());
                    dto.setBookTitle(record.getBookName());
                    dto.setIsbn(record.getIsbn());
                    dto.setBookIsbn(record.getIsbn());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }
}