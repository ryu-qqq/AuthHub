package com.ryuqq.authhub.application.tenant.dto.response;

/**
 * OnboardingResult - 온보딩 결과 DTO
 *
 * <p>온보딩 완료 후 생성된 테넌트 ID와 조직 ID를 반환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Lombok 금지
 * </ul>
 *
 * @param tenantId 생성된 테넌트 ID (UUIDv7)
 * @param organizationId 생성된 조직 ID (UUIDv7)
 * @author development-team
 * @since 1.0.0
 */
public record OnboardingResult(String tenantId, String organizationId) {}
