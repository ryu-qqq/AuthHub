package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * ChangeUserStatusCommand - 사용자 상태 변경 요청 데이터
 *
 * <p>사용자 상태 변경에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (Lombok 금지)
 *   <li>jakarta.validation 금지
 *   <li>비즈니스 로직/검증 금지 (데이터 전달만)
 * </ul>
 *
 * @param userId 사용자 ID (필수)
 * @param targetStatus 변경할 상태 (ACTIVE, INACTIVE, SUSPENDED, DELETED)
 * @param reason 상태 변경 사유 (선택)
 * @author development-team
 * @since 1.0.0
 */
public record ChangeUserStatusCommand(UUID userId, String targetStatus, String reason) {}
