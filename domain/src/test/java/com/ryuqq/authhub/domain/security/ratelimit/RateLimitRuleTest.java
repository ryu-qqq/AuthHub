package com.ryuqq.authhub.domain.security.ratelimit;

import com.ryuqq.authhub.domain.security.ratelimit.exception.RateLimitExceededException;
import com.ryuqq.authhub.domain.security.ratelimit.vo.LimitCount;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitRuleId;
import com.ryuqq.authhub.domain.security.ratelimit.vo.RateLimitType;
import com.ryuqq.authhub.domain.security.ratelimit.vo.TimeWindow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * RateLimitRule Aggregate Root 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("RateLimitRule 단위 테스트")
class RateLimitRuleTest {

    @Test
    @DisplayName("create()로 새로운 RateLimitRule을 생성할 수 있다")
    void testCreate() {
        // given
        RateLimitType type = RateLimitType.IP_BASED;
        LimitCount limitCount = LimitCount.of(100);
        TimeWindow timeWindow = TimeWindow.ofSeconds(60);

        // when
        RateLimitRule rule = RateLimitRule.create(type, limitCount, timeWindow);

        // then
        assertThat(rule).isNotNull();
        assertThat(rule.getId()).isNotNull();
        assertThat(rule.getType()).isEqualTo(type);
        assertThat(rule.getLimitCount()).isEqualTo(limitCount);
        assertThat(rule.getTimeWindow()).isEqualTo(timeWindow);
        assertThat(rule.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("reconstruct()로 기존 데이터로부터 RateLimitRule을 재구성할 수 있다")
    void testReconstruct() {
        // given
        RateLimitRuleId id = RateLimitRuleId.newId();
        RateLimitType type = RateLimitType.USER_BASED;
        LimitCount limitCount = LimitCount.of(1000);
        TimeWindow timeWindow = TimeWindow.ofMinutes(1);
        Instant createdAt = Instant.now().minusSeconds(3600);

        // when
        RateLimitRule rule = RateLimitRule.reconstruct(id, type, limitCount, timeWindow, createdAt);

        // then
        assertThat(rule.getId()).isEqualTo(id);
        assertThat(rule.getType()).isEqualTo(type);
        assertThat(rule.getLimitCount()).isEqualTo(limitCount);
        assertThat(rule.getTimeWindow()).isEqualTo(timeWindow);
        assertThat(rule.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("isExceeded()로 제한 초과 여부를 확인할 수 있다 - 핵심 비즈니스 로직")
    void testIsExceeded() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThat(rule.isExceeded(50)).isFalse();
        assertThat(rule.isExceeded(99)).isFalse();
        assertThat(rule.isExceeded(100)).isTrue();
        assertThat(rule.isExceeded(101)).isTrue();
    }

    @Test
    @DisplayName("isExceeded()에 음수 currentCount를 전달하면 예외가 발생한다")
    void testIsExceededWithNegativeCount() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThatThrownBy(() -> rule.isExceeded(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("isWithinLimit()로 제한 이내 여부를 확인할 수 있다")
    void testIsWithinLimit() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.USER_BASED,
                LimitCount.of(50),
                TimeWindow.ofMinutes(1)
        );

        // when & then
        assertThat(rule.isWithinLimit(25)).isTrue();
        assertThat(rule.isWithinLimit(49)).isTrue();
        assertThat(rule.isWithinLimit(50)).isFalse();
        assertThat(rule.isWithinLimit(51)).isFalse();
    }

    @Test
    @DisplayName("getRemainingCount()로 남은 요청 횟수를 계산할 수 있다")
    void testGetRemainingCount() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.ENDPOINT_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThat(rule.getRemainingCount(0)).isEqualTo(100);
        assertThat(rule.getRemainingCount(50)).isEqualTo(50);
        assertThat(rule.getRemainingCount(99)).isEqualTo(1);
        assertThat(rule.getRemainingCount(100)).isEqualTo(0);
        assertThat(rule.getRemainingCount(150)).isEqualTo(0);
    }

    @Test
    @DisplayName("ensureNotExceeded()는 제한을 초과하지 않으면 예외를 발생시키지 않는다")
    void testEnsureNotExceededWithValidCount() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThatCode(() -> rule.ensureNotExceeded(50))
                .doesNotThrowAnyException();
        assertThatCode(() -> rule.ensureNotExceeded(99))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ensureNotExceeded()는 제한을 초과하면 RateLimitExceededException을 발생시킨다")
    void testEnsureNotExceededWithExceededCount() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThatThrownBy(() -> rule.ensureNotExceeded(100))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("Rate limit exceeded");

        assertThatThrownBy(() -> rule.ensureNotExceeded(150))
                .isInstanceOf(RateLimitExceededException.class)
                .hasMessageContaining("Rate limit exceeded");
    }

    @Test
    @DisplayName("Law of Demeter를 준수하는 헬퍼 메서드들이 정상 동작한다")
    void testLawOfDemeterHelpers() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofMinutes(1)
        );

        // when & then
        assertThat(rule.getIdAsString()).isNotBlank();
        assertThat(rule.getTypeDisplayName()).isEqualTo("IP-based Rate Limiting");
        assertThat(rule.getTypeDescription()).contains("IP 주소");
        assertThat(rule.getLimitCountValue()).isEqualTo(100);
        assertThat(rule.getTimeWindowSeconds()).isEqualTo(60);
    }

    @Test
    @DisplayName("타입 확인 메서드들이 정상 동작한다")
    void testTypeChecks() {
        // given
        RateLimitRule ipRule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );
        RateLimitRule userRule = RateLimitRule.create(
                RateLimitType.USER_BASED,
                LimitCount.of(1000),
                TimeWindow.ofMinutes(1)
        );
        RateLimitRule endpointRule = RateLimitRule.create(
                RateLimitType.ENDPOINT_BASED,
                LimitCount.of(500),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThat(ipRule.isIpBased()).isTrue();
        assertThat(ipRule.isUserBased()).isFalse();
        assertThat(ipRule.isEndpointBased()).isFalse();

        assertThat(userRule.isUserBased()).isTrue();
        assertThat(userRule.isIpBased()).isFalse();
        assertThat(userRule.isEndpointBased()).isFalse();

        assertThat(endpointRule.isEndpointBased()).isTrue();
        assertThat(endpointRule.isIpBased()).isFalse();
        assertThat(endpointRule.isUserBased()).isFalse();
    }

    @Test
    @DisplayName("같은 ID를 가진 RateLimitRule은 동등하다")
    void testEquality() {
        // given
        RateLimitRuleId id = RateLimitRuleId.newId();
        RateLimitType type = RateLimitType.IP_BASED;
        LimitCount limitCount = LimitCount.of(100);
        TimeWindow timeWindow = TimeWindow.ofSeconds(60);
        Instant createdAt = Instant.now();

        RateLimitRule rule1 = RateLimitRule.reconstruct(id, type, limitCount, timeWindow, createdAt);
        RateLimitRule rule2 = RateLimitRule.reconstruct(id, type, limitCount, timeWindow, createdAt);

        // when & then
        assertThat(rule1).isEqualTo(rule2);
        assertThat(rule1.hashCode()).isEqualTo(rule2.hashCode());
    }

    @Test
    @DisplayName("다른 ID를 가진 RateLimitRule은 동등하지 않다")
    void testInequality() {
        // given
        RateLimitRule rule1 = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );
        RateLimitRule rule2 = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when & then
        assertThat(rule1).isNotEqualTo(rule2);
    }

    @Test
    @DisplayName("toString()이 유용한 디버그 정보를 제공한다")
    void testToString() {
        // given
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(100),
                TimeWindow.ofSeconds(60)
        );

        // when
        String result = rule.toString();

        // then
        assertThat(result).contains("RateLimitRule");
        assertThat(result).contains("type=IP_BASED");
        assertThat(result).contains("limitCount=");
        assertThat(result).contains("timeWindow=");
    }

    @Test
    @DisplayName("실전 시나리오 - IP 기반 로그인 API Rate Limiting (60초에 20회 제한)")
    void testRealScenarioLoginApi() {
        // given - 로그인 API Rate Limit 규칙 (60초에 20회)
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.IP_BASED,
                LimitCount.of(20),
                TimeWindow.ofSeconds(60)
        );

        // when & then - 19회까지는 허용
        for (int i = 1; i <= 19; i++) {
            assertThat(rule.isExceeded(i)).isFalse();
            assertThat(rule.getRemainingCount(i)).isEqualTo(20 - i);
        }

        // 20회째부터 차단
        assertThat(rule.isExceeded(20)).isTrue();
        assertThat(rule.getRemainingCount(20)).isEqualTo(0);

        // 초과 시 예외 발생
        assertThatThrownBy(() -> rule.ensureNotExceeded(20))
                .isInstanceOf(RateLimitExceededException.class);
    }

    @Test
    @DisplayName("실전 시나리오 - 사용자 기반 API Rate Limiting (1분에 1000회 제한)")
    void testRealScenarioApiRateLimit() {
        // given - 인증된 사용자 API Rate Limit 규칙 (1분에 1000회)
        RateLimitRule rule = RateLimitRule.create(
                RateLimitType.USER_BASED,
                LimitCount.of(1000),
                TimeWindow.ofMinutes(1)
        );

        // when & then - 정상 사용 범위
        assertThat(rule.isExceeded(500)).isFalse();
        assertThat(rule.getRemainingCount(500)).isEqualTo(500);

        // 제한 임박
        assertThat(rule.isExceeded(999)).isFalse();
        assertThat(rule.getRemainingCount(999)).isEqualTo(1);

        // 제한 초과
        assertThat(rule.isExceeded(1000)).isTrue();
        assertThat(rule.isExceeded(1500)).isTrue();
    }
}
