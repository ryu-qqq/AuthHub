package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;

/**
 * OrganizationResult - 조직 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 Organization 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-007: Response DTO는 createdAt, updatedAt 시간 필드 필수 포함.
 *
 * <p>RDTO-008: Response DTO는 Domain 타입 의존 금지.
 *
 * @param organizationId 조직 ID
 * @param tenantId 테넌트 ID
 * @param name 조직 이름
 * @param status 조직 상태
 * @param createdAt 생성 시각
 * @param updatedAt 수정 시각
 * @author development-team
 * @since 1.0.0
 */
public record OrganizationResult(
        String organizationId,
        String tenantId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt) {}
