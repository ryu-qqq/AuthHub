package com.ryuqq.authhub.application.permissionendpoint.dto.command;

/**
 * UpdatePermissionEndpointCommand - PermissionEndpoint 수정 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionEndpointId 수정할 엔드포인트 ID
 * @param urlPattern 새 URL 패턴 (null이면 변경 안 함)
 * @param httpMethod 새 HTTP 메서드 (null이면 변경 안 함)
 * @param description 새 설명 (null이면 변경 안 함)
 * @author development-team
 * @since 1.0.0
 */
public record UpdatePermissionEndpointCommand(
        Long permissionEndpointId, String urlPattern, String httpMethod, String description) {}
