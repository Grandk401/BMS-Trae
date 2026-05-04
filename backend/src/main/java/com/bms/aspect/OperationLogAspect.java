/**
 * 操作日志切面
 * <p>
 * 使用AOP方式自动记录用户操作日志。
 * </p>
 *
 * @author BMS Team
 * @version 1.0.0
 * @since 2026
 */
package com.bms.aspect;

import com.bms.annotation.OperationLogging;
import com.bms.entity.OperationLog;
import com.bms.service.OperationLogService;
import com.bms.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 环绕通知，记录操作日志
     *
     * @param joinPoint 切点
     * @param logging   操作日志注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(logging)")
    public Object logOperation(ProceedingJoinPoint joinPoint, OperationLogging logging) throws Throwable {
        // 先执行目标方法
        Object result = joinPoint.proceed();

        try {
            // 获取当前用户信息
            Integer userId = UserContext.getUserId();
            String username = UserContext.getUsername();

            // 构建操作日志对象
            OperationLog operationLog = new OperationLog();
            operationLog.setUserId(userId);
            operationLog.setUsername(username != null ? username : "匿名用户");
            operationLog.setModule(logging.module());
            operationLog.setOperationType(logging.type());
            operationLog.setDescription(logging.description());
            operationLog.setIpAddress(getClientIp());
            operationLog.setCreateTime(LocalDateTime.now());

            // 尝试从方法参数中提取目标ID（优先从第一个Integer类型参数获取）
            extractTargetId(joinPoint, operationLog);

            // 异步保存操作日志
            operationLogService.saveLogAsync(operationLog);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * 从方法参数中提取目标ID
     *
     * @param joinPoint    切点
     * @param operationLog 操作日志对象
     */
    private void extractTargetId(ProceedingJoinPoint joinPoint, OperationLog operationLog) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            if (args != null && args.length > 0) {
                // 优先查找名为id的参数
                for (int i = 0; i < paramNames.length; i++) {
                    if ("id".equals(paramNames[i]) && args[i] instanceof Integer) {
                        operationLog.setTargetId((Integer) args[i]);
                        return;
                    }
                }

                // 如果没有找到id参数，尝试获取第一个Integer类型的参数
                for (Object arg : args) {
                    if (arg instanceof Integer) {
                        operationLog.setTargetId((Integer) arg);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("提取目标ID失败: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端IP地址
     *
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
                // 处理多个IP的情况（取第一个）
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
}