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

import com.bms.dto.BookSearchDTO;
import com.bms.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 模糊搜索图书（多条件动态组合）
     *
     * @param dto 搜索条件 DTO
     * @return 符合条件的图书列表
     */
    List<Book> searchBooks(BookSearchDTO dto);

    /**
     * 根据ID列表查询图书
     *
     * @param ids 图书ID列表
     * @return 图书列表
     */
    @Select("<script>" +
            "SELECT * FROM book WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Book> findByIds(List<Integer> ids);

    /**
     * 获取所有图书分类
     *
     * @return 分类列表
     */
    @Select("SELECT DISTINCT category FROM book WHERE category IS NOT NULL AND category != '' ORDER BY category")
    List<String> findAllCategories();

    /**
     * 统计图书总数
     *
     * @return 图书总数
     */
    @Select("SELECT COUNT(*) FROM book")
    int countTotalBooks();

    /**
     * 统计已借出图书数量
     *
     * @return 已借出数量
     */
    @Select("SELECT COUNT(*) FROM book WHERE stock > 0")
    int countBorrowedBooks();

    /**
     * 统计可用图书数量
     *
     * @return 可用数量
     */
    @Select("SELECT COALESCE(SUM(stock), 0) FROM book WHERE stock > 0")
    int countAvailableBooks();

    /**
     * 获取各类别图书分布
     *
     * @return 类别统计数据
     */
    @Select("SELECT category AS name, COUNT(*) AS value FROM book WHERE category IS NOT NULL AND category != '' GROUP BY category")
    List<Map<String, Object>> getCategoryDistribution();
}
