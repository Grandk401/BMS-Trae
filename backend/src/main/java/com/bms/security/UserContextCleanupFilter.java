/**
 * 用户上下文清理过滤器
 * 
 * 在请求处理完成后清理 ThreadLocal 中的用户上下文，防止内存泄漏
 */
package com.bms.security;

import com.bms.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class UserContextCleanupFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 在请求完全处理完成后才清理用户上下文
            UserContext.clear();
            log.debug("用户上下文已清理");
        }
    }
}
