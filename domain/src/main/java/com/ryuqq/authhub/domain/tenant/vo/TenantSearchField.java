package com.ryuqq.authhub.domain.tenant.vo;

import com.ryuqq.authhub.domain.common.vo.SearchField;

/**
 * TenantSearchField - 테넌트 검색 필드 enum
 *
 * <p>테넌트 검색에 사용할 수 있는 필드를 정의합니다.
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
public enum TenantSearchField implements SearchField {

    /** 테넌트 이름 검색 */
    NAME("name");

    private final String fieldName;

    TenantSearchField(String fieldName) {
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
    public static TenantSearchField defaultField() {
        return NAME;
    }

    /**
     * 문자열에서 TenantSearchField 파싱
     *
     * @param value 검색 필드명
     * @return TenantSearchField (null이거나 유효하지 않으면 기본값)
     */
    public static TenantSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultField();
        }
        try {
            return TenantSearchField.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultField();
        }
    }
}
