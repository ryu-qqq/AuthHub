package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * UserDetailResponse - 사용자 상세 조회용 Detail 응답 DTO
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답 DTO입니다. 사용자의 할당된 역할 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시합니다.
 *
 * <p><strong>UserSummaryResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>roles 목록 추가 (할당된 역할 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
 * </ul>
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
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @param identifier 사용자 식별자 (이메일)
 * @param status 사용자 상태
 * @param roles 할당된 역할 목록
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author development-team
 * @since 1.0.0
 * @see UserSummaryResponse 목록 조회용 응답 DTO
 * @see UserRoleSummary 역할 요약 정보
 */
public record UserDetailResponse(
        UUID userId,
        UUID tenantId,
        String tenantName,
        UUID organizationId,
        String organizationName,
        String identifier,
        String status,
        List<UserRoleSummary> roles,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * UserRoleSummary - 사용자 상세 조회 시 포함되는 역할 요약 정보
     *
     * <p>사용자에게 할당된 역할의 핵심 정보만 포함합니다.
     *
     * @param roleId 역할 ID
     * @param name 역할 이름
     * @param description 역할 설명
     * @param scope 역할 범위 (GLOBAL, TENANT, ORGANIZATION)
     * @param type 역할 유형 (SYSTEM, CUSTOM)
     */
    public record UserRoleSummary(
            UUID roleId, String name, String description, String scope, String type) {}
}
