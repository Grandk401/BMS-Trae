/**
 * 图书管理员 - 用户管理控制器
 * <p>
 * 提供图书管理员操作用户的RESTful接口。
 * 图书管理员可修改用户的角色，但只能设置为LIBRARIAN或READER，不能将用户升级为系统管理员，
 * 也不能修改自己的角色。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller.librarian;

import com.bms.common.Result;
import com.bms.dto.UserSearchDTO;
import com.bms.entity.User;
import com.bms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书管理员 - 用户管理 REST API 控制器
 */
@RestController
@RequestMapping("/librarian/users")
@RequiredArgsConstructor
@Slf4j
public class LibrarianUserController {

    private final UserService userService;

    /**
     * 获取所有用户列表
     * <p>
     * 图书管理员可查看所有用户信息。
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
     * 图书管理员可根据用户名、角色、状态等条件搜索用户。
     * </p>
     *
     * @param dto 搜索条件
     * @return 用户列表
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('user:read')")
    public Result<List<User>> searchUsers(UserSearchDTO dto) {
        log.info("图书管理员 - 搜索用户: id={}, username={}, role={}, enabled={}", 
                dto.getId(), dto.getUsername(), dto.getRole(), dto.getEnabled());
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
     * 图书管理员可将普通读者设置为图书管理员，或将图书管理员降级为普通读者。
     * 图书管理员无权将用户升级为系统管理员，也无权修改自己的角色。
     * </p>
     *
     * @param id   用户ID
     * @param role 新角色（仅限LIBRARIAN或READER）
     * @return 操作结果
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> updateUserRole(@PathVariable Integer id, @RequestParam String role) {
        log.info("图书管理员 - 更新用户角色: userId={}, newRole={}", id, role);
        userService.updateUserRole(id, role);
        return Result.success("角色更新成功", null);
    }

    /**
     * 启用或禁用用户
     * <p>
     * 图书管理员可启用或禁用普通读者账户，无权操作系统管理员账户。
     * </p>
     *
     * @param id      用户ID
     * @param enabled 是否启用
     * @return 操作结果
     */
    @PutMapping("/{id}/enabled")
    @PreAuthorize("hasAuthority('role:update')")
    public Result<Void> setUserEnabled(@PathVariable Integer id, @RequestParam Boolean enabled) {
        log.info("图书管理员 - {}用户: userId={}, enabled={}", enabled ? "启用" : "禁用", id, enabled);
        userService.setUserEnabled(id, enabled);
        return Result.success();
    }
}
