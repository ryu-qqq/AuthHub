package com.ryuqq.authhub.application.endpointpermission.dto.query;

/**
 * GetEndpointPermissionSpecQuery - 엔드포인트 권한 스펙 조회 Query DTO (인증용)
 *
 * <p>요청 경로와 메서드로 엔드포인트 권한 스펙을 조회합니다.
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
 * @param requestPath 요청 경로 (예: /api/v1/users/123)
 * @param method HTTP 메서드 (예: GET, POST)
 * @author development-team
 * @since 1.0.0
 */
public record GetEndpointPermissionSpecQuery(
        String serviceName, String requestPath, String method) {}
