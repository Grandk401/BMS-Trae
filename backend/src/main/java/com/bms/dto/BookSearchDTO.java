/**
 * 图书搜索条件 DTO
 * <p>
 * 用于图书模糊搜索的条件封装，支持多条件组合查询。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.dto;

import lombok.Data;

/**
 * 图书搜索条件 DTO
 */
@Data
public class BookSearchDTO {

    /**
     * 书名（模糊匹配）
     */
    private String title;

    /**
     * 作者（模糊匹配）
     */
    private String author;

    /**
     * ISBN（模糊匹配）
     */
    private String isbn;

    /**
     * 分类（模糊匹配）
     */
    private String category;

    /**
     * 出版社（模糊匹配）
     */
    private String publisher;
}
