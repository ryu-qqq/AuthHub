package com.ryuqq.authhub.domain.security.ratelimit.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * RateLimitType Enum 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RateLimitType 단위 테스트")
class RateLimitTypeTest {

    @Test
    @DisplayName("IP_BASED 타입의 표시 이름과 설명을 확인할 수 있다")
    void testIpBasedProperties() {
        // when
        RateLimitType type = RateLimitType.IP_BASED;

        // then
        assertThat(type.getDisplayName()).isEqualTo("IP-based Rate Limiting");
        assertThat(type.getDescription()).contains("IP 주소");
    }

    @Test
    @DisplayName("USER_BASED 타입의 표시 이름과 설명을 확인할 수 있다")
    void testUserBasedProperties() {
        // when
        RateLimitType type = RateLimitType.USER_BASED;

        // then
        assertThat(type.getDisplayName()).isEqualTo("User-based Rate Limiting");
        assertThat(type.getDescription()).contains("사용자");
    }

    @Test
    @DisplayName("ENDPOINT_BASED 타입의 표시 이름과 설명을 확인할 수 있다")
    void testEndpointBasedProperties() {
        // when
        RateLimitType type = RateLimitType.ENDPOINT_BASED;

        // then
        assertThat(type.getDisplayName()).isEqualTo("Endpoint-based Rate Limiting");
        assertThat(type.getDescription()).contains("엔드포인트");
    }

    @Test
    @DisplayName("isIpBased()로 IP 기반 타입을 확인할 수 있다")
    void testIsIpBased() {
        assertThat(RateLimitType.IP_BASED.isIpBased()).isTrue();
        assertThat(RateLimitType.USER_BASED.isIpBased()).isFalse();
        assertThat(RateLimitType.ENDPOINT_BASED.isIpBased()).isFalse();
    }

    @Test
    @DisplayName("isUserBased()로 사용자 기반 타입을 확인할 수 있다")
    void testIsUserBased() {
        assertThat(RateLimitType.USER_BASED.isUserBased()).isTrue();
        assertThat(RateLimitType.IP_BASED.isUserBased()).isFalse();
        assertThat(RateLimitType.ENDPOINT_BASED.isUserBased()).isFalse();
    }

    @Test
    @DisplayName("isEndpointBased()로 엔드포인트 기반 타입을 확인할 수 있다")
    void testIsEndpointBased() {
        assertThat(RateLimitType.ENDPOINT_BASED.isEndpointBased()).isTrue();
        assertThat(RateLimitType.IP_BASED.isEndpointBased()).isFalse();
        assertThat(RateLimitType.USER_BASED.isEndpointBased()).isFalse();
    }
}
