/**
 * 权限编码常量类
 */
package com.bms.common.constant;

/**
 * 权限编码常量
 */
public class PermissionCode {

    // ========== 用户管理 ==========
    public static final String USER_CREATE = "user:create";
    public static final String USER_READ = "user:read";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_READ_OWN = "user:read:own";
    public static final String USER_UPDATE_OWN = "user:update:own";

    // ========== 图书管理 ==========
    public static final String BOOK_CREATE = "book:create";
    public static final String BOOK_READ = "book:read";
    public static final String BOOK_UPDATE = "book:update";
    public static final String BOOK_DELETE = "book:delete";

    // ========== 借阅管理 ==========
    public static final String BORROW_CREATE = "borrow:create";
    public static final String BORROW_READ = "borrow:read";
    public static final String BORROW_UPDATE = "borrow:update";
    public static final String BORROW_DELETE = "borrow:delete";
    public static final String BORROW_READ_OWN = "borrow:read:own";
    public static final String BORROW_CREATE_OWN = "borrow:create:own";

    // ========== 角色管理 ==========
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_READ = "role:read";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";

    // ========== 权限管理 ==========
    public static final String PERMISSION_CREATE = "permission:create";
    public static final String PERMISSION_READ = "permission:read";
    public static final String PERMISSION_UPDATE = "permission:update";
    public static final String PERMISSION_DELETE = "permission:delete";

    // ========== 系统配置 ==========
    public static final String SYSTEM_CONFIG = "system:config";
}
