/**
 * 操作日志 Mapper 接口
 * <p>
 * 提供操作日志数据访问层的 CRUD 操作。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.mapper;

import com.bms.dto.OperationLogQueryDTO;
import com.bms.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 操作日志数据访问接口
 */
@Mapper
public interface OperationLogMapper {

    /**
     * 插入操作日志
     *
     * @param log 操作日志对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO operation_log(user_id, username, operation_type, module, target_id, description, ip_address, create_time) " +
            "VALUES(#{userId}, #{username}, #{operationType}, #{module}, #{targetId}, #{description}, #{ipAddress}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    /**
     * 根据条件查询操作日志列表
     *
     * @param dto 查询条件
     * @return 操作日志列表
     */
    List<OperationLog> searchLogs(OperationLogQueryDTO dto);

    /**
     * 根据ID查询操作日志
     *
     * @param id 日志ID
     * @return 操作日志对象
     */
    @Select("SELECT * FROM operation_log WHERE id = #{id}")
    OperationLog findById(Integer id);
}