package com.ryuqq.authhub.domain.endpointpermission.vo;

import java.util.regex.Pattern;

/**
 * EndpointPath Value Object - API 엔드포인트 경로
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>/로 시작해야 함
 *   <li>알파벳, 숫자, 슬래시(/), 하이픈(-), 언더스코어(_), 중괄호({}) 허용
 *   <li>Path Variable 지원: /api/users/{userId}
 *   <li>최대 500자
 * </ul>
 *
 * <p><strong>예시</strong>:
 *
 * <ul>
 *   <li>/api/v1/users
 *   <li>/api/v1/users/{userId}
 *   <li>/api/v1/orders/{orderId}/items
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPath(String value) {

    private static final Pattern PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9/_{}*-]*$");
    private static final int MAX_LENGTH = 500;

    /** Compact Constructor (검증 로직) */
    public EndpointPath {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("EndpointPath는 null이거나 빈 문자열일 수 없습니다");
        }

        value = value.trim();

        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("EndpointPath는 /로 시작해야 합니다: " + value);
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "EndpointPath는 최대 " + MAX_LENGTH + "자를 초과할 수 없습니다: " + value.length());
        }

        if (!PATH_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("EndpointPath 형식이 올바르지 않습니다: " + value);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param value 엔드포인트 경로
     * @return EndpointPath
     * @throws IllegalArgumentException 검증 실패 시
     */
    public static EndpointPath of(String value) {
        return new EndpointPath(value);
    }

    /**
     * Path Variable 포함 여부 확인
     *
     * @return Path Variable이 있으면 true
     */
    public boolean hasPathVariable() {
        return value.contains("{") && value.contains("}");
    }

    /**
     * Ant 스타일 패턴 매칭 여부 확인
     *
     * @return 와일드카드(*)를 포함하면 true
     */
    public boolean hasWildcard() {
        return value.contains("*");
    }
}
