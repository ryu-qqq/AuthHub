package com.ryuqq.authhub.domain.service.vo;

import com.ryuqq.authhub.domain.common.vo.SortKey;

/**
 * ServiceSortKey - 서비스 정렬 키 Value Object
 *
 * <p>서비스 목록 조회 시 정렬 가능한 필드를 정의합니다.
 *
 * <p><strong>지원 정렬 필드:</strong>
 *
 * <ul>
 *   <li>{@link #CREATED_AT}: 생성일시 (기본값)
 *   <li>{@link #UPDATED_AT}: 수정일시
 *   <li>{@link #NAME}: 서비스 이름
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ServiceSortKey implements SortKey {
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
    NAME("name");

    private final String fieldName;

    ServiceSortKey(String fieldName) {
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
    public static ServiceSortKey defaultKey() {
        return CREATED_AT;
    }

    /**
     * 문자열로부터 ServiceSortKey 파싱 (대소문자 무관)
     *
     * @param value 문자열 (null이거나 유효하지 않으면 기본값 반환)
     * @return ServiceSortKey
     */
    public static ServiceSortKey fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultKey();
        }
        String normalized = value.toUpperCase().trim().replace("-", "_");
        for (ServiceSortKey key : values()) {
            if (key.name().equals(normalized) || key.fieldName.equalsIgnoreCase(value.trim())) {
                return key;
            }
        }
        return defaultKey();
    }
}
