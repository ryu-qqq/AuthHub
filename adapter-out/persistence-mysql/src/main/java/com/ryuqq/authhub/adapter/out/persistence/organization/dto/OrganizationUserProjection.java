package com.ryuqq.authhub.adapter.out.persistence.organization.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * OrganizationUserProjection - 조직별 사용자 조회용 QueryDSL Projection DTO
 *
 * <p>User 테이블에서 조직 조건으로 조회한 결과를 담는 Persistence Layer 내부 DTO입니다.
 *
 * <p><strong>주의:</strong>
 *
 * <ul>
 *   <li>Persistence Layer 내부에서만 사용
 *   <li>Application DTO 변환은 Adapter에서 수행
 * </ul>
 *
 * @param userId 사용자 UUID
 * @param email 사용자 이메일 (identifier)
 * @param tenantId 테넌트 UUID
 * @param createdAt 사용자 생성 일시
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationUserProjection(
        UUID userId, String email, UUID tenantId, Instant createdAt) {}
