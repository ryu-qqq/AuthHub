package com.ryuqq.authhub.domain.permissionendpoint.vo;

/**
 * UrlPattern - URL 패턴 Value Object
 *
 * <p>Gateway에서 사용하는 URL 패턴을 나타내는 불변 값 객체입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>null이거나 빈 값 불가
 *   <li>'/'로 시작해야 함
 *   <li>최대 500자
 * </ul>
 *
 * <p><strong>패턴 예시:</strong>
 *
 * <ul>
 *   <li>/api/v1/users - 사용자 목록
 *   <li>/api/v1/users/{id} - Path Variable
 *   <li>/api/v1/organizations/{orgId}/members - 중첩 리소스
 * </ul>
 *
 * @param value URL 패턴 문자열
 * @author development-team
 * @since 1.0.0
 */
public record UrlPattern(String value) {

    private static final int MAX_LENGTH = 500;

    public UrlPattern {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("urlPattern은 null이거나 빈 값일 수 없습니다");
        }
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("urlPattern은 '/'로 시작해야 합니다: " + value);
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("urlPattern은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value URL 패턴 문자열
     * @return UrlPattern 인스턴스
     */
    public static UrlPattern of(String value) {
        return new UrlPattern(value);
    }

    /**
     * 요청 URL이 이 패턴과 매칭되는지 확인
     *
     * <p>Path Variable({id})을 정규식으로 변환하여 매칭합니다.
     *
     * @param requestUrl 요청 URL
     * @return 매칭되면 true
     */
    public boolean matches(String requestUrl) {
        String withPathVariables = value.replaceAll("\\{[^}]+\\}", "[^/]+");
        String withDoubleWildcardPlaceholder = withPathVariables.replace("**", "\0");
        String withSingleWildcard = withDoubleWildcardPlaceholder.replace("*", "[^/]*");
        String regex = withSingleWildcard.replace("\0", ".*");
        return requestUrl.matches("^" + regex + "$");
    }
}
