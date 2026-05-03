package com.bms.controller.admin;

import com.bms.common.Result;
import com.bms.dto.UserSearchDTO;
import com.bms.entity.User;
import com.bms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理员 - 用户管理控制器
 * <p>
 * 提供系统管理员操作用户的RESTful接口，包括用户查询、角色变更、删除等功能。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取所有用户列表
     * <p>
     * 系统管理员可查看所有用户信息，包含用户名、角色、创建时间等。
     * </p>
     *
     * @return 所有用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    /**
     * 动态搜索用户列表
     * <p>
     * 系统管理员可根据用户名、角色、状态等条件搜索用户。
     * </p>
     *
     * @param dto 搜索条件
     * @return 用户列表
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:read')")
    public Result<List<User>> searchUsers(UserSearchDTO dto) {
        return Result.success(userService.searchUsers(dto));
    }

    /**
     * 根据ID获取用户信息
     * <p>
     * 通过用户ID查询指定用户的详细信息。
     * </p>
     *
     * @param id 用户ID
     * @return 指定用户信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public Result<User> getUserById(@PathVariable Integer id) {
        return Result.success(userService.getUserById(id));
    }

    /**
     * 更新用户角色
     * <p>
     * 系统管理员可修改用户的角色，将其从普通读者升级为图书管理员，
     * 或从图书管理员降级为普通读者等。
     * </p>
     *
     * @param id   用户ID
     * @param role 新角色（ADMIN、LIBRARIAN、READER）
     * @return 操作结果
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> updateUserRole(@PathVariable Integer id, @RequestParam String role) {
        userService.updateUserRole(id, role);
        return Result.success();
    }

    /**
     * 删除用户
     * <p>
     * 系统管理员可删除指定用户，删除后该用户将无法登录系统。
     * </p>
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 启用或禁用用户
     * <p>
     * 系统管理员可启用或禁用指定用户账户。
     * </p>
     *
     * @param id      用户ID
     * @param enabled 是否启用
     * @return 操作结果
     */
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> setUserEnabled(@PathVariable Integer id, @RequestParam Boolean enabled) {
        userService.setUserEnabled(id, enabled);
        return Result.success();
    }

    /**
     * 创建新用户
     * <p>
     * 系统管理员可创建新用户账号，密码会使用BCrypt加密后存储。
     * </p>
     *
     * @param username 用户名
     * @param password 密码（明文）
     * @param role     角色（ADMIN、LIBRARIAN、READER）
     * @return 创建的用户信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public Result<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user.getUsername(), user.getPassword(), 
                user.getRole() != null ? user.getRole() : "READER");
        return Result.success(createdUser);
    }
}
