package com.ryuqq.authhub.application.rolepermission.dto.response;

import java.time.Instant;

/**
 * RolePermissionResult - 역할-권한 관계 조회 결과
 *
 * <p>역할-권한 관계 정보를 담는 응답 DTO입니다.
 *
 * @param rolePermissionId 관계 ID
 * @param roleId 역할 ID
 * @param permissionId 권한 ID
 * @param createdAt 생성 시간
 * @author development-team
 * @since 1.0.0
 */
public record RolePermissionResult(
        Long rolePermissionId, Long roleId, Long permissionId, Instant createdAt) {}
