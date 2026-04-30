/**
 * 异常常量类
 * <p>
 * 统一管理业务异常的错误码和错误消息。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.common;

/**
 * 异常常量类
 */
public class ExceptionCode {

    /**
     * 成功
     */
    public static final Integer SUCCESS = 200;

    /**
     * 未知错误
     */
    public static final Integer ERROR_UNKNOWN = 500;

    /**
     * 参数错误
     */
    public static final Integer ERROR_PARAM = 400;

    /**
     * 未授权
     */
    public static final Integer ERROR_UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    public static final Integer ERROR_FORBIDDEN = 403;

    /**
     * 资源未找到
     */
    public static final Integer ERROR_NOT_FOUND = 404;

    // ==================== 用户相关 ====================

    /**
     * 用户不存在
     */
    public static final Integer ERROR_USER_NOT_FOUND = 1001;
    public static final String MESSAGE_USER_NOT_FOUND = "用户不存在";

    /**
     * 密码错误
     */
    public static final Integer ERROR_PASSWORD_WRONG = 1002;
    public static final String MESSAGE_PASSWORD_WRONG = "密码错误";

    /**
     * 用户已存在
     */
    public static final Integer ERROR_USER_EXISTS = 1003;
    public static final String MESSAGE_USER_EXISTS = "用户已存在";

    // ==================== 图书相关 ====================

    /**
     * 图书不存在
     */
    public static final Integer ERROR_BOOK_NOT_FOUND = 2001;
    public static final String MESSAGE_BOOK_NOT_FOUND = "图书不存在";

    /**
     * 图书库存不足
     */
    public static final Integer ERROR_BOOK_STOCK_NOT_ENOUGH = 2002;
    public static final String MESSAGE_BOOK_STOCK_NOT_ENOUGH = "图书库存不足";

    /**
     * 图书已存在
     */
    public static final Integer ERROR_BOOK_EXISTS = 2003;
    public static final String MESSAGE_BOOK_EXISTS = "图书已存在";

    // ==================== 借阅记录相关 ====================

    /**
     * 借阅记录不存在
     */
    public static final Integer ERROR_RECORD_NOT_FOUND = 3001;
    public static final String MESSAGE_RECORD_NOT_FOUND = "借阅记录不存在";

    /**
     * 图书未归还
     */
    public static final Integer ERROR_BOOK_NOT_RETURNED = 3002;
    public static final String MESSAGE_BOOK_NOT_RETURNED = "图书未归还";

    /**
     * 库存不足
     */
    public static final Integer ERROR_STOCK_INSUFFICIENT = 3003;
    public static final String MESSAGE_STOCK_INSUFFICIENT = "库存不足";

    /**
     * 超过最大借阅限制
     */
    public static final Integer ERROR_MAX_BORROW_LIMIT_EXCEEDED = 3004;
    public static final String MESSAGE_MAX_BORROW_LIMIT_EXCEEDED = "超过最大借阅限制";

    /**
     * 状态无效
     */
    public static final Integer ERROR_INVALID_STATUS = 3005;
    public static final String MESSAGE_INVALID_STATUS = "状态无效";

    // ==================== 系统相关 ====================

    /**
     * 数据库操作失败
     */
    public static final Integer ERROR_DB_OPERATION = 9001;
    public static final String MESSAGE_DB_OPERATION = "数据库操作失败";

    /**
     * 操作失败
     */
    public static final Integer ERROR_OPERATION = 9002;
    public static final String MESSAGE_OPERATION = "操作失败";
}
