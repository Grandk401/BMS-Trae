package com.bms.service;

import com.bms.common.Role;
import com.bms.entity.User;
import com.bms.exception.BusinessException;
import com.bms.mapper.UserMapper;
import com.bms.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户业务服务层
 * <p>
 * 处理用户相关的业务逻辑，包括用户登录、查询、角色管理等功能。
 * 角色权限关系采用枚举维护，登录时生成含权限的JWT Token。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * <p>
     * 验证用户名密码，验证通过后根据用户角色生成含权限信息的JWT Token。
     * 权限从Role枚举获取，无需查询数据库。
     * </p>
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @return 登录结果，包含success、message、token、user信息
     * @throws BusinessException 用户不存在或密码错误时抛出
     */
    public Map<String, Object> login(String username, String password) {
        log.info("用户登录请求: username={}", username);
        Map<String, Object> result = new HashMap<>();

        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("用户登录失败: 用户不存在, username={}", username);
            throw BusinessException.userNotFound();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("用户登录失败: 密码错误, username={}", username);
            throw BusinessException.passwordWrong();
        }

        Role role = Role.valueOf(user.getRole());
        String token = jwtUtils.generateToken(username, role, role.getPermissions());
        log.info("用户登录成功: username={}, userId={}, role={}", username, user.getId(), role);

        result.put("success", true);
        result.put("message", "登录成功");
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象
     * @throws BusinessException 用户不存在时抛出
     */
    public User getUserByUsername(String username) {
        log.info("查询用户信息: username={}", username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("查询用户失败: 用户不存在, username={}", username);
            throw BusinessException.userNotFound();
        }
        return user;
    }

    /**
     * 根据Token查询用户
     * <p>
     * 从Token中解析出用户名，再查询用户信息。
     * </p>
     *
     * @param token JWT Token
     * @return 用户对象
     * @throws BusinessException Token为空、解析失败或用户不存在时抛出
     */
    public User getUserByToken(String token) {
        log.info("根据 Token 查询用户信息");
        if (token == null || token.isEmpty()) {
            log.warn("Token 为空");
            throw BusinessException.operationFailure("Token 不能为空");
        }

        String username = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            log.warn("Token 解析失败");
            throw BusinessException.operationFailure("无效的 Token");
        }

        return getUserByUsername(username);
    }

    /**
     * 获取所有用户列表
     *
     * @return 所有用户列表
     */
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户对象
     * @throws BusinessException 用户不存在时抛出
     */
    public User getUserById(Integer id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw BusinessException.userNotFound();
        }
        return user;
    }

    /**
     * 更新用户角色
     * <p>
     * 系统管理员可修改用户的角色，有效值为ADMIN、LIBRARIAN、READER。
     * 角色变更后，用户重新登录将获得新角色的权限。
     * </p>
     *
     * @param userId 用户ID
     * @param role   新角色
     * @throws BusinessException 用户不存在或角色值无效时抛出
     */
    public void updateUserRole(Integer userId, String role) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.userNotFound();
        }

        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw BusinessException.operationFailure("无效的角色: " + role);
        }

        userMapper.updateRole(userId, role);
        log.info("更新用户角色: userId={}, newRole={}", userId, role);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @throws BusinessException 用户不存在时抛出
     */
    public void deleteUser(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw BusinessException.userNotFound();
        }
        userMapper.deleteById(userId);
        log.info("删除用户: userId={}", userId);
    }
}
