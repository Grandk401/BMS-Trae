/**
 * 用户搜索条件 DTO
 * <p>
 * 用于用户模糊搜索的条件封装，支持多条件组合查询。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.dto;

import lombok.Data;

/**
 * 用户搜索条件 DTO
 */
@Data
public class UserSearchDTO {

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名（模糊匹配）
     */
    private String username;

    /**
     * 角色（精确匹配）
     */
    private String role;

    /**
     * 状态（精确匹配，true-正常，false-禁用）
     */
    private Boolean enabled;
}
