package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.common.vo.SearchField;

/**
 * UserSearchField - 사용자 검색 필드 Value Object
 *
 * <p>사용자 검색 시 검색 대상 필드를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserSearchField implements SearchField {

    /** 로그인 식별자 검색 */
    IDENTIFIER("identifier"),

    /** 전화번호 검색 */
    PHONE_NUMBER("phoneNumber");

    private final String fieldName;

    UserSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }
}
