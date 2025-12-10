package com.ryuqq.authhub.domain.user.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * UserId - 사용자 식별자 Value Object
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserId {

    private final UUID value;

    private UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("UserId value cannot be null");
        }
        this.value = value;
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /**
     * 새로운 UserId 생성 (Application Layer에서 UUID 생성 후 전달)
     *
     * @param uuid Application Layer에서 생성된 UUIDv7
     * @return 새로운 UserId 인스턴스
     */
    public static UserId forNew(UUID uuid) {
        return new UserId(uuid);
    }

    public UUID value() {
        return value;
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
