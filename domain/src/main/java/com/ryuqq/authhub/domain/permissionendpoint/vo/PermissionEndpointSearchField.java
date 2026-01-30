package com.ryuqq.authhub.domain.permissionendpoint.vo;

/**
 * PermissionEndpointSearchField - PermissionEndpoint 검색 필드 열거형
 *
 * <p>PermissionEndpoint 검색 시 사용되는 검색 대상 필드를 정의합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public enum PermissionEndpointSearchField {

    /** URL 패턴 검색 */
    URL_PATTERN("urlPattern"),

    /** HTTP 메서드 검색 */
    HTTP_METHOD("httpMethod"),

    /** 설명 검색 */
    DESCRIPTION("description");

    private final String fieldName;

    PermissionEndpointSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 필드명 반환
     *
     * @return 필드명
     */
    public String fieldName() {
        return fieldName;
    }

    /**
     * 문자열로부터 PermissionEndpointSearchField 변환
     *
     * @param value 검색 필드 문자열
     * @return PermissionEndpointSearchField enum
     * @throws IllegalArgumentException 유효하지 않은 값인 경우
     */
    public static PermissionEndpointSearchField from(String value) {
        if (value == null || value.isBlank()) {
            return defaultField();
        }
        for (PermissionEndpointSearchField field : values()) {
            if (field.name().equalsIgnoreCase(value) || field.fieldName.equalsIgnoreCase(value)) {
                return field;
            }
        }
        return defaultField();
    }

    /**
     * 기본 검색 필드 반환
     *
     * @return URL_PATTERN
     */
    public static PermissionEndpointSearchField defaultField() {
        return URL_PATTERN;
    }
}
