package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * OrganizationSummaryResponse - 조직 목록 조회용 Summary 응답 DTO (Admin용)
 *
 * <p>어드민 화면 목록 조회에 최적화된 응답입니다. FK 대신 관련 엔티티명을 포함하여 프론트엔드에서 추가 조회 없이 표시할 수 있습니다.
 *
 * <p><strong>OrganizationResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>tenantName 추가 (FK 대신 이름)
 *   <li>userCount 추가 (조직에 소속된 사용자 수)
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
 * @param organizationId 조직 ID
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름 (FK 대신 이름)
 * @param name 조직 이름
 * @param status 조직 상태
 * @param userCount 조직에 소속된 사용자 수
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 * @see OrganizationResponse 기본 응답 DTO
 * @see OrganizationDetailResponse 상세 조회용 응답 DTO
 */
public record OrganizationSummaryResponse(
        UUID organizationId,
        UUID tenantId,
        String tenantName,
        String name,
        String status,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
