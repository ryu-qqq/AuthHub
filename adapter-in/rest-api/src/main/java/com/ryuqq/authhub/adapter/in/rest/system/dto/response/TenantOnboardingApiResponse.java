package com.ryuqq.authhub.adapter.in.rest.system.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TenantOnboardingApiResponse - 테넌트 온보딩 API 응답 DTO
 *
 * <p>테넌트 온보딩 완료 후 생성된 리소스 정보를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "테넌트 온보딩 응답")
public record TenantOnboardingApiResponse(
        @Schema(description = "생성된 테넌트 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                String tenantId,
        @Schema(description = "생성된 조직 ID", example = "660e8400-e29b-41d4-a716-446655440000")
                String organizationId,
        @Schema(description = "생성된 마스터 사용자 ID", example = "770e8400-e29b-41d4-a716-446655440000")
                String userId,
        @Schema(description = "임시 비밀번호 (호출자가 이메일 발송 필요)", example = "Abc123!@#xyz")
                String temporaryPassword) {}
