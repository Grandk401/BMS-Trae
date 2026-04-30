/**
 * 借阅状态枚举类
 * <p>
 * 定义图书借阅的状态，便于统一管理和维护。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.common.constant;

/**
 * 借阅状态枚举
 */
public enum BorrowStatus {

    /**
     * 借阅中
     */
    BORROWING("借阅中"),

    /**
     * 已归还
     */
    RETURNED("已归还");

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param description 状态描述
     */
    BorrowStatus(String description) {
        this.description = description;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码获取枚举值
     *
     * @param status 状态码
     * @return 对应的枚举值
     */
    public static BorrowStatus fromValue(String status) {
        for (BorrowStatus borrowStatus : BorrowStatus.values()) {
            if (borrowStatus.name().equalsIgnoreCase(status)) {
                return borrowStatus;
            }
        }
        throw new IllegalArgumentException("未知的借阅状态: " + status);
    }

    /**
     * 判断状态是否有效
     *
     * @param status 状态码
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValid(String status) {
        try {
            fromValue(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取所有有效状态的正则表达式
     *
     * @return 正则表达式
     */
    public static String getRegexPattern() {
        StringBuilder pattern = new StringBuilder("^(");
        BorrowStatus[] values = BorrowStatus.values();
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
