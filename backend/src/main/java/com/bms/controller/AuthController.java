/**
 * 用户认证控制器
 * <p>
 * 提供用户登录、获取用户信息等认证相关的 REST API。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.controller;

import com.bms.common.Result;
import com.bms.entity.User;
import com.bms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户认证 REST API 控制器
 */
@RestController
@RequestMapping("")
@CrossOrigin
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     *
     * @param params 包含 username 和 password 的请求体
     * @return 登录结果，包含 token 和 user 信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        log.info("收到登录请求: username={}", username);

        Map<String, Object> result = userService.login(username, password);
        boolean success = (Boolean) result.get("success");
        
        if (success) {
            return Result.success("登录成功", result);
        } else {
            String message = (String) result.get("message");
            throw new RuntimeException(message);
        }
    }

    /**
     * 获取用户信息接口
     *
     * @param token Authorization 请求头中的 JWT Token
     * @return 用户信息
     */
    @GetMapping("/userinfo")
    public Result<User> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("获取用户信息: token={}", token != null ? "***" : null);
        User user = userService.getUserByToken(token);
        return Result.success("获取成功", user);
    }
}
