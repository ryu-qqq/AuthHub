package com.ryuqq.authhub.domain.endpointpermission.vo;

/**
 * EndpointDescription Value Object - 엔드포인트 설명
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>최대 500자
 *   <li>null 또는 빈 문자열 허용 (선택 필드)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record EndpointDescription(String value) {

    private static final int MAX_LENGTH = 500;

    /** Compact Constructor (검증 로직) */
    public EndpointDescription {
        if (value != null) {
            value = value.trim();
            if (value.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "EndpointDescription은 최대 "
                                + MAX_LENGTH
                                + "자를 초과할 수 없습니다: "
                                + value.length());
            }
            if (value.isEmpty()) {
                value = null;
            }
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 설명 (null 허용)
     * @return EndpointDescription
     * @throws IllegalArgumentException 최대 길이 초과 시
     */
    public static EndpointDescription of(String value) {
        return new EndpointDescription(value);
    }

    /**
     * 빈 설명 생성
     *
     * @return 빈 EndpointDescription
     */
    public static EndpointDescription empty() {
        return new EndpointDescription(null);
    }

    /**
     * 설명이 있는지 확인
     *
     * @return 설명이 있으면 true
     */
    public boolean hasValue() {
        return value != null && !value.isEmpty();
    }
}
