package com.ryuqq.authhub.application.permission.dto.command;

import java.util.List;

/**
 * RegisterPermissionUsageCommand - 권한 사용 이력 등록 Command DTO
 *
 * <p>특정 권한이 어떤 서비스에서 사용되는지 등록합니다.
 *
 * <p><strong>사용 시나리오:</strong>
 *
 * <ul>
 *   <li>n8n에서 승인 후 권한 사용 이력 자동 등록
 *   <li>CI/CD 스캔 결과 저장
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionKey 권한 키 (예: product:read)
 * @param serviceName 서비스명 (예: product-service)
 * @param locations 코드 위치 목록 (예: ["ProductController.java:45"])
 * @author development-team
 * @since 1.0.0
 */
public record RegisterPermissionUsageCommand(
        String permissionKey, String serviceName, List<String> locations) {}
