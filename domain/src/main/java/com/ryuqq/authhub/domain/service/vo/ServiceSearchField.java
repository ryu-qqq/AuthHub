package com.ryuqq.authhub.domain.service.vo;

import com.ryuqq.authhub.domain.common.vo.SearchField;

/**
 * ServiceSearchField - 서비스 검색 필드 enum
 *
 * <p>서비스 검색에 사용할 수 있는 필드를 정의합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>SearchField 인터페이스 구현
 *   <li>검색 가능한 필드만 enum 값으로 노출
 *   <li>DB 컬럼명 매핑은 Adapter에서 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum ServiceSearchField implements SearchField {

    /** 서비스 코드 검색 */
    SERVICE_CODE("serviceCode"),

    /** 서비스 이름 검색 */
    NAME("name");

    private final String fieldName;

    ServiceSearchField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    /**
     * 기본 검색 필드
     *
     * @return NAME (기본값)
     */
    public static ServiceSearchField defaultField() {
        return NAME;
    }

    /**
     * 문자열에서 ServiceSearchField 파싱
     *
     * @param value 검색 필드명
     * @return ServiceSearchField (null이거나 유효하지 않으면 기본값)
     */
    public static ServiceSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultField();
        }
        try {
            return ServiceSearchField.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultField();
        }
    }
}
