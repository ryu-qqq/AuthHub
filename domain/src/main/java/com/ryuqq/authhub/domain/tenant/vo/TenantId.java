package com.ryuqq.authhub.domain.tenant.vo;

import com.ryuqq.authhub.domain.common.model.ValueObject;

/**
 * TenantId Value Object
 * Tenant의 식별자
 */
public record TenantId(Long value) implements ValueObject {

    public static TenantId of(Long value) {
        validate(value);
        return new TenantId(value);
    }

    private static void validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("TenantId는 양수여야 합니다");
        }
    }
}
