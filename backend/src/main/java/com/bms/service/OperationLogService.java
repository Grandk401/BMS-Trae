/**
 * 操作日志服务类
 * <p>
 * 提供操作日志的业务处理逻辑。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.service;

import com.bms.dto.OperationLogQueryDTO;
import com.bms.entity.OperationLog;
import com.bms.mapper.OperationLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务类
 */
@Service
@Slf4j
public class OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 异步保存操作日志
     *
     * @param operationLog 操作日志对象
     */
    @Async
    public void saveLogAsync(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
            log.debug("操作日志保存成功: {}", operationLog.getDescription());
        } catch (Exception e) {
            log.error("操作日志保存失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据条件查询操作日志（分页）
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    public PageInfo<OperationLog> searchLogs(OperationLogQueryDTO dto) {
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<OperationLog> logs = operationLogMapper.searchLogs(dto);
        return new PageInfo<>(logs);
    }

    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志对象
     */
    public OperationLog getLogById(Integer id) {
        return operationLogMapper.findById(id);
    }
}