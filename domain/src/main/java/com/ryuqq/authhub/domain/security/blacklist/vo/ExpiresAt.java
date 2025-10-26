package com.ryuqq.authhub.domain.security.blacklist.vo;

import java.time.Instant;

/**
 * 토큰의 만료 시간을 나타내는 Value Object.
 * JWT의 exp (Expiration Time) Claim에 대응하며, 블랙리스트 토큰의 자동 정리 기준 시간입니다.
 *
 * <p>만료 시간은 블랙리스트 토큰의 생명주기를 관리하는 핵심 속성입니다.
 * 만료된 토큰은 더 이상 블랙리스트에서 관리할 필요가 없으므로 자동으로 정리됩니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>토큰의 만료 시간 보유</li>
 *   <li>만료 여부 판단 (현재 시간과 비교)</li>
 *   <li>특정 시간과의 선후 관계 비교</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * <p><strong>검증 규칙:</strong></p>
 * <ul>
 *   <li>null 불가</li>
 *   <li>과거 시간 허용 (이미 만료된 토큰도 블랙리스트에 등록 가능)</li>
 *   <li>미래 시간 허용 (아직 만료되지 않은 토큰 블랙리스트 등록)</li>
 * </ul>
 *
 * @param value 만료 시간 (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record ExpiresAt(Instant value) {

    /**
     * Compact constructor - 만료 시간의 null 검증을 수행합니다.
     *
     * @throws IllegalArgumentException value가 null인 경우
     */
    public ExpiresAt {
        if (value == null) {
            throw new IllegalArgumentException("ExpiresAt value cannot be null");
        }
    }

    /**
     * Instant 값으로부터 ExpiresAt을 생성합니다.
     *
     * @param value 만료 시간 (null 불가)
     * @return ExpiresAt 인스턴스
     * @throws IllegalArgumentException value가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt of(final Instant value) {
        return new ExpiresAt(value);
    }

    /**
     * epoch seconds로부터 ExpiresAt을 생성합니다.
     * JWT의 exp claim은 일반적으로 epoch seconds로 표현됩니다.
     *
     * @param epochSeconds epoch seconds (1970-01-01T00:00:00Z 이후의 초)
     * @return ExpiresAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ExpiresAt fromEpochSeconds(final long epochSeconds) {
        return new ExpiresAt(Instant.ofEpochSecond(epochSeconds));
    }

    /**
     * 현재 시간 기준으로 만료되었는지 확인합니다.
     *
     * @return 만료되었으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isExpired() {
        return this.value.isBefore(Instant.now());
    }

    /**
     * 특정 시간 이전인지 확인합니다.
     *
     * @param other 비교할 시간
     * @return 이전이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isBefore(final Instant other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison time cannot be null");
        }
        return this.value.isBefore(other);
    }

    /**
     * 특정 시간 이후인지 확인합니다.
     *
     * @param other 비교할 시간
     * @return 이후면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAfter(final Instant other) {
        if (other == null) {
            throw new IllegalArgumentException("Comparison time cannot be null");
        }
        return this.value.isAfter(other);
    }

    /**
     * epoch seconds로 변환합니다.
     *
     * @return epoch seconds
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long toEpochSeconds() {
        return this.value.getEpochSecond();
    }

    /**
     * Instant 값을 반환합니다.
     *
     * @return Instant 값
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant asInstant() {
        return this.value;
    }
}
