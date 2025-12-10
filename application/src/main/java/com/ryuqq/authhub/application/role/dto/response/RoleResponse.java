package com.ryuqq.authhub.application.role.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * RoleResponse - 역할 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID (GLOBAL 범위인 경우 null)
 * @param name 역할 이름
 * @param description 역할 설명
 * @param scope 역할 범위
 * @param type 역할 유형
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 */
public record RoleResponse(
        UUID roleId,
        UUID tenantId,
        String name,
        String description,
        String scope,
        String type,
        Instant createdAt,
        Instant updatedAt) {}
