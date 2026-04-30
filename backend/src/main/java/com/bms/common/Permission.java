package com.bms.common;

/**
 * 权限枚举
 */
public enum Permission {
    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    USER_READ_OWN("user:read:own"),
    USER_UPDATE_OWN("user:update:own"),

    BOOK_CREATE("book:create"),
    BOOK_READ("book:read"),
    BOOK_UPDATE("book:update"),
    BOOK_DELETE("book:delete"),

    BORROW_CREATE("borrow:create"),
    BORROW_READ("borrow:read"),
    BORROW_UPDATE("borrow:update"),
    BORROW_DELETE("borrow:delete"),
    BORROW_READ_OWN("borrow:read:own"),
    BORROW_CREATE_OWN("borrow:create:own"),

    ROLE_CREATE("role:create"),
    ROLE_READ("role:read"),
    ROLE_UPDATE("role:update"),
    ROLE_DELETE("role:delete"),

    PERMISSION_CREATE("permission:create"),
    PERMISSION_READ("permission:read"),
    PERMISSION_UPDATE("permission:update"),
    PERMISSION_DELETE("permission:delete"),

    SYSTEM_CONFIG("system:config");

    private final String code;

    Permission(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
