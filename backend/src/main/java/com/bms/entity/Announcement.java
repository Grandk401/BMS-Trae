/**
 * 公告实体类
 * <p>
 * 用于存储图书馆公告信息。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
public class Announcement {

    /**
     * 公告 ID
     */
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 排序号（数字越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
