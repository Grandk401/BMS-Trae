/**
 * 操作日志控制器
 * <p>
 * 提供操作日志的查询接口，仅供管理员访问。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.admin;

import com.bms.common.Result;
import com.bms.dto.OperationLogQueryDTO;
import com.bms.entity.OperationLog;
import com.bms.service.OperationLogService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/admin/operation-logs")
@Slf4j
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param module       操作模块
     * @param operationType 操作类型
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @return 分页结果
     */
    @GetMapping
    public Result<PageInfo<OperationLog>> searchLogs(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        OperationLogQueryDTO dto = new OperationLogQueryDTO();
        dto.setUserId(userId);
        dto.setUsername(username);
        dto.setModule(module);
        dto.setOperationType(operationType);
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        dto.setPageNum(pageNum);
        dto.setPageSize(pageSize);

        return Result.success("查询成功", operationLogService.searchLogs(dto));
    }

    /**
     * 根据ID查询操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    @GetMapping("/{id}")
    public Result<OperationLog> getLogById(@PathVariable Integer id) {
        OperationLog operationLog = operationLogService.getLogById(id);
        if (operationLog == null) {
            return Result.error("日志不存在");
        }
        return Result.success("查询成功", operationLog);
    }
}