/**
 * 角色权限关联实体类
 */
package com.bms.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色权限关联实体类
 */
@Data
public class RolePermission implements Serializable {

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 权限ID
     */
    private Integer permissionId;
}
