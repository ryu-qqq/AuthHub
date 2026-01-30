package com.ryuqq.authhub.domain.role.vo;

/**
 * RoleName - 역할 이름 Value Object
 *
 * <p>역할의 고유한 이름을 나타내는 VO입니다.
 *
 * <p><strong>명명 규칙:</strong>
 *
 * <ul>
 *   <li>영문 대문자, 숫자, 언더스코어만 허용
 *   <li>예: SUPER_ADMIN, TENANT_ADMIN, USER_MANAGER
 * </ul>
 *
 * @param value 역할 이름 값
 * @author development-team
 * @since 1.0.0
 */
public record RoleName(String value) {

    private static final String NAME_PATTERN = "^[A-Z][A-Z0-9_]*$";

    /** Compact Constructor - 유효성 검증 */
    public RoleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RoleName은 null이거나 빈 값일 수 없습니다");
        }
        if (!value.matches(NAME_PATTERN)) {
            throw new IllegalArgumentException(
                    "RoleName은 영문 대문자로 시작하고 대문자, 숫자, 언더스코어만 포함해야 합니다: " + value);
        }
    }

    /**
     * RoleName 생성
     *
     * @param value 역할 이름
     * @return RoleName 인스턴스
     */
    public static RoleName of(String value) {
        return new RoleName(value);
    }

    /**
     * 문자열로부터 RoleName 파싱 (nullable)
     *
     * <p>null이거나 빈 문자열이면 null을 반환합니다.
     *
     * @param value 역할 이름 문자열 (nullable)
     * @return RoleName 또는 null
     */
    public static RoleName fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return new RoleName(value);
    }
}
