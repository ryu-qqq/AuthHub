package com.ryuqq.authhub.adapter.in.rest.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

/**
 * UserDetailApiResponse - 사용자 상세 조회용 Detail 응답 DTO (Admin용)
 *
 * <p>어드민 화면 상세 조회에 최적화된 응답입니다. 사용자의 할당된 역할 목록을 포함하여 추가 API 호출 없이 상세 정보를 표시할 수 있습니다.
 *
 * <p><strong>UserSummaryApiResponse와의 차이점:</strong>
 *
 * <ul>
 *   <li>roles 목록 추가 (할당된 역할 상세 정보)
 *   <li>단건 상세 조회 API에서 사용
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
 * @see UserSummaryApiResponse 목록 조회용 응답 DTO
 * @see UserRoleSummaryApiResponse 역할 요약 정보
 */
@Schema(description = "사용자 상세 응답 (Admin용)")
public record UserDetailApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "테넌트 ID") String tenantId,
        @Schema(description = "테넌트 이름") String tenantName,
        @Schema(description = "조직 ID") String organizationId,
        @Schema(description = "조직 이름") String organizationName,
        @Schema(description = "사용자 식별자") String identifier,
        @Schema(description = "사용자 상태") String status,
        @Schema(description = "할당된 역할 목록") List<UserRoleSummaryApiResponse> roles,
        @Schema(description = "생성 일시") Instant createdAt,
        @Schema(description = "수정 일시") Instant updatedAt) {}
