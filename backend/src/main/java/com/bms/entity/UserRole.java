/**
 * 用户角色关联实体类
 */
package com.bms.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联实体类
 */
@Data
public class UserRole implements Serializable {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;
}
