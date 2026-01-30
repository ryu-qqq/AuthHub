package com.ryuqq.authhub.domain.permission.vo;

/**
 * PermissionUpdateData - 권한 수정 데이터 Value Object
 *
 * <p>Permission 수정 시 변경 가능한 필드들을 담는 VO입니다.
 *
 * <p>현재는 description만 변경 가능하지만, 추후 확장을 고려한 설계입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 사용
 *   <li>Lombok 금지
 *   <li>불변 객체
 * </ul>
 *
 * @param description 권한 설명 (null 허용)
 * @author development-team
 * @since 1.0.0
 */
public record PermissionUpdateData(String description) {

    /**
     * PermissionUpdateData 생성
     *
     * @param description 권한 설명
     * @return PermissionUpdateData 인스턴스
     */
    public static PermissionUpdateData of(String description) {
        return new PermissionUpdateData(description);
    }

    /**
     * 설명이 있는지 확인
     *
     * @return description이 null이 아니면 true
     */
    public boolean hasDescription() {
        return description != null;
    }
}
