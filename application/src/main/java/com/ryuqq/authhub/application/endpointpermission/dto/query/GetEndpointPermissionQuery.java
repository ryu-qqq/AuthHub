package com.ryuqq.authhub.application.endpointpermission.dto.query;

/**
 * GetEndpointPermissionQuery - 엔드포인트 권한 단건 조회 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param endpointPermissionId 조회할 엔드포인트 권한 ID (UUID 문자열)
 * @author development-team
 * @since 1.0.0
 */
public record GetEndpointPermissionQuery(String endpointPermissionId) {}
