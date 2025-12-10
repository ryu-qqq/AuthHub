package com.ryuqq.authhub.application.user.assembler;

import com.ryuqq.authhub.application.user.dto.response.UserRoleResponse;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import org.springframework.stereotype.Component;

/**
 * UserRoleAssembler - 사용자 역할 Assembler
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Response DTO 변환
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserRoleAssembler {

    /**
     * UserRole 도메인 객체를 Response DTO로 변환합니다.
     *
     * @param userRole 사용자 역할 도메인 객체
     * @return 사용자 역할 Response DTO
     */
    public UserRoleResponse toResponse(UserRole userRole) {
        return new UserRoleResponse(
                userRole.userIdValue(), userRole.roleIdValue(), userRole.getAssignedAt());
    }
}
