package com.ryuqq.authhub.domain.permissionendpoint.vo;

import java.util.regex.Pattern;

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

    /** Regex 메타문자 escape 패턴 (URL에서 사용될 수 있는 문자들) */
    private static final Pattern REGEX_META_CHARS = Pattern.compile("([.+?^$|()\\[\\]\\\\])");

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
     * <p>Path Variable({id})을 정규식으로 변환하여 매칭합니다. regex 메타문자를 안전하게 escape하여 정확한 매칭을 보장합니다.
     *
     * @param requestUrl 요청 URL
     * @return 매칭되면 true
     */
    public boolean matches(String requestUrl) {
        // 1. Path Variable({id}) → [^/]+ 변환
        String withPathVariables = value.replaceAll("\\{[^}]+\\}", "[^/]+");

        // 2. ** 와일드카드를 플레이스홀더로 임시 대체 (단일 * 처리 전에)
        String withDoubleWildcardPlaceholder = withPathVariables.replace("**", "\0");

        // 3. 단일 * 와일드카드 → [^/]* 변환
        String withSingleWildcard = withDoubleWildcardPlaceholder.replace("*", "[^/]*");

        // 4. ** 플레이스홀더 → .* 변환
        String withAllWildcards = withSingleWildcard.replace("\0", ".*");

        // 5. URL에서 사용될 수 있는 regex 메타문자 escape (., +, ? 등)
        //    단, 이미 변환된 regex 패턴([^/]+, [^/]*, .*)은 보존해야 함
        //    → 변환 전에 메타문자를 escape하는 것이 아니라, 원본 패턴에서 escape 필요 부분만 처리
        String regex = escapeRegexMetaCharsInLiterals(withAllWildcards);

        return requestUrl.matches("^" + regex + "$");
    }

    /**
     * 이미 변환된 regex 패턴을 보존하면서 리터럴 부분의 메타문자만 escape
     *
     * @param pattern 부분적으로 변환된 패턴
     * @return escape된 패턴
     */
    private String escapeRegexMetaCharsInLiterals(String pattern) {
        // 현재 패턴에서 이미 변환된 regex 구문:
        // - [^/]+ : path variable 또는 단일 와일드카드
        // - [^/]* : 단일 와일드카드
        // - .* : 더블 와일드카드
        // 이들을 제외한 나머지에서 . 만 escape하면 됨 (URL에서 가장 흔한 특수문자)
        // 예: /api.v1/users → /api\.v1/users (. 을 리터럴로)

        // 간단한 접근: . 이 regex 패턴의 일부(.*)가 아닌 경우에만 escape
        // .* 패턴 보존을 위해 먼저 플레이스홀더로 대체
        String preserved = pattern.replace(".*", "\u0001DOTSTAR\u0001");
        String escaped = preserved.replace(".", "\\.");
        return escaped.replace("\u0001DOTSTAR\u0001", ".*");
    }
}
