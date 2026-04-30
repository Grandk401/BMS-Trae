/**
 * 用户角色枚举类
 * <p>
 * 定义系统用户的角色类型，便于统一管理和维护权限。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.common.constant;

/**
 * 用户角色枚举
 */
public enum UserRole {

    /**
     * 管理员
     */
    ADMIN("管理员"),

    /**
     * 普通用户
     */
    USER("普通用户");

    /**
     * 角色描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param description 角色描述
     */
    UserRole(String description) {
        this.description = description;
    }

    /**
     * 获取角色描述
     *
     * @return 角色描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据角色码获取枚举值
     *
     * @param role 角色码
     * @return 对应的枚举值
     */
    public static UserRole fromValue(String role) {
        for (UserRole userRole : UserRole.values()) {
            if (userRole.name().equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("未知的用户角色: " + role);
    }

    /**
     * 判断角色是否有效
     *
     * @param role 角色码
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValid(String role) {
        try {
            fromValue(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取所有有效角色的正则表达式
     *
     * @return 正则表达式
     */
    public static String getRegexPattern() {
        StringBuilder pattern = new StringBuilder("^(");
        UserRole[] values = UserRole.values();
        for (int i = 0; i < values.length; i++) {
            pattern.append(values[i].name());
            if (i < values.length - 1) {
                pattern.append("|");
            }
        }
        pattern.append(")$");
        return pattern.toString();
    }
}
