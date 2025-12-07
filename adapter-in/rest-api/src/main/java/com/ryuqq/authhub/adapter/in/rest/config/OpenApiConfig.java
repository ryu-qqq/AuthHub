package com.ryuqq.authhub.adapter.in.rest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 설정
 *
 * <p>Swagger UI 및 API 문서 메타데이터를 설정합니다.
 *
 * <p>Gateway-AuthHub 인증 아키텍처:
 *
 * <ul>
 *   <li>Gateway가 JWT 검증 후 X-* 헤더로 인증 정보 전달
 *   <li>AuthHub는 X-User-Id 헤더가 있으면 인증된 요청으로 처리
 *   <li>Swagger UI에서는 X-* 헤더를 직접 설정하여 테스트
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    private static final String GATEWAY_AUTH_SCHEME = "gateway-auth";
    private static final String API_TITLE = "AuthHub API";
    private static final String API_DESCRIPTION =
            """
            B2B Multi-Tenant 인증/인가 플랫폼 API

            ## 인증 방식
            이 API는 Gateway를 통한 인증을 사용합니다:
            1. Client → Gateway: JWT 토큰 전송
            2. Gateway → AuthHub: X-* 헤더로 인증 정보 전달
            3. AuthHub: X-User-Id 헤더가 있으면 인증된 요청으로 처리

            ## X-* 헤더 (Gateway에서 설정)
            - `X-User-Id`: 사용자 UUID (필수)
            - `X-Tenant-Id`: 테넌트 ID
            - `X-Roles`: 역할 목록 (JSON 배열)
            - `X-Permissions`: 권한 목록 (콤마 구분)

            ## Public 엔드포인트
            - POST /api/v1/auth/login
            - POST /api/v1/auth/refresh
            """;
    private static final String API_VERSION = "1.0.0";
    private static final String CONTACT_NAME = "Development Team";
    private static final String CONTACT_EMAIL = "dev@authhub.com";
    private static final String LICENSE_NAME = "Proprietary";

    /**
     * OpenAPI 메타데이터 설정
     *
     * @return OpenAPI 빈
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .components(securityComponents())
                .addSecurityItem(new SecurityRequirement().addList(GATEWAY_AUTH_SCHEME));
    }

    private Info apiInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION)
                .contact(new Contact().name(CONTACT_NAME).email(CONTACT_EMAIL))
                .license(new License().name(LICENSE_NAME));
    }

    private List<Server> servers() {
        return List.of(
                new Server().url("http://localhost:8080").description("Local Development"),
                new Server().url("https://api.authhub.dev").description("Development"),
                new Server().url("https://api.authhub.com").description("Production"));
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes(
                        GATEWAY_AUTH_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-User-Id")
                                .description(
                                        "Gateway에서 전달하는 사용자 ID (UUID 형식). "
                                                + "예: 01912f58-7b9c-7d4e-8b4a-6c5d4e3f2a1b"));
    }
}
