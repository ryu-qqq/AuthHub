package com.ryuqq.authhub.application.user.dto.command;

import java.util.UUID;

/**
 * ChangePasswordCommand - 비밀번호 변경 요청 데이터
 *
 * <p>비밀번호 변경에 필요한 데이터를 전달하는 불변 Command DTO입니다.
 *
 * <p><strong>비밀번호 변경 시나리오:</strong>
 *
 * <ul>
 *   <li>일반 변경 (verified=false): 현재 비밀번호 검증 필요
 *   <li>재설정 (verified=true): 이메일/SMS 등으로 본인 인증됨, 현재 비밀번호 검증 불필요
 * </ul>
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
 * @param currentPassword 현재 비밀번호 (verified=false인 경우 필수)
 * @param newPassword 새 비밀번호 (필수)
 * @param verified 본인 인증 완료 여부 (true: 비밀번호 재설정, false: 일반 변경)
 * @author development-team
 * @since 1.0.0
 */
public record ChangePasswordCommand(
        UUID userId, String currentPassword, String newPassword, boolean verified) {

    /**
     * 일반 비밀번호 변경용 팩토리 메서드
     *
     * @param userId 사용자 ID
     * @param currentPassword 현재 비밀번호
     * @param newPassword 새 비밀번호
     * @return ChangePasswordCommand (verified=false)
     */
    public static ChangePasswordCommand forChange(
            UUID userId, String currentPassword, String newPassword) {
        return new ChangePasswordCommand(userId, currentPassword, newPassword, false);
    }

    /**
     * 비밀번호 재설정용 팩토리 메서드 (본인 인증 완료된 경우)
     *
     * @param userId 사용자 ID
     * @param newPassword 새 비밀번호
     * @return ChangePasswordCommand (verified=true, currentPassword=null)
     */
    public static ChangePasswordCommand forReset(UUID userId, String newPassword) {
        return new ChangePasswordCommand(userId, null, newPassword, true);
    }
}
