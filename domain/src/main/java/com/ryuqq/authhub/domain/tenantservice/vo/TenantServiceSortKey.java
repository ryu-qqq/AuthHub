package com.ryuqq.authhub.domain.tenantservice.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * TenantServiceSortKey - 테넌트-서비스 정렬 기준 Value Object
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantServiceSortKey implements SortKey {
    SUBSCRIBED_AT("subscribedAt"),
    CREATED_AT("createdAt");

    private final String fieldName;

    TenantServiceSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    public String fieldName() {
        return fieldName;
    }
}
