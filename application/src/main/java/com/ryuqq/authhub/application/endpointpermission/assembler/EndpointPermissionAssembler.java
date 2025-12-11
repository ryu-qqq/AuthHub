package com.ryuqq.authhub.application.endpointpermission.assembler;

import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionSpecResponse;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import org.springframework.stereotype.Component;

/**
 * EndpointPermissionAssembler - Domain → Response 변환
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
public class EndpointPermissionAssembler {

    /**
     * EndpointPermission → EndpointPermissionResponse 변환
     *
     * @param endpointPermission EndpointPermission Aggregate
     * @return EndpointPermissionResponse DTO
     */
    public EndpointPermissionResponse toResponse(EndpointPermission endpointPermission) {
        return new EndpointPermissionResponse(
                endpointPermission.endpointPermissionIdValue().toString(),
                endpointPermission.serviceNameValue(),
                endpointPermission.pathValue(),
                endpointPermission.methodValue(),
                endpointPermission.descriptionValue(),
                endpointPermission.isPublic(),
                endpointPermission.requiredPermissionValues(),
                endpointPermission.requiredRoleValues(),
                endpointPermission.getVersion(),
                endpointPermission.createdAt(),
                endpointPermission.updatedAt());
    }

    /**
     * EndpointPermission → EndpointPermissionSpecResponse 변환 (인증용)
     *
     * @param endpointPermission EndpointPermission Aggregate
     * @return EndpointPermissionSpecResponse DTO
     */
    public EndpointPermissionSpecResponse toSpecResponse(EndpointPermission endpointPermission) {
        return new EndpointPermissionSpecResponse(
                endpointPermission.isPublic(),
                endpointPermission.requiredPermissionValues(),
                endpointPermission.requiredRoleValues());
    }
}
