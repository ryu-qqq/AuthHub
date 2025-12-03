package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 사용자 생성 API 요청 DTO
 *
 * <p>새 사용자 생성 요청 데이터를 전달합니다.
 *
 * @param tenantId 테넌트 ID (필수)
 * @param organizationId 조직 ID (필수)
 * @param identifier 로그인 식별자 (이메일 또는 아이디) (필수)
 * @param password 비밀번호 (필수)
 * @param userType 사용자 유형 (PUBLIC, ADMIN 등) (선택)
 * @param name 이름 (선택)
 * @param phoneNumber 전화번호 (선택)
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserApiRequest(
        @NotNull(message = "테넌트 ID는 필수입니다")
        Long tenantId,

        @NotNull(message = "조직 ID는 필수입니다")
        Long organizationId,

        @NotBlank(message = "식별자는 필수입니다")
        String identifier,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password,

        String userType,

        String name,

        String phoneNumber
) {}
