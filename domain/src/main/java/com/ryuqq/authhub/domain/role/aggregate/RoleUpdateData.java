package com.ryuqq.authhub.domain.role.aggregate;

/**
 * RoleUpdateData - 역할 수정 데이터 Value Object
 *
 * <p>Role 수정 시 변경 가능한 필드들을 담는 VO입니다.
 *
 * <p><strong>변경 가능한 필드:</strong>
 *
 * <ul>
 *   <li>displayName: 표시 이름 (UI용)
 *   <li>description: 역할 설명
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 사용
 *   <li>Lombok 금지
 *   <li>불변 객체
 * </ul>
 *
 * @param displayName 표시 이름 (null 허용)
 * @param description 역할 설명 (null 허용)
 * @author development-team
 * @since 1.0.0
 */
public record RoleUpdateData(String displayName, String description) {

    /**
     * RoleUpdateData 생성
     *
     * @param displayName 표시 이름
     * @param description 역할 설명
     * @return RoleUpdateData 인스턴스
     */
    public static RoleUpdateData of(String displayName, String description) {
        return new RoleUpdateData(displayName, description);
    }

    /**
     * 표시 이름이 있는지 확인
     *
     * @return displayName이 null이 아니면 true
     */
    public boolean hasDisplayName() {
        return displayName != null;
    }

    /**
     * 설명이 있는지 확인
     *
     * @return description이 null이 아니면 true
     */
    public boolean hasDescription() {
        return description != null;
    }

    /**
     * 변경할 내용이 있는지 확인
     *
     * @return 하나라도 변경할 내용이 있으면 true
     */
    public boolean hasAnyUpdate() {
        return hasDisplayName() || hasDescription();
    }
}
