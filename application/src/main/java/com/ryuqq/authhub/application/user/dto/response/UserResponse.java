package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * UserResponse - 사용자 응답 DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 사용자 ID
 * @param tenantId 테넌트 ID
 * @param organizationId 조직 ID
 * @param identifier 사용자 식별자 (이메일)
 * @param phoneNumber 핸드폰 번호
 * @param status 사용자 상태
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author development-team
 * @since 1.0.0
 */
public record UserResponse(
        UUID userId,
        UUID tenantId,
        UUID organizationId,
        String identifier,
        String phoneNumber,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
