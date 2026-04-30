/**
 * 公告 Mapper
 * <p>
 * 提供公告数据访问方法。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.mapper;

import com.bms.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告数据访问接口
 */
@Mapper
public interface AnnouncementMapper {

    /**
     * 插入公告
     *
     * @param announcement 公告实体
     */
    void insert(Announcement announcement);

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     */
    void update(Announcement announcement);

    /**
     * 根据ID删除公告
     *
     * @param id 公告ID
     */
    void deleteById(Integer id);

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告实体
     */
    Announcement findById(Integer id);

    /**
     * 查询所有公告（按排序号升序）
     *
     * @return 公告列表
     */
    List<Announcement> findAll();

    /**
     * 查询启用的公告（按排序号升序）
     *
     * @return 启用的公告列表
     */
    List<Announcement> findEnabled();

    /**
     * 根据标题模糊查询
     *
     * @param title 标题关键词
     * @return 公告列表
     */
    List<Announcement> findByTitleLike(@Param("title") String title);
}
