package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import com.ryuqq.authhub.adapter.out.persistence.role.dto.RoleUserProjection;
import com.ryuqq.authhub.application.role.dto.response.RoleUserResponse;
import org.springframework.stereotype.Component;

/**
 * RoleUserMapper - Projection → Application DTO 변환 Mapper
 *
 * <p>Persistence Layer의 QueryDSL Projection을 Application Layer DTO로 변환합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>RoleUserProjection → RoleUserResponse
 *   <li>UUID → String 변환 (null-safe)
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Persistence DTO와 Application DTO 분리
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleUserMapper {

    /**
     * Projection → Application DTO 변환
     *
     * @param projection Persistence Layer Projection DTO
     * @return Application Layer Response DTO
     */
    public RoleUserResponse toResponse(RoleUserProjection projection) {
        return new RoleUserResponse(
                projection.userId() != null ? projection.userId().toString() : null,
                projection.email(),
                projection.tenantId() != null ? projection.tenantId().toString() : null,
                projection.organizationId() != null ? projection.organizationId().toString() : null,
                projection.assignedAt());
    }
}
