package com.ryuqq.authhub.application.permission.assembler;

import com.ryuqq.authhub.application.permission.dto.response.PermissionUsageResponse;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.vo.CodeLocation;
import java.util.List;
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

    /**
     * 문자열 리스트 → CodeLocation 리스트 변환
     *
     * @param locations 코드 위치 문자열 리스트 (nullable)
     * @return CodeLocation 리스트 (빈 리스트 가능)
     */
    public List<CodeLocation> toCodeLocations(List<String> locations) {
        if (locations == null || locations.isEmpty()) {
            return List.of();
        }
        return locations.stream().map(CodeLocation::of).toList();
    }
}
