package com.ryuqq.authhub.domain.identity.profile.vo;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 사용자 프로필 이미지 URL을 나타내는 Value Object.
 * 유효한 URL 형식을 보장하며, 기본 프로필 이미지를 지원합니다.
 *
 * <p>이 Record는 불변(immutable) 객체이며, 동등성 비교는 내부 value 값으로 수행됩니다.</p>
 *
 * <p><strong>Validation 규칙:</strong></p>
 * <ul>
 *   <li>유효한 URL 형식이어야 함</li>
 *   <li>HTTP 또는 HTTPS 프로토콜만 허용</li>
 *   <li>null 허용 (기본 프로필 이미지 사용)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>불변성 보장 - Record의 본질적 불변성</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @param value 프로필 이미지 URL 문자열 (null 허용, 유효한 URL 형식이어야 함)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record ProfileImageUrl(String value) {

    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://cdn.authhub.com/default-profile.png";

    /**
     * Compact constructor - URL 값의 유효성 검증을 수행합니다.
     * null인 경우 기본 프로필 이미지 URL을 할당하지 않고 null로 유지합니다.
     *
     * @throws IllegalArgumentException value가 유효한 URL 형식이 아니거나 HTTP/HTTPS가 아닌 경우
     */
    public ProfileImageUrl {
        if (value != null && !value.trim().isEmpty()) {
            validateUrlFormat(value);
            validateProtocol(value);
        }
    }

    /**
     * URL 형식을 검증합니다.
     *
     * @param value 검증 대상 URL 문자열
     * @throws IllegalArgumentException 유효한 URL 형식이 아닌 경우
     */
    private void validateUrlFormat(final String value) {
        try {
            new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + value, e);
        }
    }

    /**
     * HTTP 또는 HTTPS 프로토콜인지 검증합니다.
     *
     * @param value 검증 대상 URL 문자열
     * @throws IllegalArgumentException HTTP/HTTPS가 아닌 경우
     */
    private void validateProtocol(final String value) {
        final String lowerCaseValue = value.toLowerCase();
        if (!lowerCaseValue.startsWith("http://") && !lowerCaseValue.startsWith("https://")) {
            throw new IllegalArgumentException("Profile image URL must use HTTP or HTTPS protocol: " + value);
        }
    }

    /**
     * 기본 프로필 이미지 URL을 가진 ProfileImageUrl을 생성합니다.
     *
     * @return 기본 프로필 이미지 URL을 가진 ProfileImageUrl 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static ProfileImageUrl defaultImage() {
        return new ProfileImageUrl(DEFAULT_PROFILE_IMAGE_URL);
    }

    /**
     * 프로필 이미지가 기본 이미지인지 확인합니다.
     *
     * @return 기본 이미지이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDefault() {
        return DEFAULT_PROFILE_IMAGE_URL.equals(value);
    }

    /**
     * 프로필 이미지 URL이 설정되어 있는지 확인합니다.
     *
     * @return URL이 null이 아니고 비어있지 않으면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean hasValue() {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * 프로필 이미지 URL 문자열을 반환합니다 (Law of Demeter 준수).
     * null인 경우 기본 프로필 이미지 URL을 반환합니다.
     *
     * @return 프로필 이미지 URL 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getValue() {
        return hasValue() ? value : DEFAULT_PROFILE_IMAGE_URL;
    }

    /**
     * 주어진 문자열이 유효한 프로필 이미지 URL 형식인지 검증합니다 (정적 헬퍼 메서드).
     *
     * @param value 검증 대상 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static boolean isValid(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return true; // null은 기본 이미지로 처리
        }
        try {
            final URL url = new URL(value);
            final String protocol = url.getProtocol().toLowerCase();
            return "http".equals(protocol) || "https".equals(protocol);
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
