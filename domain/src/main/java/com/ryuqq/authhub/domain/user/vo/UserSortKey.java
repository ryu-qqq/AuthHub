package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * UserSortKey - 사용자 정렬 키 Value Object
 *
 * <p>사용자 목록 조회 시 정렬 가능한 필드를 정의합니다.
 *
 * <p><strong>지원 정렬 필드:</strong>
 *
 * <ul>
 *   <li>{@link #IDENTIFIER}: 사용자 식별자
 *   <li>{@link #STATUS}: 사용자 상태
 *   <li>{@link #CREATED_AT}: 생성일시 (기본값)
 *   <li>{@link #UPDATED_AT}: 수정일시
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserSortKey implements SortKey {
    IDENTIFIER("identifier"),
    STATUS("status"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    UserSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 기본 정렬 키 반환
     *
     * @return CREATED_AT
     */
    public static UserSortKey defaultKey() {
        return CREATED_AT;
    }

    /**
     * 문자열로부터 UserSortKey 파싱 (대소문자 무관)
     *
     * @param value 문자열 (null이거나 유효하지 않으면 기본값 반환)
     * @return UserSortKey
     */
    public static UserSortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        String normalized = value.toUpperCase().trim().replace("-", "_");
        for (UserSortKey key : values()) {
            if (key.name().equals(normalized) || key.fieldName.equalsIgnoreCase(value.trim())) {
                return key;
            }
        }
        return defaultKey();
    }
}
