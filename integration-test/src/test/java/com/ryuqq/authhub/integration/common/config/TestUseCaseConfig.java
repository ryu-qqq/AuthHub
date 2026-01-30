package com.ryuqq.authhub.integration.common.config;

import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.port.in.query.GetMyContextUseCase;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 테스트용 UseCase Mock 설정.
 *
 * <p>구현체가 없는 UseCase 인터페이스에 대해 Mock 빈을 제공합니다.
 */
@TestConfiguration
public class TestUseCaseConfig {

    /**
     * GetMyContextUseCase Mock 빈.
     *
     * <p>테스트에서 /api/v1/auth/me 엔드포인트 호출 시 사용됩니다.
     */
    @Bean
    @Primary
    public GetMyContextUseCase getMyContextUseCase() {
        return userId ->
                new MyContextResponse(
                        userId,
                        "test@example.com",
                        "Test User",
                        "test-tenant-id",
                        "Test Tenant",
                        "test-org-id",
                        "Test Organization",
                        List.of(),
                        List.of());
    }
}
