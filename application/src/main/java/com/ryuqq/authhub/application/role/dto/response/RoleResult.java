package com.ryuqq.authhub.application.role.dto.response;

import java.time.Instant;

/**
 * RoleResult - 역할 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 Role 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID (null이면 Global)
 * @param name 역할 이름 (예: "USER_MANAGER")
 * @param displayName 표시 이름 (예: "사용자 관리자")
 * @param description 역할 설명
 * @param type 역할 유형 (SYSTEM 또는 CUSTOM)
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record RoleResult(
        Long roleId,
        String tenantId,
        String name,
        String displayName,
        String description,
        String type,
        Instant createdAt,
        Instant updatedAt) {}
