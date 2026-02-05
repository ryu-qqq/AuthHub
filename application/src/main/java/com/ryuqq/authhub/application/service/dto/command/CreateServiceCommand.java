package com.ryuqq.authhub.application.service.dto.command;

/**
 * CreateServiceCommand - 서비스 생성 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceCode 서비스 코드 (필수, 예: SVC_STORE, SVC_B2B)
 * @param name 서비스 이름 (필수)
 * @param description 서비스 설명 (선택)
 * @author development-team
 * @since 1.0.0
 */
public record CreateServiceCommand(String serviceCode, String name, String description) {}
