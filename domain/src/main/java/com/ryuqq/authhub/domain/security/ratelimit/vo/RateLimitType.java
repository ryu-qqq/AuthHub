package com.ryuqq.authhub.domain.security.ratelimit.vo;

/**
 * Rate Limit 규칙의 타입을 정의하는 Enum.
 *
 * <p>Rate Limiting을 적용하는 기준을 나타냅니다:</p>
 * <ul>
 *   <li><strong>IP_BASED</strong>: IP 주소 기반 Rate Limiting (익명 사용자 보호)</li>
 *   <li><strong>USER_BASED</strong>: 인증된 사용자 기반 Rate Limiting (사용자별 할당량)</li>
 *   <li><strong>ENDPOINT_BASED</strong>: API 엔드포인트 기반 Rate Limiting (API별 제한)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Plain Java Enum 사용</li>
 *   <li>✅ 불변성 보장 - Enum의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum RateLimitType {

    /**
     * IP 주소 기반 Rate Limiting.
     * 익명 사용자나 인증되지 않은 요청에 대한 제한에 사용됩니다.
     */
    IP_BASED("IP-based Rate Limiting", "IP 주소 단위로 요청 횟수를 제한합니다"),

    /**
     * 인증된 사용자 기반 Rate Limiting.
     * 사용자별로 차등화된 할당량을 적용할 때 사용됩니다.
     */
    USER_BASED("User-based Rate Limiting", "사용자 단위로 요청 횟수를 제한합니다"),

    /**
     * API 엔드포인트 기반 Rate Limiting.
     * 특정 API의 부하를 제한하고 리소스를 보호할 때 사용됩니다.
     */
    ENDPOINT_BASED("Endpoint-based Rate Limiting", "API 엔드포인트 단위로 요청 횟수를 제한합니다");

    private final String displayName;
    private final String description;

    /**
     * RateLimitType Enum 생성자.
     *
     * @param displayName 타입의 표시 이름
     * @param description 타입의 설명
     */
    RateLimitType(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 타입의 표시 이름을 반환합니다.
     *
     * @return 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 타입의 설명을 반환합니다.
     *
     * @return 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * IP 기반 타입인지 확인합니다.
     *
     * @return IP 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isIpBased() {
        return this == IP_BASED;
    }

    /**
     * 사용자 기반 타입인지 확인합니다.
     *
     * @return 사용자 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUserBased() {
        return this == USER_BASED;
    }

    /**
     * 엔드포인트 기반 타입인지 확인합니다.
     *
     * @return 엔드포인트 기반이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isEndpointBased() {
        return this == ENDPOINT_BASED;
    }
}
