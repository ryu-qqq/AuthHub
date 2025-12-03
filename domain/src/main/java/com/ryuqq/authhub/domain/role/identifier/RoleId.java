package com.ryuqq.authhub.domain.role.identifier;

import java.util.Objects;

/**
 * RoleId - Role 식별자 Value Object
 *
 * <p>Long FK 전략에 따라 Long 타입 ID를 사용합니다. 새로 생성된 Role은 forNew()로 생성하여 null ID를 가질 수 있습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class RoleId {

    private final Long value;

    private RoleId(Long value, boolean isNew) {
        if (!isNew) {
            if (value == null) {
                throw new IllegalArgumentException("RoleId는 null일 수 없습니다");
            }
            if (value <= 0) {
                throw new IllegalArgumentException("RoleId는 양수여야 합니다");
            }
        }
        this.value = value;
    }

    public static RoleId of(Long value) {
        return new RoleId(value, false);
    }

    public static RoleId forNew() {
        return new RoleId(null, true);
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
        RoleId roleId = (RoleId) o;
        return Objects.equals(value, roleId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "RoleId{value=" + value + "}";
    }
}
