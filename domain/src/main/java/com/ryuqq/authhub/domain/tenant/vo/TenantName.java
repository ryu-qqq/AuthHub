package com.ryuqq.authhub.domain.tenant.vo;

/** TenantName Value Object Tenant의 이름 (2-100자) */
public record TenantName(String value) {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    public static TenantName of(String value) {
        validate(value);
        return new TenantName(value);
    }

    private static void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("TenantName은 null일 수 없습니다");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("TenantName은 2자 이상 100자 이하여야 합니다");
        }
    }
}
