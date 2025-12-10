package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * UserRoleResponse - 사용자 역할 할당 Response DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>불변 객체
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record UserRoleResponse(UUID userId, UUID roleId, Instant assignedAt) {}
