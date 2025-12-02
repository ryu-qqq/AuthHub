package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * User Response DTO
 *
 * <p>사용자 정보를 클라이언트에게 반환하는 응답 객체입니다.
 *
 * <p><strong>보안 규칙:</strong>
 * <ul>
 *   <li>비밀번호, credential 등 민감 정보 절대 포함 금지</li>
 *   <li>Domain Entity 직접 노출 금지</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record UserResponse(
        UUID userId,
        Long tenantId,
        Long organizationId,
        String userType,
        String status,
        String name,
        String nickname,
        String profileImageUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
