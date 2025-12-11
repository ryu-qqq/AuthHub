package com.ryuqq.authhub.application.permission.assembler;

import com.ryuqq.authhub.application.permission.dto.response.PermissionResponse;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import org.springframework.stereotype.Component;

/**
 * PermissionAssembler - Domain → Response 변환
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
public class PermissionAssembler {

    /**
     * Permission → PermissionResponse 변환
     *
     * @param permission Permission Aggregate
     * @return PermissionResponse DTO
     */
    public PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.permissionIdValue(),
                permission.keyValue(),
                permission.resourceValue(),
                permission.actionValue(),
                permission.descriptionValue(),
                permission.isSystem() ? "SYSTEM" : "CUSTOM",
                permission.createdAt(),
                permission.updatedAt());
    }
}
