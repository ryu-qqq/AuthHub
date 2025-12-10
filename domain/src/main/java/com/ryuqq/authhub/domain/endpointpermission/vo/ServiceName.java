package com.ryuqq.authhub.domain.endpointpermission.vo;

import java.util.regex.Pattern;

/**
 * ServiceName Value Object - 서비스 이름
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>영문 소문자, 숫자, 하이픈만 허용
 *   <li>첫 글자는 영문 소문자로 시작
 *   <li>최소 2자, 최대 50자
 * </ul>
 *
 * <p><strong>예시</strong>:
 *
 * <ul>
 *   <li>auth-hub
 *   <li>user-service
 *   <li>order-api
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record ServiceName(String value) {

    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("^[a-z][a-z0-9-]{1,49}$");
    private static final int MAX_LENGTH = 50;
    private static final int MIN_LENGTH = 2;

    /** Compact Constructor (검증 로직) */
    public ServiceName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ServiceName은 null이거나 빈 문자열일 수 없습니다");
        }

        value = value.trim().toLowerCase();

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "ServiceName은 최소 " + MIN_LENGTH + "자 이상이어야 합니다: " + value);
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "ServiceName은 최대 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + value);
        }

        if (!SERVICE_NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "ServiceName은 영문 소문자로 시작하고, 영문 소문자, 숫자, 하이픈만 포함해야 합니다: " + value);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 서비스 이름
     * @return ServiceName
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static ServiceName of(String value) {
        return new ServiceName(value);
    }
}
