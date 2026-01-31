package com.ryuqq.authhub.domain.userrole.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * UserRoleSortKey - 사용자-역할 정렬 키 Value Object
 *
 * <p>사용자-역할 관계 목록 조회 시 정렬 기준을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserRoleSortKey implements SortKey {

    /** 생성일시 기준 정렬 */
    CREATED_AT("createdAt");

    private final String fieldName;

    UserRoleSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }
}
