package com.ryuqq.setof.adapter.out.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Security Adapter Configuration
 *
 * <p>Security 모듈의 설정을 활성화합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityAdapterConfig {
}
