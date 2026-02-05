package com.ryuqq.authhub.application.service.dto.command;

/**
 * UpdateServiceCommand - 서비스 수정 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceId 서비스 ID (필수, Long PK)
 * @param name 서비스 이름 (선택, null이면 변경하지 않음)
 * @param description 서비스 설명 (선택, null이면 변경하지 않음)
 * @param status 서비스 상태 (선택, null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateServiceCommand(
        Long serviceId, String name, String description, String status) {}
