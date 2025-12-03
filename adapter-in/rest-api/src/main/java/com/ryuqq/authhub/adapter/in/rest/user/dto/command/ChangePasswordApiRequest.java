package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import jakarta.validation.constraints.NotBlank;

/**
 * 비밀번호 변경 API 요청 DTO
 *
 * <p>비밀번호 변경 또는 재설정 요청 데이터를 전달합니다.
 *
 * <p><strong>비밀번호 변경 시나리오:</strong>
 * <ul>
 *   <li>일반 변경 (verified=false): currentPassword 필수</li>
 *   <li>재설정 (verified=true): currentPassword 불필요 (이메일/SMS 인증 완료)</li>
 * </ul>
 *
 * @param currentPassword 현재 비밀번호 (일반 변경 시 필수)
 * @param newPassword 새 비밀번호 (필수)
 * @param verified 본인 인증 완료 여부
 * @author development-team
 * @since 1.0.0
 */
public record ChangePasswordApiRequest(
        String currentPassword,

        @NotBlank(message = "새 비밀번호는 필수입니다")
        String newPassword,

        boolean verified
) {}
