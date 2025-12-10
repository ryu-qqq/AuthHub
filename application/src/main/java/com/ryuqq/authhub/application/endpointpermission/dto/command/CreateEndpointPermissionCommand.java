package com.ryuqq.authhub.application.endpointpermission.dto.command;

import java.util.Set;

/**
 * CreateEndpointPermissionCommand - 엔드포인트 권한 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceName 서비스 이름 (예: auth-hub)
 * @param path 엔드포인트 경로 (예: /api/v1/users/{userId})
 * @param method HTTP 메서드 (예: GET, POST)
 * @param description 엔드포인트 설명 (선택)
 * @param isPublic 공개 여부 (true: 권한 체크 스킵)
 * @param requiredPermissions 필요 권한 목록 (OR 조건)
 * @param requiredRoles 필요 역할 목록 (OR 조건)
 * @author development-team
 * @since 1.0.0
 */
public record CreateEndpointPermissionCommand(
        String serviceName,
        String path,
        String method,
        String description,
        boolean isPublic,
        Set<String> requiredPermissions,
        Set<String> requiredRoles) {}
