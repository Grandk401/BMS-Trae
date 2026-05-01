/**
 * 借阅记录 Mapper 接口
 * <p>
 * 提供借阅记录数据访问层的 CRUD 操作。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.mapper;

import com.bms.entity.BorrowRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅记录数据访问接口
 */
@Mapper
public interface BorrowRecordMapper {

    /**
     * 查询所有借阅记录（关联用户和图书信息）
     *
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findAllWithInfo();

    /**
     * 根据 ID 查询借阅记录（关联用户和图书信息）
     *
     * @param id 借阅记录 ID
     * @return 借阅记录对象
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.id = #{id}")
    BorrowRecord findByIdWithInfo(Integer id);

    /**
     * 根据 ID 查询借阅记录
     *
     * @param id 借阅记录 ID
     * @return 借阅记录对象
     */
    @Select("SELECT * FROM borrow_record WHERE id = #{id}")
    BorrowRecord findById(Integer id);

    /**
     * 插入新借阅记录
     *
     * @param record 借阅记录对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO borrow_record(book_id, user_id, borrow_date, due_date, status) " +
            "VALUES(#{bookId}, #{userId}, #{borrowDate}, #{dueDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BorrowRecord record);

    /**
     * 更新借阅记录
     *
     * @param record 借阅记录对象
     * @return 影响的行数
     */
    @Update("UPDATE borrow_record SET book_id=#{bookId}, user_id=#{userId}, " +
            "borrow_date=#{borrowDate}, due_date=#{dueDate}, return_date=#{returnDate}, " +
            "status=#{status}, remark=#{remark}, operator_id=#{operatorId}, renewal_count=#{renewalCount} WHERE id=#{id}")
    int update(BorrowRecord record);

    /**
     * 更新借阅记录状态
     *
     * @param id     借阅记录ID
     * @param status 状态
     * @param returnDate 实际归还日期（可为null）
     * @return 影响的行数
     */
    @Update("UPDATE borrow_record SET status=#{status}, return_date=#{returnDate} WHERE id=#{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status, @Param("returnDate") LocalDateTime returnDate);

    /**
     * 根据用户ID查询借阅记录（关联用户和图书信息）
     *
     * @param userId 用户ID
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.user_id = #{userId} ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findByUserIdWithInfo(Integer userId);

    /**
     * 根据用户ID查询正在进行中的借阅数量（待审核、借阅中）
     *
     * @param userId 用户ID
     * @return 借阅数量
     */
    @Select("SELECT COUNT(*) FROM borrow_record WHERE user_id = #{userId} AND status IN ('PENDING', 'BORROWING')")
    int countActiveBorrowsByUserId(Integer userId);

    /**
     * 根据状态查询借阅记录
     *
     * @param status 状态
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.status = #{status} ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findByStatus(String status);

    /**
     * 查询所有借阅记录（关联用户和图书信息）用于搜索
     *
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "ORDER BY br.borrow_date DESC")
    List<BorrowRecord> findAllRecordsWithInfo();

    /**
     * 查询逾期的借阅记录（借阅中状态且已过应归还日期）
     *
     * @param now 当前时间
     * @return 借阅记录列表
     */
    @Select("SELECT br.*, u.username, b.title as book_name, b.isbn " +
            "FROM borrow_record br " +
            "LEFT JOIN user u ON br.user_id = u.id " +
            "LEFT JOIN book b ON br.book_id = b.id " +
            "WHERE br.status = 'BORROWING' AND br.due_date < #{now} ORDER BY br.due_date ASC")
    List<BorrowRecord> findOverdueRecords(LocalDateTime now);

    /**
     * 根据 ID 删除借阅记录
     *
     * @param id 借阅记录 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM borrow_record WHERE id = #{id}")
    int deleteById(Integer id);
}