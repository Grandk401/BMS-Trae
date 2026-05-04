/**
 * 操作日志查询DTO
 * <p>
 * 用于封装操作日志的查询条件。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 */
@Data
public class OperationLogQueryDTO {

    /**
     * 操作用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}