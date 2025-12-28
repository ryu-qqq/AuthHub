package com.ryuqq.authhub.application.tenant.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * TenantDetailResponse - Admin 테넌트 상세 조회용 Detail 응답 DTO
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 테넌트에 소속된 조직 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>TenantSummaryResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>organizations 목록 추가 (소속 조직 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Jackson 어노테이션 금지 (REST API 책임)
 *   <li>Lombok 금지
 *   <li>중첩 Record를 사용하여 조직 요약 정보 표현
 * </ul>
 *
 * @param tenantId 테넌트 ID
 * @param name 테넌트 이름
 * @param status 테넌트 상태
 * @param organizations 테넌트에 소속된 조직 목록 (최근 N개)
 * @param organizationCount 테넌트에 소속된 전체 조직 수
 * @param createdAt 생성 시간
 * @param updatedAt 수정 시간
 * @author development-team
 * @since 1.0.0
 * @see TenantSummaryResponse 목록 조회용 응답 DTO
 */
public record TenantDetailResponse(
        UUID tenantId,
        String name,
        String status,
        List<TenantOrganizationSummary> organizations,
        int organizationCount,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * TenantOrganizationSummary - 테넌트 상세 조회 시 포함되는 조직 요약 정보
     *
     * <p>테넌트에 소속된 조직의 핵심 정보만 포함합니다.
     *
     * @param organizationId 조직 ID
     * @param name 조직 이름
     * @param status 조직 상태
     * @param createdAt 생성 시간
     */
    public record TenantOrganizationSummary(
            UUID organizationId, String name, String status, Instant createdAt) {}
}
