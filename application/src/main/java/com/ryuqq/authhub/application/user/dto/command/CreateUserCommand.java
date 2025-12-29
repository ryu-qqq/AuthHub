package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * CreateUserCommand - 사용자 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param tenantId 테넌트 ID
 * @param organizationId 조직 ID
 * @param identifier 사용자 식별자 (이메일)
 * @param phoneNumber 핸드폰 번호 (한국 형식, 필수)
 * @param password 비밀번호 (plain text, Service에서 해싱)
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserCommand(
        UUID tenantId,
        UUID organizationId,
        String identifier,
        String phoneNumber,
        String password) {}
