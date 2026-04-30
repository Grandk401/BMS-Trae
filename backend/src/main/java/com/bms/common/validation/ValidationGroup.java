/**
 * 参数校验分组接口
 * <p>
 * 用于区分不同场景的参数校验规则，如新增和更新操作。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.common.validation;

/**
 * 参数校验分组标记接口
 */
public interface ValidationGroup {

    /**
     * 新增操作校验分组
     */
    interface Add extends ValidationGroup {
    }

    /**
     * 更新操作校验分组
     */
    interface Update extends ValidationGroup {
    }

    /**
     * 查询操作校验分组
     */
    interface Query extends ValidationGroup {
    }
}
