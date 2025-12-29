package com.ryuqq.authhub.application.permission.assembler;

import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import org.springframework.stereotype.Component;

/**
 * PermissionUsageAssembler - Domain → Response 변환
 *
 * <p>PermissionUsage Domain Aggregate를 Response DTO로 변환합니다.
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
public class PermissionUsageAssembler {

    /**
     * PermissionUsage → PermissionUsageResponse 변환
     *
     * @param usage PermissionUsage Aggregate
     * @return PermissionUsageResponse DTO
     */
    public PermissionUsageResponse toResponse(PermissionUsage usage) {
        return new PermissionUsageResponse(
                usage.usageIdValue(),
                usage.permissionKeyValue(),
                usage.serviceNameValue(),
                usage.locationValues(),
                usage.getLastScannedAt(),
                usage.createdAt());
    }
}
