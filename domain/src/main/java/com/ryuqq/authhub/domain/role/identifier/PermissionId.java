package com.ryuqq.authhub.domain.role.identifier;

import java.util.Objects;

/**
 * PermissionId - Permission 식별자 Value Object
 *
 * <p>Long FK 전략에 따라 Long 타입 ID를 사용합니다. 새로 생성된 Permission은 forNew()로 생성하여 null ID를 가질 수 있습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionId {

    private final Long value;

    private PermissionId(Long value, boolean isNew) {
        if (!isNew) {
            if (value == null) {
                throw new IllegalArgumentException("PermissionId는 null일 수 없습니다");
            }
            if (value <= 0) {
                throw new IllegalArgumentException("PermissionId는 양수여야 합니다");
            }
        }
        this.value = value;
    }

    public static PermissionId of(Long value) {
        return new PermissionId(value, false);
    }

    public static PermissionId forNew() {
        return new PermissionId(null, true);
    }

    public Long value() {
        return value;
    }

    public boolean isNew() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PermissionId that = (PermissionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PermissionId{value=" + value + "}";
    }
}
