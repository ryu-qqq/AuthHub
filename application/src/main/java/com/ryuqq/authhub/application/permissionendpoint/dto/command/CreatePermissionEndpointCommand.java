package com.ryuqq.authhub.application.permissionendpoint.dto.command;

/**
 * CreatePermissionEndpointCommand - PermissionEndpoint 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionId 연결할 권한 ID
 * @param urlPattern URL 패턴 (예: /api/v1/users/{id})
 * @param httpMethod HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param description 설명
 * @author development-team
 * @since 1.0.0
 */
public record CreatePermissionEndpointCommand(
        Long permissionId, String urlPattern, String httpMethod, String description) {}
