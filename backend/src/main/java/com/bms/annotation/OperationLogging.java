/**
 * 操作日志注解
 * <p>
 * 用于标记需要记录操作日志的方法。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogging {

    /**
     * 操作模块
     */
    String module();

    /**
     * 操作类型
     */
    String type();

    /**
     * 操作描述
     */
    String description() default "";
}