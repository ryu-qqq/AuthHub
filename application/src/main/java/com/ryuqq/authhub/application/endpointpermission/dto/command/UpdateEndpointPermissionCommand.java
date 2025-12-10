package com.ryuqq.authhub.application.endpointpermission.dto.command;

import java.util.Set;

/**
 * UpdateEndpointPermissionCommand - 엔드포인트 권한 수정 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param endpointPermissionId 대상 엔드포인트 권한 ID (UUID 문자열)
 * @param description 변경할 설명 (null 가능)
 * @param isPublic 공개 여부 변경
 * @param requiredPermissions 변경할 필요 권한 목록 (null이면 변경 안함)
 * @param requiredRoles 변경할 필요 역할 목록 (null이면 변경 안함)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateEndpointPermissionCommand(
        String endpointPermissionId,
        String description,
        Boolean isPublic,
        Set<String> requiredPermissions,
        Set<String> requiredRoles) {}
