package com.ryuqq.authhub.adapter.in.rest.tenant.dto.response;

/**
 * CreateTenantApiResponse - 테넌트 생성 응답 DTO
 *
 * <p>테넌트 등록 API의 응답 본문을 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiResponse 네이밍 규칙
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantApiResponse(String tenantId) {}
