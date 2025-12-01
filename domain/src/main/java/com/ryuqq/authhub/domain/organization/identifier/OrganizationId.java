package com.ryuqq.authhub.domain.organization.identifier;

import java.util.Objects;

/**
 * OrganizationId - Organization 식별자 Value Object
 *
 * <p>Long FK 전략에 따라 Long 타입 ID를 사용합니다.
 * 새로운 Organization 생성 시에는 forNew()를 사용하고,
 * 영속화된 Organization 로드 시에는 of()를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationId {

    private final Long value;
    private final boolean isNew;

    private OrganizationId(Long value, boolean isNew) {
        if (!isNew && value == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
        if (value != null && value <= 0) {
            throw new IllegalArgumentException("OrganizationId는 양수여야 합니다");
        }
        this.value = value;
        this.isNew = isNew;
    }

    public static OrganizationId of(Long value) {
        return new OrganizationId(value, false);
    }

    public static OrganizationId forNew() {
        return new OrganizationId(null, true);
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
        OrganizationId that = (OrganizationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OrganizationId{value=" + value + "}";
    }
}
