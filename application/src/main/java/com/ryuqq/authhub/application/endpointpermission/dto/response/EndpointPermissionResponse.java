package com.ryuqq.authhub.application.endpointpermission.dto.response;

import java.time.Instant;
import java.util.Set;

/**
 * EndpointPermissionResponse - 엔드포인트 권한 Response DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param id 엔드포인트 권한 ID (UUID 문자열)
 * @param serviceName 서비스 이름
 * @param path 엔드포인트 경로
 * @param method HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 여부
 * @param requiredPermissions 필요 권한 목록
 * @param requiredRoles 필요 역할 목록
 * @param version 버전 (낙관적 락)
 * @param createdAt 생성 일시
 * @param updatedAt 수정 일시
 * @author development-team
 * @since 1.0.0
 */
public record EndpointPermissionResponse(
        String id,
        String serviceName,
        String path,
        String method,
        String description,
        boolean isPublic,
        Set<String> requiredPermissions,
        Set<String> requiredRoles,
        long version,
        Instant createdAt,
        Instant updatedAt) {}
