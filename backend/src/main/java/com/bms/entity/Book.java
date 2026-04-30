/**
 * 图书实体类
 * <p>
 * 表示图书信息，包含图书的基本属性。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.entity;

import com.bms.common.validation.ValidationGroup;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
public class Book implements Serializable {

    /**
     * 图书 ID
     */
    private Integer id;

    /**
     * ISBN 编号
     */
    @NotBlank(message = "ISBN 不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Size(min = 10, max = 20, message = "ISBN 长度必须在 10-20 之间", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String isbn;

    /**
     * 书名
     */
    @NotBlank(message = "书名不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Size(max = 100, message = "书名长度不能超过 100 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String title;

    /**
     * 作者
     */
    @NotBlank(message = "作者不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Size(max = 50, message = "作者长度不能超过 50 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String author;

    /**
     * 出版社
     */
    @NotBlank(message = "出版社不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Size(max = 100, message = "出版社长度不能超过 100 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String publisher;

    /**
     * 出版日期
     */
    private LocalDateTime publishDate;

    /**
     * 分类
     */
    @Size(max = 50, message = "分类长度不能超过 50 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String category;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @DecimalMin(value = "0.01", message = "价格必须大于 0", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @DecimalMax(value = "99999.99", message = "价格不能超过 99999.99", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private Double price;

    /**
     * 库存数量
     */
    @NotNull(message = "库存不能为空", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Min(value = 0, message = "库存不能为负数", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    @Max(value = 9999, message = "库存不能超过 9999", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private Integer stock;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过 500 字符", groups = {ValidationGroup.Add.class, ValidationGroup.Update.class})
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
