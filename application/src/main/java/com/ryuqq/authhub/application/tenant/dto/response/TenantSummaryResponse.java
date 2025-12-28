package com.ryuqq.authhub.application.tenant.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * TenantSummaryResponse - Admin 테넌트 목록 조회용 Summary 응답 DTO
 *
 * <p>어드민 화면 목록 조회에 최적화된 응답입니다. organizationCount를 포함하여 프론트엔드에서 추가 조회 없이 표시할 수 있습니다.
 *
 * <p><strong>TenantResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>organizationCount 추가 (테넌트에 소속된 조직 수)
 *   <li>목록 조회 API에서 사용
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Jackson 어노테이션 금지 (REST API 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태
 * @param organizationCount 테넌트에 소속된 조직 수
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 * @see TenantResponse 기본 응답 DTO
 * @see TenantDetailResponse 상세 조회용 응답 DTO
 */
public record TenantSummaryResponse(
        UUID tenantId,
        String name,
        String status,
        int organizationCount,
        Instant createdAt,
        Instant updatedAt) {}
