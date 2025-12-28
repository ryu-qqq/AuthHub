package com.ryuqq.authhub.application.organization.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * OrganizationDetailResponse - 조직 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 조직에 소속된 사용자 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>OrganizationSummaryResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>users 목록 추가 (소속 사용자 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
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
 * @param users 소속 사용자 목록 (최근 N명)
 * @param userCount 조직에 소속된 전체 사용자 수
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 * @see OrganizationSummaryResponse 목록 조회용 응답 DTO
 * @see OrganizationUserSummary 사용자 요약 정보
 */
public record OrganizationDetailResponse(
        UUID organizationId,
        UUID tenantId,
        String tenantName,
        String name,
        String status,
        List<OrganizationUserSummary> users,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * OrganizationUserSummary - 조직 상세 조회 시 포함되는 사용자 요약 정보
     *
     * <p>조직에 소속된 사용자의 핵심 정보만 포함합니다.
     *
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param createdAt 소속 일시
     */
    public record OrganizationUserSummary(UUID userId, String email, Instant createdAt) {}
}
