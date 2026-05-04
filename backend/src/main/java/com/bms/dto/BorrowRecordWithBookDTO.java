/**
 * 借阅记录 DTO（包含图书信息）
 * <p>
 * 用于返回给读者的借阅记录详情，包含关联的图书信息。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借阅记录 DTO（包含图书信息）
 */
@Data
public class BorrowRecordWithBookDTO {

    /**
     * 记录 ID
     */
    private Integer id;

    /**
     * 图书 ID
     */
    private Integer bookId;

    /**
     * 图书名称
     */
    private String bookTitle;

    /**
     * 图书名称（兼容字段）
     */
    private String bookName;

    /**
     * 图书 ISBN
     */
    private String bookIsbn;

    /**
     * 图书 ISBN（兼容字段）
     */
    private String isbn;

    /**
     * 图书作者
     */
    private String author;

    /**
     * 图书分类
     */
    private String category;

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 借阅日期
     */
    private LocalDateTime borrowDate;

    /**
     * 应归还日期
     */
    private LocalDateTime dueDate;

    /**
     * 实际归还日期
     */
    private LocalDateTime returnDate;

    /**
     * 状态（如：PENDING, BORROWING, RETURNED, OVERDUE, OVERDUE_RETURNED, REJECTED）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作员ID
     */
    private Integer operatorId;

    /**
     * 续借次数
     */
    private Integer renewalCount;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    /**
     * 罚款金额（元）
     */
    private BigDecimal fine;
}