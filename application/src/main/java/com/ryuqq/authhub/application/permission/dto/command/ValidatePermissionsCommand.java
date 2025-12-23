package com.ryuqq.authhub.application.permission.dto.command;

import java.util.List;

/**
 * ValidatePermissionsCommand - 권한 검증 Command DTO
 *
 * <p>CI/CD에서 PermissionScanner가 생성한 permissions.json을 검증합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceName 서비스명 (예: product-service, order-service)
 * @param permissions 검증할 권한 목록
 * @author development-team
 * @since 1.0.0
 */
public record ValidatePermissionsCommand(String serviceName, List<PermissionEntry> permissions) {

    /**
     * 권한 항목
     *
     * @param key 권한 키 (예: product:read, order:write)
     * @param locations 해당 권한이 사용된 위치 목록 (파일:라인)
     */
    public record PermissionEntry(String key, List<String> locations) {}
}
