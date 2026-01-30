package com.ryuqq.authhub.application.tenant.dto.command;

import java.util.Objects;

/**
 * OnboardingCommand - 온보딩 Command DTO
 *
 * <p>테넌트와 조직을 한 번에 생성하기 위한 Command입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * <p><strong>멱등키 필수:</strong>
 *
 * <ul>
 *   <li>X-Idempotency-Key 헤더를 통해 전달
 *   <li>네트워크 오류로 인한 재시도 시 중복 생성 방지
 *   <li>UUID 형식 권장
 * </ul>
 *
 * @param tenantName 테넌트 이름 (필수)
 * @param organizationName 조직 이름 (필수)
 * @param idempotencyKey 멱등키 (필수, X-Idempotency-Key 헤더)
 * @author development-team
 * @since 1.0.0
 */
public record OnboardingCommand(String tenantName, String organizationName, String idempotencyKey) {

    public OnboardingCommand {
        Objects.requireNonNull(tenantName, "tenantName must not be null");
        Objects.requireNonNull(organizationName, "organizationName must not be null");
        Objects.requireNonNull(idempotencyKey, "idempotencyKey must not be null");
        if (idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey must not be blank");
        }
    }
}
