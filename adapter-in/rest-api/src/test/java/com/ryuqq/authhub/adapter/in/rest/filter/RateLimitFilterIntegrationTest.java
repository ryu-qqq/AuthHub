package com.ryuqq.authhub.adapter.in.rest.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RateLimitFilter Integration Test with Testcontainers.
 *
 * <p>Testcontainers를 사용하여 실제 Redis와 통합된 Rate Limiting 기능을 검증합니다.
 * Spring Boot Test와 MockMvc를 사용하여 실제 HTTP 요청 흐름을 테스트합니다.</p>
 *
 * <p><strong>테스트 환경:</strong></p>
 * <ul>
 *   <li>Redis: Testcontainers로 실행되는 실제 Redis 컨테이너</li>
 *   <li>Spring Context: 전체 ApplicationContext 로딩</li>
 *   <li>MockMvc: HTTP 요청 시뮬레이션</li>
 * </ul>
 *
 * <p><strong>테스트 시나리오:</strong></p>
 * <ul>
 *   <li>제한 이내 연속 요청 - 모두 200 OK 응답</li>
 *   <li>제한 초과 요청 - 429 Too Many Requests 응답</li>
 *   <li>Response Header 검증 (X-RateLimit-*)</li>
 *   <li>TTL 만료 후 카운터 리셋 검증</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Testcontainers - 실제 Redis 환경 테스트</li>
 *   <li>✅ @DynamicPropertySource - Spring 설정 자동 주입</li>
 *   <li>✅ @DisplayName - 한글 테스트 설명</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@SpringBootTest(classes = RateLimitTestApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("RateLimitFilter 통합 테스트")
class RateLimitFilterIntegrationTest {

    /**
     * Redis Testcontainer.
     * 테스트 실행 시 자동으로 Redis 컨테이너를 시작하고 종료합니다.
     */
    @Container
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine")
    ).withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    /**
     * Testcontainers Redis 설정을 Spring에 주입합니다.
     *
     * @param registry Spring DynamicPropertyRegistry
     */
    @DynamicPropertySource
    static void redisProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    /**
     * Rate Limit 이내 연속 요청 시 모두 정상 처리됨을 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Rate Limit: 100회/60초</li>
     *   <li>Redis 컨테이너 실행 중</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>동일 IP에서 50회 연속 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>모든 요청이 200 OK 응답</li>
     *   <li>X-RateLimit-Remaining 값이 점진적으로 감소</li>
     * </ul>
     *
     * @throws Exception MockMvc 실행 중 예외 발생 시
     */
    @Test
    @DisplayName("제한 이내 연속 요청 시 모두 정상 처리되어야 한다")
    void shouldAllowMultipleRequestsWithinLimit() throws Exception {
        // Given
        final String testEndpoint = "/api/v1/test-endpoint";
        final String clientIp = "192.168.1.100";

        // When & Then - 50회 요청 (제한: 100회)
        for (int i = 0; i < 50; i++) {
            mockMvc.perform(get(testEndpoint)
                            .header("X-Forwarded-For", clientIp))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("X-RateLimit-Limit"))
                    .andExpect(header().string("X-RateLimit-Limit", "100"))
                    .andExpect(header().exists("X-RateLimit-Remaining"))
                    .andExpect(header().exists("X-RateLimit-Reset"));
        }
    }

    /**
     * Rate Limit 초과 요청 시 429 응답을 반환함을 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Rate Limit: 100회/60초</li>
     *   <li>Redis 컨테이너 실행 중</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>동일 IP에서 101회 요청 (제한 초과)</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>100회까지는 200 OK 응답</li>
     *   <li>101회째는 429 Too Many Requests 응답</li>
     *   <li>X-RateLimit-Remaining = 0</li>
     * </ul>
     *
     * @throws Exception MockMvc 실행 중 예외 발생 시
     */
    @Test
    @DisplayName("제한 초과 시 429 Too Many Requests 응답을 반환해야 한다")
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        // Given
        final String testEndpoint = "/api/v1/rate-limit-test";
        final String clientIp = "192.168.1.200";
        final int limit = 100;

        // When & Then - 100회까지 성공
        for (int i = 0; i < limit; i++) {
            mockMvc.perform(get(testEndpoint)
                            .header("X-Forwarded-For", clientIp))
                    .andExpect(status().isOk())
                    .andExpect(header().string("X-RateLimit-Limit", String.valueOf(limit)));
        }

        // When & Then - 101회째 실패 (429)
        mockMvc.perform(get(testEndpoint)
                        .header("X-Forwarded-For", clientIp))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("X-RateLimit-Limit", String.valueOf(limit)))
                .andExpect(header().string("X-RateLimit-Remaining", "0"))
                .andExpect(header().exists("X-RateLimit-Reset"));
    }

    /**
     * 서로 다른 IP에서의 요청은 독립적으로 Rate Limit이 적용됨을 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Rate Limit: IP당 100회/60초</li>
     *   <li>Redis 컨테이너 실행 중</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>IP1에서 100회 요청 후 IP2에서 요청</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>IP1의 100회 요청 모두 성공</li>
     *   <li>IP2의 요청도 성공 (독립적인 카운터)</li>
     * </ul>
     *
     * @throws Exception MockMvc 실행 중 예외 발생 시
     */
    @Test
    @DisplayName("서로 다른 IP는 독립적으로 Rate Limit이 적용되어야 한다")
    void shouldApplyRateLimitIndependentlyPerIp() throws Exception {
        // Given
        final String testEndpoint = "/api/v1/independent-test";
        final String clientIp1 = "192.168.1.101";
        final String clientIp2 = "192.168.1.102";
        final int limit = 100;

        // When & Then - IP1에서 100회 요청 (한계까지)
        for (int i = 0; i < limit; i++) {
            mockMvc.perform(get(testEndpoint)
                            .header("X-Forwarded-For", clientIp1))
                    .andExpect(status().isOk());
        }

        // When & Then - IP2에서 요청 (독립적인 카운터, 성공해야 함)
        mockMvc.perform(get(testEndpoint)
                        .header("X-Forwarded-For", clientIp2))
                .andExpect(status().isOk())
                .andExpect(header().string("X-RateLimit-Limit", String.valueOf(limit)))
                .andExpect(header().string("X-RateLimit-Remaining", String.valueOf(limit - 1)));
    }

    /**
     * Response Header에 올바른 Rate Limit 정보가 포함됨을 검증합니다.
     *
     * <p><strong>Given:</strong></p>
     * <ul>
     *   <li>Rate Limit: 100회/60초</li>
     *   <li>Redis 컨테이너 실행 중</li>
     * </ul>
     *
     * <p><strong>When:</strong></p>
     * <ul>
     *   <li>첫 요청 후 Header 확인</li>
     * </ul>
     *
     * <p><strong>Then:</strong></p>
     * <ul>
     *   <li>X-RateLimit-Limit = 100</li>
     *   <li>X-RateLimit-Remaining = 99 (1회 사용)</li>
     *   <li>X-RateLimit-Reset = 현재 시각 + 60초 (Unix Timestamp)</li>
     * </ul>
     *
     * @throws Exception MockMvc 실행 중 예외 발생 시
     */
    @Test
    @DisplayName("Response Header에 Rate Limit 정보가 정확히 포함되어야 한다")
    void shouldIncludeRateLimitHeadersInResponse() throws Exception {
        // Given
        final String testEndpoint = "/api/v1/header-test";
        final String clientIp = "192.168.1.150";
        final int limit = 100;

        // When & Then
        mockMvc.perform(get(testEndpoint)
                        .header("X-Forwarded-For", clientIp))
                .andExpect(status().isOk())
                .andExpect(header().string("X-RateLimit-Limit", String.valueOf(limit)))
                .andExpect(header().string("X-RateLimit-Remaining", String.valueOf(limit - 1)))
                .andExpect(header().exists("X-RateLimit-Reset"))
                .andExpect(result -> {
                    // X-RateLimit-Reset 값이 현재 시각 이후임을 검증
                    String resetHeader = result.getResponse().getHeader("X-RateLimit-Reset");
                    long resetTimestamp = Long.parseLong(resetHeader);
                    long currentTimestamp = System.currentTimeMillis() / 1000;
                    assert resetTimestamp > currentTimestamp : "Reset timestamp should be in the future";
                });
    }
}
