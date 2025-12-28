package com.ryuqq.authhub.application.role.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * RoleSummaryResponse - 역할 목록 조회용 Summary 응답 DTO
 *
 * <p>어드민 화면 목록 조회에 최적화된 응답 DTO입니다. FK 대신 관련 엔티티명을 포함하여 프론트엔드에서 추가 조회 없이 표시 가능합니다.
 *
 * <p><strong>RoleResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>tenantName 추가 (FK 대신 이름)
 *   <li>permissionCount 추가 (할당된 권한 수)
 *   <li>userCount 추가 (역할이 할당된 사용자 수)
 *   <li>목록 조회 API에서 사용
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
 * @param roleId 역할 ID
 * @param tenantId 테넌트 ID (GLOBAL 범위인 경우 null)
 * @param tenantName 테넌트 이름 (GLOBAL 범위인 경우 null, 목록 표시용)
 * @param name 역할 이름
 * @param description 역할 설명
 * @param scope 역할 범위 (GLOBAL/TENANT/ORGANIZATION)
 * @param type 역할 유형 (SYSTEM/CUSTOM)
 * @param permissionCount 할당된 권한 수
 * @param userCount 역할이 할당된 사용자 수
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author development-team
 * @since 1.0.0
 * @see RoleResponse 기본 응답 DTO
 * @see RoleDetailResponse 상세 조회용 응답 DTO
 */
public record RoleSummaryResponse(
        UUID roleId,
        UUID tenantId,
        String tenantName,
        String name,
        String description,
        String scope,
        String type,
        int permissionCount,
        int userCount,
        Instant createdAt,
        Instant updatedAt) {}
