/**
 * 公告服务
 * <p>
 * 提供公告管理业务逻辑。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.service;

import com.bms.entity.Announcement;
import com.bms.exception.BusinessException;
import com.bms.mapper.AnnouncementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现
 */
@Service
@Slf4j
public class AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    /**
     * 添加公告
     *
     * @param announcement 公告实体
     */
    public void addAnnouncement(Announcement announcement) {
        log.info("添加公告: title={}", announcement.getTitle());
        
        if (announcement.getSortOrder() == null) {
            announcement.setSortOrder(0);
        }
        
        announcementMapper.insert(announcement);
        log.info("添加公告成功: id={}", announcement.getId());
    }

    /**
     * 更新公告
     *
     * @param announcement 公告实体
     */
    public void updateAnnouncement(Announcement announcement) {
        log.info("更新公告: id={}, title={}", announcement.getId(), announcement.getTitle());
        
        Announcement existing = announcementMapper.findById(announcement.getId());
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcementMapper.update(announcement);
        log.info("更新公告成功: id={}", announcement.getId());
    }

    /**
     * 删除公告
     *
     * @param id 公告ID
     */
    public void deleteAnnouncement(Integer id) {
        log.info("删除公告: id={}", id);
        
        Announcement existing = announcementMapper.findById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcementMapper.deleteById(id);
        log.info("删除公告成功: id={}", id);
    }

    /**
     * 根据ID查询公告
     *
     * @param id 公告ID
     * @return 公告实体
     */
    public Announcement getAnnouncementById(Integer id) {
        return announcementMapper.findById(id);
    }

    /**
     * 查询所有公告
     *
     * @return 公告列表
     */
    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.findAll();
    }

    /**
     * 查询启用的公告
     *
     * @return 启用的公告列表
     */
    public List<Announcement> getEnabledAnnouncements() {
        return announcementMapper.findEnabled();
    }

    /**
     * 根据标题模糊查询
     *
     * @param title 标题关键词
     * @return 公告列表
     */
    public List<Announcement> searchByTitle(String title) {
        return announcementMapper.findByTitleLike(title);
    }
}
