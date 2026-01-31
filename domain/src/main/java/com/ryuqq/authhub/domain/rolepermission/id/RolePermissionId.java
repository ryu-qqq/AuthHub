package com.ryuqq.authhub.domain.rolepermission.id;

/**
 * RolePermissionId - 역할-권한 관계 식별자 (Value Object)
 *
 * <p>RolePermission Aggregate의 고유 식별자입니다.
 *
 * <p><strong>Long PK 전략:</strong>
 *
 * <ul>
 *   <li>Auto Increment Long 값을 PK로 사용
 *   <li>roleId + permissionId는 Unique Constraint로 관리
 *   <li>JPA Entity에서 @GeneratedValue(IDENTITY) 사용
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (record 자동 보장)
 *   <li>null 허용 안함 (생성 시 검증)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record RolePermissionId(Long value) {

    /**
     * Compact Constructor - null 검증
     *
     * @throws IllegalArgumentException value가 null인 경우
     */
    public RolePermissionId {
        if (value == null) {
            throw new IllegalArgumentException("RolePermissionId는 null일 수 없습니다");
        }
    }

    /**
     * Long 값으로 RolePermissionId 생성
     *
     * @param value Long 값 (not null)
     * @return RolePermissionId 인스턴스
     * @throws IllegalArgumentException value가 null인 경우
     */
    public static RolePermissionId of(Long value) {
        return new RolePermissionId(value);
    }
}
