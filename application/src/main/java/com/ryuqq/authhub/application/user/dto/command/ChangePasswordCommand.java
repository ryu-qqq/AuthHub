package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * ChangePasswordCommand - 비밀번호 변경 요청 데이터
 *
 * <p>비밀번호 변경에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>순수 Java Record (Lombok 금지)</li>
 *   <li>jakarta.validation 금지</li>
 *   <li>비즈니스 로직/검증 금지 (데이터 전달만)</li>
 * </ul>
 *
 * @param userId 사용자 ID (필수)
 * @param currentPassword 현재 비밀번호 (필수)
 * @param newPassword 새 비밀번호 (필수)
 *
 * @author development-team
 * @since 1.0.0
 */
public record ChangePasswordCommand(
        UUID userId,
        String currentPassword,
        String newPassword
) {
}
