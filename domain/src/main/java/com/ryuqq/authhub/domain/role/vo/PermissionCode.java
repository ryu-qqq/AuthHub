package com.ryuqq.authhub.domain.role.vo;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * PermissionCode - Permission 코드 Value Object
 *
 * <p>권한 코드는 리소스:액션 형식을 따릅니다.
 *
 * <p><strong>형식:</strong>
 *
 * <ul>
 *   <li>리소스:액션 형태 (예: user:read, user:write)
 *   <li>영문 소문자, 숫자, 콜론, 언더스코어만 허용
 *   <li>와일드카드(*) 지원 (예: user:*, *:read)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionCode {

    private static final Pattern PERMISSION_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_]*:[a-z*][a-z0-9_*]*$");

    private final String value;

    private PermissionCode(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PermissionCode는 null이거나 빈 문자열일 수 없습니다");
        }
        String normalizedValue = value.toLowerCase(Locale.ROOT);
        if (!PERMISSION_PATTERN.matcher(normalizedValue).matches()) {
            throw new IllegalArgumentException(
                    "PermissionCode는 'resource:action' 형식이어야 합니다: " + value);
        }
        this.value = normalizedValue;
    }

    public static PermissionCode of(String value) {
        return new PermissionCode(value);
    }

    /**
     * 리소스와 액션을 조합하여 생성
     *
     * @param resource 리소스 (예: user, organization)
     * @param action 액션 (예: read, write, delete)
     * @return PermissionCode 인스턴스
     */
    public static PermissionCode of(String resource, String action) {
        return new PermissionCode(resource + ":" + action);
    }

    public String value() {
        return value;
    }

    /**
     * 리소스 부분 반환
     *
     * @return 리소스 (예: user)
     */
    public String resource() {
        return value.split(":")[0];
    }

    /**
     * 액션 부분 반환
     *
     * @return 액션 (예: read)
     */
    public String action() {
        return value.split(":")[1];
    }

    /**
     * 주어진 권한이 이 권한에 포함되는지 확인 (와일드카드 지원)
     *
     * @param other 확인할 권한 코드
     * @return 포함 여부
     */
    public boolean implies(PermissionCode other) {
        if (this.equals(other)) {
            return true;
        }

        String thisResource = this.resource();
        String thisAction = this.action();
        String otherResource = other.resource();
        String otherAction = other.action();

        boolean resourceMatches = "*".equals(thisResource) || thisResource.equals(otherResource);
        boolean actionMatches = "*".equals(thisAction) || thisAction.equals(otherAction);

        return resourceMatches && actionMatches;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionCode that = (PermissionCode) o;
        return Objects.equals(value, that.value);
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
