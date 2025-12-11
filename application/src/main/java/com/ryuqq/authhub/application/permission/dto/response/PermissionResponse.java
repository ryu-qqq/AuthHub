package com.ryuqq.authhub.application.permission.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * PermissionResponse - 권한 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param permissionId 권한 ID
 * @param key 권한 키 (resource:action 형식)
 * @param resource 리소스 식별자
 * @param action 액션 식별자
 * @param description 권한 설명
 * @param type 권한 타입 (SYSTEM/CUSTOM)
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 */
public record PermissionResponse(
        UUID permissionId,
        String key,
        String resource,
        String action,
        String description,
        String type,
        Instant createdAt,
        Instant updatedAt) {}
