package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * UserSummaryResponse - 사용자 목록 조회용 Summary 응답 DTO
 *
 * <p>어드민 화면 목록 조회에 최적화된 응답 DTO입니다. FK 대신 관련 엔티티명을 포함하여 프론트엔드에서 추가 조회 없이 표시 가능합니다.
 *
 * <p><strong>UserResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>tenantName, organizationName 추가 (FK 대신 이름)
 *   <li>roleCount 추가 (할당된 역할 수)
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
 * @param userId 사용자 ID
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름 (목록 표시용)
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름 (목록 표시용)
 * @param identifier 사용자 식별자 (이메일)
 * @param phoneNumber 핸드폰 번호
 * @param status 사용자 상태
 * @param roleCount 할당된 역할 수
 * @param createdAt 생성일시
 * @param updatedAt 수정일시
 * @author development-team
 * @since 1.0.0
 * @see UserResponse 기본 응답 DTO
 * @see UserDetailResponse 상세 조회용 응답 DTO
 */
public record UserSummaryResponse(
        UUID userId,
        UUID tenantId,
        String tenantName,
        UUID organizationId,
        String organizationName,
        String identifier,
        String phoneNumber,
        String status,
        int roleCount,
        Instant createdAt,
        Instant updatedAt) {}
