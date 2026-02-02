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
 * @param serviceName 서비스 이름 (예: product-service)
 * @param urlPattern URL 패턴 (예: /api/v1/users/{id})
 * @param httpMethod HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부 (인증 불필요)
 * @author development-team
 * @since 1.0.0
 */
public record CreatePermissionEndpointCommand(
        Long permissionId,
        String serviceName,
        String urlPattern,
        String httpMethod,
        String description,
        boolean isPublic) {}
