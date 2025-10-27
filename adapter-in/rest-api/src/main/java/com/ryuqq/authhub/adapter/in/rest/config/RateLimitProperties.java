package com.ryuqq.authhub.adapter.in.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Rate Limit 설정 프로퍼티.
 *
 * <p>application.yml에서 Rate Limiting 관련 설정을 주입받는 Configuration Properties 클래스입니다.
 * Spring Boot의 @ConfigurationProperties를 사용하여 type-safe한 설정값 관리를 제공합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Rate Limit 시간 윈도우(초) 관리</li>
 *   <li>타입별 제한 횟수 관리 (IP, USER, ENDPOINT)</li>
 *   <li>설정값 검증 및 기본값 제공</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Adapter-In-Rest Layer - REST API 설정 관심사</li>
 *   <li>✅ @ConfigurationProperties 사용 - Type-safe 설정</li>
 * </ul>
 *
 * <p><strong>설정 예시 (application.yml):</strong></p>
 * <pre>
 * rate-limit:
 *   time-window-seconds: 60
 *   ip-based-limit: 100
 *   user-based-limit: 1000
 *   endpoint-based-limit: 5000
 * </pre>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * {@code @Component}
 * public class RateLimitFilter extends OncePerRequestFilter {
 *     private final RateLimitProperties properties;
 *
 *     public RateLimitFilter(RateLimitProperties properties) {
 *         this.properties = properties;
 *     }
 *
 *     protected void doFilterInternal(...) {
 *         long ttl = properties.getTimeWindowSeconds();
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    /**
     * Rate Limit 시간 윈도우 (초).
     * 기본값: 60초 (1분)
     */
    private long timeWindowSeconds = 60L;

    /**
     * IP 기반 Rate Limit 제한 횟수.
     * 기본값: 100회/시간윈도우
     */
    private int ipBasedLimit = 100;

    /**
     * 사용자 기반 Rate Limit 제한 횟수.
     * 기본값: 1000회/시간윈도우
     */
    private int userBasedLimit = 1000;

    /**
     * 엔드포인트 기반 Rate Limit 제한 횟수.
     * 기본값: 5000회/시간윈도우
     */
    private int endpointBasedLimit = 5000;

    /**
     * 기본 생성자.
     * Spring Boot가 설정값을 주입할 때 사용합니다.
     */
    public RateLimitProperties() {
        // Spring Boot ConfigurationProperties 바인딩용 기본 생성자
    }

    /**
     * 시간 윈도우(초)를 반환합니다.
     *
     * @return 시간 윈도우 (초)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long getTimeWindowSeconds() {
        return this.timeWindowSeconds;
    }

    /**
     * 시간 윈도우(초)를 설정합니다.
     *
     * @param timeWindowSeconds 시간 윈도우 (초)
     * @throws IllegalArgumentException timeWindowSeconds가 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setTimeWindowSeconds(final long timeWindowSeconds) {
        if (timeWindowSeconds <= 0) {
            throw new IllegalArgumentException("Time window must be positive: " + timeWindowSeconds);
        }
        this.timeWindowSeconds = timeWindowSeconds;
    }

    /**
     * IP 기반 제한 횟수를 반환합니다.
     *
     * @return IP 기반 제한 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getIpBasedLimit() {
        return this.ipBasedLimit;
    }

    /**
     * IP 기반 제한 횟수를 설정합니다.
     *
     * @param ipBasedLimit IP 기반 제한 횟수
     * @throws IllegalArgumentException ipBasedLimit이 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setIpBasedLimit(final int ipBasedLimit) {
        if (ipBasedLimit <= 0) {
            throw new IllegalArgumentException("IP based limit must be positive: " + ipBasedLimit);
        }
        this.ipBasedLimit = ipBasedLimit;
    }

    /**
     * 사용자 기반 제한 횟수를 반환합니다.
     *
     * @return 사용자 기반 제한 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getUserBasedLimit() {
        return this.userBasedLimit;
    }

    /**
     * 사용자 기반 제한 횟수를 설정합니다.
     *
     * @param userBasedLimit 사용자 기반 제한 횟수
     * @throws IllegalArgumentException userBasedLimit이 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setUserBasedLimit(final int userBasedLimit) {
        if (userBasedLimit <= 0) {
            throw new IllegalArgumentException("User based limit must be positive: " + userBasedLimit);
        }
        this.userBasedLimit = userBasedLimit;
    }

    /**
     * 엔드포인트 기반 제한 횟수를 반환합니다.
     *
     * @return 엔드포인트 기반 제한 횟수
     * @author AuthHub Team
     * @since 1.0.0
     */
    public int getEndpointBasedLimit() {
        return this.endpointBasedLimit;
    }

    /**
     * 엔드포인트 기반 제한 횟수를 설정합니다.
     *
     * @param endpointBasedLimit 엔드포인트 기반 제한 횟수
     * @throws IllegalArgumentException endpointBasedLimit이 0 이하인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public void setEndpointBasedLimit(final int endpointBasedLimit) {
        if (endpointBasedLimit <= 0) {
            throw new IllegalArgumentException("Endpoint based limit must be positive: " + endpointBasedLimit);
        }
        this.endpointBasedLimit = endpointBasedLimit;
    }

    /**
     * 두 RateLimitProperties 객체의 동등성을 비교합니다.
     *
     * @param obj 비교 대상 객체
     * @return 모든 필드가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RateLimitProperties other = (RateLimitProperties) obj;
        return this.timeWindowSeconds == other.timeWindowSeconds &&
                this.ipBasedLimit == other.ipBasedLimit &&
                this.userBasedLimit == other.userBasedLimit &&
                this.endpointBasedLimit == other.endpointBasedLimit;
    }

    /**
     * 해시 코드를 반환합니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                this.timeWindowSeconds,
                this.ipBasedLimit,
                this.userBasedLimit,
                this.endpointBasedLimit
        );
    }

    /**
     * RateLimitProperties의 문자열 표현을 반환합니다.
     *
     * @return "RateLimitProperties{timeWindowSeconds=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "RateLimitProperties{" +
                "timeWindowSeconds=" + this.timeWindowSeconds +
                ", ipBasedLimit=" + this.ipBasedLimit +
                ", userBasedLimit=" + this.userBasedLimit +
                ", endpointBasedLimit=" + this.endpointBasedLimit +
                '}';
    }
}
