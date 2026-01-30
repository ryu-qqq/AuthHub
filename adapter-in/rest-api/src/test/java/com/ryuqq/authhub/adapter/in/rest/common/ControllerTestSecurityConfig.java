package com.ryuqq.authhub.adapter.in.rest.common;

import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 컨트롤러 테스트용 Security 설정
 *
 * <p>@WebMvcTest에서 Security를 비활성화하고, 테스트에 필요한 최소 설정만 제공합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@TestConfiguration
@EnableMethodSecurity
public class ControllerTestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .build();
    }

    /**
     * @PreAuthorize 권한 체크용 Mock Access Checker
     *
     * <p>테스트에서는 모든 권한을 허용합니다.
     */
    @Bean(name = "access")
    public TestAccessChecker accessChecker() {
        return new TestAccessChecker();
    }

    /** ErrorMapperRegistry - 테스트용 빈 (빈 목록으로 초기화) */
    @Bean
    public ErrorMapperRegistry errorMapperRegistry(List<ErrorMapper> mappers) {
        return new ErrorMapperRegistry(mappers);
    }

    /** 테스트용 Access Checker - 모든 권한 허용 */
    public static class TestAccessChecker {

        public boolean hasPermission(String resource, String action) {
            return true;
        }

        public boolean self(String userId) {
            return true;
        }

        public boolean superAdmin() {
            return true;
        }

        public boolean organization(String organizationId, String action) {
            return true;
        }

        public boolean tenant(String tenantId, String action) {
            return true;
        }

        public boolean role(String roleId, String action) {
            return true;
        }
    }
}
