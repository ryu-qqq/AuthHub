package com.ryuqq.authhub.domain.role.vo;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * RoleName - Role 이름 Value Object
 *
 * <p>Spring Security 호환 형식 (ROLE_ prefix)을 강제합니다.
 *
 * <p><strong>형식:</strong>
 *
 * <ul>
 *   <li>ROLE_로 시작해야 함
 *   <li>영문 대문자와 언더스코어만 허용
 *   <li>예: ROLE_USER, ROLE_ADMIN, ROLE_SUPER_ADMIN
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleName {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final Pattern ROLE_PATTERN = Pattern.compile("^ROLE_[A-Z][A-Z_]*$");

    private final String value;

    private RoleName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("RoleName은 null이거나 빈 문자열일 수 없습니다");
        }
        if (!ROLE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "RoleName은 ROLE_로 시작하고 영문 대문자와 언더스코어만 포함해야 합니다: " + value);
        }
        this.value = value;
    }

    /**
     * ROLE_ prefix가 포함된 전체 이름으로 생성
     *
     * @param fullName ROLE_로 시작하는 전체 이름 (예: ROLE_ADMIN)
     * @return RoleName 인스턴스
     */
    public static RoleName of(String fullName) {
        return new RoleName(fullName);
    }

    /**
     * ROLE_ prefix 없이 이름만으로 생성
     *
     * @param name prefix 없는 이름 (예: ADMIN)
     * @return RoleName 인스턴스
     */
    public static RoleName withPrefix(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role name은 null이거나 빈 문자열일 수 없습니다");
        }
        return new RoleName(ROLE_PREFIX + name.toUpperCase(Locale.ROOT));
    }

    /**
     * 전체 Role 이름 반환 (ROLE_ prefix 포함)
     *
     * @return ROLE_로 시작하는 전체 이름
     */
    public String value() {
        return value;
    }

    /**
     * ROLE_ prefix 제외한 이름만 반환
     *
     * @return prefix 없는 이름
     */
    public String nameWithoutPrefix() {
        return value.substring(ROLE_PREFIX.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleName roleName = (RoleName) o;
        return Objects.equals(value, roleName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
