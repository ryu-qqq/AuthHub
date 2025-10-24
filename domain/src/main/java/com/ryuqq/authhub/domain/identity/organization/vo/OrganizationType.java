package com.ryuqq.authhub.domain.identity.organization.vo;

/**
 * 조직의 유형을 나타내는 Enum.
 *
 * <p>AuthHub Identity 시스템에서 관리하는 조직 타입을 정의합니다.</p>
 *
 * <p><strong>지원 타입:</strong></p>
 * <ul>
 *   <li>SELLER - B2C 플랫폼의 판매자 조직</li>
 *   <li>COMPANY - B2B 플랫폼의 기업 조직</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Pure Java Enum</li>
 *   <li>불변성 보장 - Enum의 본질적 불변성</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum OrganizationType {

    /**
     * B2C 플랫폼의 판매자 조직.
     * 상품을 등록하고 판매하는 개인 또는 사업자 조직입니다.
     */
    SELLER("판매자"),

    /**
     * B2B 플랫폼의 기업 조직.
     * 기업 간 거래를 위한 구매자 또는 공급업체 조직입니다.
     */
    COMPANY("기업");

    private final String description;

    /**
     * OrganizationType의 생성자.
     *
     * @param description 타입 설명
     */
    OrganizationType(final String description) {
        this.description = description;
    }

    /**
     * 조직 타입의 설명을 반환합니다.
     *
     * @return 타입 설명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * 주어진 문자열이 유효한 OrganizationType인지 검증합니다.
     *
     * @param value 검증 대상 문자열
     * @return 유효하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static boolean isValid(final String value) {
        if (value == null) {
            return false;
        }
        try {
            OrganizationType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 문자열로부터 OrganizationType을 안전하게 변환합니다.
     *
     * @param value 변환 대상 문자열
     * @return OrganizationType 인스턴스
     * @throws IllegalArgumentException value가 null이거나 유효하지 않은 타입인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static OrganizationType fromString(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Organization type cannot be null");
        }
        try {
            return OrganizationType.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid organization type: " + value, e);
        }
    }
}
