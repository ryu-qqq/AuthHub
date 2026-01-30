package com.ryuqq.authhub.domain.organization.vo;

import com.ryuqq.authhub.domain.common.vo.SearchField;

/**
 * OrganizationSearchField - 조직 검색 필드 enum
 *
 * <p>조직 검색에 사용할 수 있는 필드를 정의합니다.
 *
 * <p><strong>지원 검색 필드:</strong>
 *
 * <ul>
 *   <li>{@link #NAME}: 조직 이름 검색
 * </ul>
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
public enum OrganizationSearchField implements SearchField {

    /** 조직 이름 검색 */
    NAME("name");

    private final String fieldName;

    OrganizationSearchField(String fieldName) {
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
    public static OrganizationSearchField defaultField() {
        return NAME;
    }

    /**
     * 문자열에서 OrganizationSearchField 파싱
     *
     * @param value 검색 필드명
     * @return OrganizationSearchField (null이거나 유효하지 않으면 기본값)
     */
    public static OrganizationSearchField fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultField();
        }
        try {
            return OrganizationSearchField.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultField();
        }
    }
}
