package com.ryuqq.authhub.domain.identity.organization.vo;

/**
 * 조직의 상태를 나타내는 Enum.
 *
 * <p>조직의 라이프사이클 상태를 정의하며, 비즈니스 규칙에 따라 상태 전환이 제어됩니다.</p>
 *
 * <p><strong>지원 상태:</strong></p>
 * <ul>
 *   <li>ACTIVE - 활성화 상태 (정상 운영 중)</li>
 *   <li>SUSPENDED - 일시 정지 상태 (정책 위반, 미납 등)</li>
 *   <li>DELETED - 삭제 상태 (논리적 삭제, 복구 불가)</li>
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
public enum OrganizationStatus {

    /**
     * 활성화 상태.
     * 조직이 정상적으로 운영되고 있으며, 모든 기능을 사용할 수 있습니다.
     */
    ACTIVE("활성"),

    /**
     * 일시 정지 상태.
     * 정책 위반, 결제 미납 등의 사유로 일시적으로 기능이 제한됩니다.
     * 관리자 승인 또는 문제 해결 시 ACTIVE 상태로 복구할 수 있습니다.
     */
    SUSPENDED("정지"),

    /**
     * 삭제 상태.
     * 조직이 논리적으로 삭제되어 복구할 수 없습니다.
     * 물리적 삭제 전까지 데이터는 보존되지만, 모든 기능이 차단됩니다.
     */
    DELETED("삭제");

    private final String description;

    /**
     * OrganizationStatus의 생성자.
     *
     * @param description 상태 설명
     */
    OrganizationStatus(final String description) {
        this.description = description;
    }

    /**
     * 조직 상태의 설명을 반환합니다.
     *
     * @return 상태 설명 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return description;
    }

    /**
     * 활성 상태인지 확인합니다.
     *
     * @return 활성 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 정지 상태인지 확인합니다.
     *
     * @return 정지 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    /**
     * 삭제 상태인지 확인합니다.
     *
     * @return 삭제 상태이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDeleted() {
        return this == DELETED;
    }

    /**
     * 조직이 운영 가능한 상태인지 확인합니다 (ACTIVE 상태).
     *
     * @return 운영 가능하면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isOperational() {
        return this == ACTIVE;
    }

    /**
     * 주어진 문자열이 유효한 OrganizationStatus인지 검증합니다.
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
            OrganizationStatus.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 문자열로부터 OrganizationStatus를 안전하게 변환합니다.
     *
     * @param value 변환 대상 문자열
     * @return OrganizationStatus 인스턴스
     * @throws IllegalArgumentException value가 null이거나 유효하지 않은 상태인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static OrganizationStatus fromString(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Organization status cannot be null");
        }
        try {
            return OrganizationStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid organization status: " + value, e);
        }
    }
}
