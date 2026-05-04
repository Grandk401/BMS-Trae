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
import com.bms.entity.OperationLog;
import com.bms.entity.User;
import com.bms.service.OperationLogService;
import com.bms.service.UserService;
import com.bms.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    @Autowired
    private OperationLogService operationLogService;

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
            // 登录成功后，手动记录日志
            User user = (User) result.get("user");
            recordLoginLog(user);
            return Result.success("登录成功", result);
        } else {
            String message = (String) result.get("message");
            throw new RuntimeException(message);
        }
    }

    /**
     * 记录登录日志
     * @param user 用户信息
     */
    private void recordLoginLog(User user) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(user.getId());
            operationLog.setUsername(user.getUsername());
            operationLog.setModule("AUTH");
            operationLog.setOperationType("LOGIN");
            operationLog.setDescription("用户登录");
            operationLog.setIpAddress(getClientIp());
            operationLog.setCreateTime(LocalDateTime.now());
            operationLogService.saveLogAsync(operationLog);
            log.info("记录登录日志: username={}", user.getUsername());
        } catch (Exception e) {
            log.error("记录登录日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取客户端IP
     * @return IP地址
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                if (ip != null && ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("获取客户端IP失败: {}", e.getMessage());
        }
        return "unknown";
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
