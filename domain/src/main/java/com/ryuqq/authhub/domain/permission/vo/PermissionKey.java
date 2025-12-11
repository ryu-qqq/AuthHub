package com.ryuqq.authhub.domain.permission.vo;

import java.util.Objects;

/**
 * PermissionKey - 권한 키 Value Object
 *
 * <p>Resource와 Action을 조합한 유니크 키입니다. "{resource}:{action}" 형식으로 표현됩니다.
 *
 * <p>예시: user:read, organization:create, tenant:manage
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionKey {

    private static final String SEPARATOR = ":";

    private final Resource resource;
    private final Action action;

    PermissionKey(Resource resource, Action action) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource는 null일 수 없습니다");
        }
        if (action == null) {
            throw new IllegalArgumentException("Action은 null일 수 없습니다");
        }
        this.resource = resource;
        this.action = action;
    }

    /**
     * Resource와 Action으로 PermissionKey 생성
     *
     * @param resource 리소스
     * @param action 행위
     * @return PermissionKey 인스턴스
     */
    public static PermissionKey of(Resource resource, Action action) {
        return new PermissionKey(resource, action);
    }

    /**
     * 문자열로부터 PermissionKey 생성 (예: "user:read")
     *
     * @param value "{resource}:{action}" 형식의 문자열
     * @return PermissionKey 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 형식인 경우
     */
    public static PermissionKey of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PermissionKey는 null이거나 빈 문자열일 수 없습니다");
        }
        String[] parts = value.split(SEPARATOR);
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "PermissionKey는 '{resource}:{action}' 형식이어야 합니다: " + value);
        }
        return new PermissionKey(Resource.of(parts[0]), Action.of(parts[1]));
    }

    public Resource resource() {
        return resource;
    }

    public Action action() {
        return action;
    }

    public String value() {
        return resource.value() + SEPARATOR + action.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionKey that = (PermissionKey) o;
        return Objects.equals(resource, that.resource) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, action);
    }

    @Override
    public String toString() {
        return "PermissionKey{value='" + value() + "'}";
    }
}
