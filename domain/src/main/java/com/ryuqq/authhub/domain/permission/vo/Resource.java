package com.ryuqq.authhub.domain.permission.vo;

/**
 * Resource - 권한 리소스 Value Object
 *
 * <p>권한이 적용되는 대상 리소스를 나타내는 불변 값 객체입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>null이거나 빈 값 불가
 *   <li>최대 50자
 *   <li>영문 소문자, 숫자, 하이픈만 허용 (예: user, role, organization)
 * </ul>
 *
 * @param value 리소스 문자열
 * @author development-team
 * @since 1.0.0
 */
public record Resource(String value) {

    private static final int MAX_LENGTH = 50;
    private static final String PATTERN = "^[a-z][a-z0-9-]*$";

    public Resource {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("resource는 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("resource는 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "resource는 영문 소문자로 시작하고, 영문 소문자, 숫자, 하이픈만 허용됩니다: " + value);
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value 리소스 문자열
     * @return Resource 인스턴스
     */
    public static Resource of(String value) {
        return new Resource(value);
    }
}
