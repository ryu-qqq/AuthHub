package com.ryuqq.authhub.application.user.dto.command;

/**
 * UpdateUserCommand - 사용자 정보 수정 Command DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 수정 대상 사용자 ID (필수)
 * @param phoneNumber 전화번호 (null이면 변경 안 함)
 * @author development-team
 * @since 1.0.0
 */
public record UpdateUserCommand(String userId, String phoneNumber) {}
