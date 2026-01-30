package com.ryuqq.authhub.adapter.out.client.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Security Client Configuration
 *
 * <p>Security Client 모듈의 설정을 활성화합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityClientConfig {}
