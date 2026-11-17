package com.lxz.careercode.admin.config;

import com.lxz.careercode.admin.common.AdminInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：拦截器 + 跨域。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                // 这三个不需要登录：ping 查活，login 本身，init 首次建账号
                .excludePathPatterns("/api/admin/ping", "/api/admin/login", "/api/admin/init");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 本地前端 5173 直连 8100 时需要；走 vite 代理时其实用不到，留着方便真机调试
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
