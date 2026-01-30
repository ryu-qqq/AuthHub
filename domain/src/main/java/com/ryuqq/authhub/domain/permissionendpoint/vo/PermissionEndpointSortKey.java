package com.ryuqq.authhub.domain.permissionendpoint.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * PermissionEndpointSortKey - PermissionEndpoint 정렬 키 열거형
 *
 * <p>PermissionEndpoint 목록 조회 시 사용되는 정렬 기준을 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionEndpointSortKey implements SortKey {

    /** 생성일시 기준 정렬 */
    CREATED_AT("createdAt"),

    /** 수정일시 기준 정렬 */
    UPDATED_AT("updatedAt"),

    /** URL 패턴 기준 정렬 */
    URL_PATTERN("urlPattern"),

    /** HTTP 메서드 기준 정렬 */
    HTTP_METHOD("httpMethod");

    private final String fieldName;

    PermissionEndpointSortKey(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 필드명 반환
     *
     * @return 필드명
     */
    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 PermissionEndpointSortKey 변환
     *
     * @param value 정렬 키 문자열
     * @return PermissionEndpointSortKey enum
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static PermissionEndpointSortKey from(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        for (PermissionEndpointSortKey key : values()) {
            if (key.name().equalsIgnoreCase(value) || key.fieldName.equalsIgnoreCase(value)) {
                return key;
            }
        }
        return defaultKey();
    }

    /**
     * 기본 정렬 키 반환
     *
     * @return CREATED_AT
     */
    public static PermissionEndpointSortKey defaultKey() {
        return CREATED_AT;
    }
}
