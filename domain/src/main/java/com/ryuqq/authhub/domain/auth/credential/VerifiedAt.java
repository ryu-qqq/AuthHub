package com.ryuqq.authhub.domain.auth.credential;

import java.time.Instant;
import java.util.Optional;

/**
 * 인증 정보의 검증 완료 시각을 나타내는 Value Object.
 *
 * <p>이 Record는 사용자의 인증 정보(이메일, 전화번호)가 검증된 시각을 추적하며,
 * null 값을 허용하여 아직 검증되지 않은 상태를 표현할 수 있습니다.</p>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>이메일 인증 완료 시각 기록</li>
 *   <li>전화번호 SMS 인증 완료 시각 기록</li>
 *   <li>미검증 상태 표현 (null value)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수 - Optional로 null 안전성 제공</li>
 *   <li>✅ Framework 의존성 금지 - Pure Java만 사용</li>
 * </ul>
 *
 * @param value Instant 값 (null 허용 - 아직 검증되지 않은 경우)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record VerifiedAt(Instant value) {

    /**
     * 현재 시각으로 VerifiedAt을 생성합니다.
     * 인증 정보를 검증 완료 처리할 때 사용합니다.
     *
     * @return 현재 시각을 담은 VerifiedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static VerifiedAt now() {
        return new VerifiedAt(Instant.now());
    }

    /**
     * 특정 Instant로 VerifiedAt을 생성합니다.
     *
     * @param instant Instant 객체 (null 허용)
     * @return VerifiedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static VerifiedAt of(final Instant instant) {
        return new VerifiedAt(instant);
    }

    /**
     * 아직 검증되지 않은 상태를 나타내는 VerifiedAt을 생성합니다.
     *
     * @return null 값을 담은 VerifiedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static VerifiedAt notVerified() {
        return new VerifiedAt(null);
    }

    /**
     * epoch milliseconds로부터 VerifiedAt을 생성합니다.
     *
     * @param epochMilli epoch milliseconds
     * @return VerifiedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static VerifiedAt fromEpochMilli(final long epochMilli) {
        return new VerifiedAt(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * ISO-8601 형식의 문자열로부터 VerifiedAt을 생성합니다.
     *
     * @param isoString ISO-8601 형식 문자열 (예: "2024-01-01T00:00:00Z")
     * @return VerifiedAt 인스턴스
     * @throws IllegalArgumentException isoString이 null이거나 유효하지 않은 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static VerifiedAt fromString(final String isoString) {
        if (isoString == null || isoString.trim().isEmpty()) {
            throw new IllegalArgumentException("ISO string cannot be null or empty");
        }
        try {
            return new VerifiedAt(Instant.parse(isoString));
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid ISO-8601 format: " + isoString, e);
        }
    }

    /**
     * VerifiedAt의 Instant 값을 Optional로 반환합니다.
     * null 안전성을 제공하기 위해 Optional을 사용합니다.
     *
     * @return Instant를 담은 Optional (값이 없으면 Optional.empty())
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Optional<Instant> getValue() {
        return Optional.ofNullable(value);
    }

    /**
     * 아직 검증되지 않은 상태인지 확인합니다.
     *
     * @return 검증 기록이 없으면 true, 있으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isNotVerified() {
        return value == null;
    }

    /**
     * 검증이 완료된 상태인지 확인합니다.
     *
     * @return 검증 기록이 있으면 true, 없으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isVerified() {
        return value != null;
    }

    /**
     * 특정 시점 이후에 검증되었는지 확인합니다.
     *
     * @param other 비교 대상 시각 (null 불가)
     * @return 검증 기록이 있고, other 이후에 검증되었으면 true, 아니면 false
     * @throws NullPointerException other가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAfter(final Instant other) {
        java.util.Objects.requireNonNull(other, "other instant cannot be null");
        if (value == null) {
            return false;
        }
        return value.isAfter(other);
    }

    /**
     * 특정 시점 이전에 검증되었는지 확인합니다.
     *
     * @param other 비교 대상 시각 (null 불가)
     * @return 검증 기록이 있고, other 이전에 검증되었으면 true, 아니면 false
     * @throws NullPointerException other가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isBefore(final Instant other) {
        java.util.Objects.requireNonNull(other, "other instant cannot be null");
        if (value == null) {
            return false;
        }
        return value.isBefore(other);
    }

    /**
     * epoch milliseconds 값을 반환합니다.
     *
     * @return epoch milliseconds
     * @throws IllegalStateException 검증 기록이 없는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public long toEpochMilli() {
        return getValue()
                .map(Instant::toEpochMilli)
                .orElseThrow(() -> new IllegalStateException("Cannot get epochMilli for credential that has not been verified."));
    }

    /**
     * ISO-8601 형식의 문자열로 변환합니다.
     *
     * @return ISO-8601 문자열 (검증 기록이 없으면 "not_verified")
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String toIsoString() {
        return value != null ? value.toString() : "not_verified";
    }

    /**
     * VerifiedAt의 문자열 표현을 반환합니다.
     * Record의 기본 toString을 오버라이드하여 가독성을 향상시킵니다.
     *
     * @return "VerifiedAt{value=...}" 또는 "VerifiedAt{not_verified}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "VerifiedAt{value=" + (value != null ? value : "not_verified") + '}';
    }
}
