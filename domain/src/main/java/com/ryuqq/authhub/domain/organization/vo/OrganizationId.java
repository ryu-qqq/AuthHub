package com.ryuqq.authhub.domain.organization.vo;

import com.ryuqq.authhub.domain.common.model.ValueObject;

/**
 * OrganizationId Value Object
 * Organization의 식별자
 */
public record OrganizationId(Long value) implements ValueObject {

    public static OrganizationId of(Long value) {
        validate(value);
        return new OrganizationId(value);
    }

    private static void validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("OrganizationId는 양수여야 합니다");
        }
    }
}
