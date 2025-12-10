package com.ryuqq.authhub.application.role.assembler;

import com.ryuqq.authhub.application.role.dto.response.RoleResponse;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import org.springframework.stereotype.Component;

/**
 * RoleAssembler - Domain → Response 변환
 *
 * <p>Domain Aggregate를 Response DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code toResponse()} 메서드 네이밍
 *   <li>순수 변환만 수행 (비즈니스 로직 금지)
 *   <li>Port 호출 금지
 *   <li>{@code @Transactional} 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleAssembler {

    /**
     * Role → RoleResponse 변환
     *
     * @param role Role Aggregate
     * @return RoleResponse DTO
     */
    public RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.roleIdValue(),
                role.tenantIdValue(),
                role.nameValue(),
                role.descriptionValue(),
                role.scopeValue(),
                role.typeValue(),
                role.createdAt(),
                role.updatedAt());
    }
}
