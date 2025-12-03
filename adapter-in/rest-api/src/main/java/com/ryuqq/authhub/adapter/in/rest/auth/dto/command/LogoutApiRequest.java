package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * 로그아웃 API 요청 DTO
 *
 * <p>사용자 로그아웃 요청 데이터를 전달합니다.
 *
 * @param userId 사용자 ID
 * @author development-team
 * @since 1.0.0
 */
public record LogoutApiRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        UUID userId
) {}
