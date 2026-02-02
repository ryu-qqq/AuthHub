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
 * @param serviceName 서비스 이름
 * @param urlPattern URL 패턴
 * @param httpMethod HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부
 * @author development-team
 * @since 1.0.0
 */
public record UpdatePermissionEndpointCommand(
        Long permissionEndpointId,
        String serviceName,
        String urlPattern,
        String httpMethod,
        String description,
        boolean isPublic) {}
