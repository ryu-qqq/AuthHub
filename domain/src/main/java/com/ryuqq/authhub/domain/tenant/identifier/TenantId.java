package com.ryuqq.authhub.domain.tenant.identifier;

import java.util.Objects;

/**
 * TenantId - Tenant 식별자 Value Object
 *
 * <p>Long FK 전략에 따라 Long 타입 ID를 사용합니다.
 * 새로 생성된 Tenant는 forNew()로 생성하여 null ID를 가질 수 있습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantId {

    private final Long value;

    private TenantId(Long value, boolean isNew) {
        if (!isNew) {
            if (value == null) {
                throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
            }
            if (value <= 0) {
                throw new IllegalArgumentException("TenantId는 양수여야 합니다");
            }
        }
        this.value = value;
    }

    public static TenantId of(Long value) {
        return new TenantId(value, false);
    }

    public static TenantId forNew() {
        return new TenantId(null, true);
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
        TenantId tenantId = (TenantId) o;
        return Objects.equals(value, tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TenantId{value=" + value + "}";
    }
}
