/**
 * 自定义业务异常类
 * <p>
 * 用于封装业务逻辑中的异常情况。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.exception;

import com.bms.common.ExceptionCode;
import lombok.Getter;

/**
 * 自定义业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 构造函数（使用错误码和消息）
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（只使用消息，默认错误码）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ExceptionCode.ERROR_OPERATION;
        this.message = message;
    }

    /**
     * 用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ExceptionCode.ERROR_USER_NOT_FOUND, ExceptionCode.MESSAGE_USER_NOT_FOUND);
    }

    /**
     * 密码错误异常
     */
    public static BusinessException passwordWrong() {
        return new BusinessException(ExceptionCode.ERROR_PASSWORD_WRONG, ExceptionCode.MESSAGE_PASSWORD_WRONG);
    }

    /**
     * 用户已存在异常
     */
    public static BusinessException userExists() {
        return new BusinessException(ExceptionCode.ERROR_USER_EXISTS, ExceptionCode.MESSAGE_USER_EXISTS);
    }

    /**
     * 图书不存在异常
     */
    public static BusinessException bookNotFound() {
        return new BusinessException(ExceptionCode.ERROR_BOOK_NOT_FOUND, ExceptionCode.MESSAGE_BOOK_NOT_FOUND);
    }

    /**
     * 图书库存不足异常
     */
    public static BusinessException bookStockNotEnough() {
        return new BusinessException(ExceptionCode.ERROR_BOOK_STOCK_NOT_ENOUGH, ExceptionCode.MESSAGE_BOOK_STOCK_NOT_ENOUGH);
    }

    /**
     * 图书已存在异常
     */
    public static BusinessException bookExists() {
        return new BusinessException(ExceptionCode.ERROR_BOOK_EXISTS, ExceptionCode.MESSAGE_BOOK_EXISTS);
    }

    /**
     * 借阅记录不存在异常
     */
    public static BusinessException recordNotFound() {
        return new BusinessException(ExceptionCode.ERROR_RECORD_NOT_FOUND, ExceptionCode.MESSAGE_RECORD_NOT_FOUND);
    }

    /**
     * 图书未归还异常
     */
    public static BusinessException bookNotReturned() {
        return new BusinessException(ExceptionCode.ERROR_BOOK_NOT_RETURNED, ExceptionCode.MESSAGE_BOOK_NOT_RETURNED);
    }

    /**
     * 数据库操作失败异常
     */
    public static BusinessException dbOperationFailure() {
        return new BusinessException(ExceptionCode.ERROR_DB_OPERATION, ExceptionCode.MESSAGE_DB_OPERATION);
    }

    /**
     * 操作失败异常
     */
    public static BusinessException operationFailure(String message) {
        return new BusinessException(ExceptionCode.ERROR_OPERATION, message);
    }

    /**
     * 库存不足异常
     */
    public static BusinessException stockInsufficient() {
        return new BusinessException(ExceptionCode.ERROR_STOCK_INSUFFICIENT, ExceptionCode.MESSAGE_STOCK_INSUFFICIENT);
    }

    /**
     * 超过最大借阅限制异常
     */
    public static BusinessException maxBorrowLimitExceeded() {
        return new BusinessException(ExceptionCode.ERROR_MAX_BORROW_LIMIT_EXCEEDED, ExceptionCode.MESSAGE_MAX_BORROW_LIMIT_EXCEEDED);
    }

    /**
     * 状态无效异常
     */
    public static BusinessException invalidStatus() {
        return new BusinessException(ExceptionCode.ERROR_INVALID_STATUS, ExceptionCode.MESSAGE_INVALID_STATUS);
    }

    /**
     * 无效操作异常
     */
    public static BusinessException invalidOperation(String message) {
        return new BusinessException(ExceptionCode.ERROR_OPERATION, message);
    }
}
