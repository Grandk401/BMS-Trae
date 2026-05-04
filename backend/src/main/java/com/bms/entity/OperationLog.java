/**
 * 操作日志实体类
 * <p>
 * 用于记录系统中的用户操作行为，支持审计和追踪。
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
 * 操作日志实体类
 */
@Data
public class OperationLog {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型：ADD/UPDATE/DELETE/QUERY/LOGIN/LOGOUT
     */
    private String operationType;

    /**
     * 操作模块：BOOK/USER/BORROW_RECORD/ANNOUNCEMENT
     */
    private String module;

    /**
     * 操作目标ID
     */
    private Integer targetId;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 客户端IP地址
     */
    private String ipAddress;

    /**
     * 操作时间
     */
    private LocalDateTime createTime;
}