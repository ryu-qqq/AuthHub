package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * UserSummaryApiResponse - 사용자 목록 조회용 Summary 응답 DTO (Admin용)
 *
 * <p>어드민 화면 목록 조회에 최적화된 응답입니다. FK 대신 관련 엔티티명을 포함하여 프론트엔드에서 추가 조회 없이 표시할 수 있습니다.
 *
 * <p><strong>UserApiResponse와의 차이점:</strong>
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
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see UserApiResponse 기본 응답 DTO
 * @see UserDetailApiResponse 상세 조회용 응답 DTO
 */
@Schema(description = "사용자 목록 응답 (Admin용)")
public record UserSummaryApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "테넌트 이름") String tenantName,
        @Schema(description = "조직 ID") String organizationId,
        @Schema(description = "조직 이름") String organizationName,
        @Schema(description = "사용자 식별자") String identifier,
        @Schema(description = "사용자 상태") String status,
        @Schema(description = "할당된 역할 수") int roleCount,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
