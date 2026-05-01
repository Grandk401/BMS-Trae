/**
 * 借阅记录实体类
 * <p>
 * 表示图书借阅记录信息，包含借阅的基本属性。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.entity;

import com.bms.common.validation.ValidationGroup;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
public class BorrowRecord implements Serializable {

    /**
     * 记录 ID
     */
    private Integer id;

    /**
     * 图书 ID
     */
    @NotNull(message = "图书 ID 不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Min(value = 1, message = "图书 ID 必须大于 0", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private Integer bookId;

    /**
     * 用户 ID
     */
    @NotNull(message = "用户 ID 不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Min(value = 1, message = "用户 ID 必须大于 0", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private Integer userId;

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
     * 状态（PENDING-待审核, BORROWING-借阅中, RETURNED-已归还, OVERDUE-已逾期, OVERDUE_RETURNED-已逾期但已归还, REJECTED-已拒绝）
     */
    @NotBlank(message = "状态不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String status;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注长度不能超过 200 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String remark;

    /**
     * 操作员ID（记录执行借阅操作的管理员ID）
     */
    private Integer operatorId;

    /**
     * 用户名（用于关联查询）
     */
    private transient String username;

    /**
     * 图书名（用于关联查询）
     */
    private transient String bookName;

    /**
     * 图书ISBN（用于关联查询）
     */
    private transient String isbn;
}