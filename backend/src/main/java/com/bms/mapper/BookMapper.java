/**
 * 图书 Mapper 接口
 * <p>
 * 提供图书数据访问层的 CRUD 操作。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.mapper;

import com.bms.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 图书数据访问接口
 */
@Mapper
public interface BookMapper {

    /**
     * 查询所有图书
     *
     * @return 图书列表
     */
    @Select("SELECT * FROM book ORDER BY create_time DESC")
    List<Book> findAll();

    /**
     * 根据 ID 查询图书
     *
     * @param id 图书 ID
     * @return 图书对象
     */
    @Select("SELECT * FROM book WHERE id = #{id}")
    Book findById(Integer id);

    /**
     * 根据 ISBN 查询图书
     *
     * @param isbn 图书 ISBN
     * @return 图书对象
     */
    @Select("SELECT * FROM book WHERE isbn = #{isbn}")
    Book findByIsbn(String isbn);

    /**
     * 插入新图书
     *
     * @param book 图书对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO book(isbn, title, author, publisher, publish_date, category, price, stock, description) " +
            "VALUES(#{isbn}, #{title}, #{author}, #{publisher}, #{publishDate}, #{category}, #{price}, #{stock}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Book book);

    /**
     * 更新图书信息
     *
     * @param book 图书对象
     * @return 影响的行数
     */
    @Update("UPDATE book SET isbn=#{isbn}, title=#{title}, author=#{author}, publisher=#{publisher}, " +
            "publish_date=#{publishDate}, category=#{category}, price=#{price}, stock=#{stock}, description=#{description}, " +
            "update_time=NOW() WHERE id=#{id}")
    int update(Book book);

    /**
     * 根据 ID 删除图书
     *
     * @param id 图书 ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM book WHERE id = #{id}")
    int deleteById(Integer id);
}
