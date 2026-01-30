package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * UserSortKey - 사용자 정렬 키 Value Object
 *
 * <p>사용자 목록 조회 시 정렬 기준을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserSortKey implements SortKey {

    /** 생성일시 기준 정렬 */
    CREATED_AT("createdAt"),

    /** 수정일시 기준 정렬 */
    UPDATED_AT("updatedAt");

    private final String fieldName;

    UserSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }
}
