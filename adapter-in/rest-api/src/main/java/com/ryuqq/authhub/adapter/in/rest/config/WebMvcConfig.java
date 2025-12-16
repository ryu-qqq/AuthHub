package com.ryuqq.authhub.adapter.in.rest.config;

import com.ryuqq.authhub.adapter.in.rest.auth.paths.ApiPaths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 설정
 *
 * <p>정적 리소스 매핑 및 MVC 커스터마이징을 담당합니다.
 *
 * <p><strong>REST Docs 경로 매핑:</strong>
 *
 * <ul>
 *   <li>URL: /api/v1/auth/docs/** → classpath:/static/docs/
 *   <li>Gateway 라우팅: /api/v1/auth/** → AuthHub 서비스
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 정적 리소스 핸들러 설정
     *
     * <p>REST Docs HTML 문서를 /api/v1/auth/docs 경로로 서빙합니다. Gateway에서 /api/v1/auth/** 패턴으로 라우팅되므로 모든
     * AuthHub API와 문서가 동일한 prefix를 사용합니다.
     *
     * @param registry 리소스 핸들러 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(ApiPaths.Docs.ALL)
                .addResourceLocations("classpath:/static/docs/");
    }
}
