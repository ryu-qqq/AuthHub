package com.ryuqq.authhub.application.user.dto.response;

/**
 * CreateUserWithRolesResult - 사용자 생성 + 역할 할당 결과 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 생성된 사용자 ID (UUIDv7)
 * @param assignedRoleCount 할당된 역할 수
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserWithRolesResult(String userId, int assignedRoleCount) {

    /**
     * 결과 생성 팩토리 메서드
     *
     * @param userId 생성된 사용자 ID
     * @param assignedRoleCount 할당된 역할 수
     * @return CreateUserWithRolesResult 인스턴스
     */
    public static CreateUserWithRolesResult of(String userId, int assignedRoleCount) {
        return new CreateUserWithRolesResult(userId, assignedRoleCount);
    }
}
