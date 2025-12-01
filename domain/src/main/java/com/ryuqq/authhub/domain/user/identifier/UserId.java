package com.ryuqq.authhub.domain.user.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * UserId - User 식별자 Value Object
 *
 * <p>UUID 기반 식별자를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserId {

    private final UUID value;

    UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
        this.value = value;
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId forNew() {
        return new UserId(UUID.randomUUID());
    }

    public UUID value() {
        return value;
    }

    public boolean isNew() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{value=" + value + "}";
    }
}
