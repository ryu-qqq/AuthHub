package com.ryuqq.authhub.adapter.in.rest.internal.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * CreateUserWithRolesApiRequest - 사용자 생성 + 역할 할당 API 요청 DTO
 *
 * <p>사용자 생성과 SERVICE scope Role 자동 할당을 한 번에 요청합니다.
 *
 * @param organizationId 소속 조직 ID (필수)
 * @param identifier 로그인 식별자 (필수, 이메일 또는 사용자명)
 * @param phoneNumber 전화번호 (선택)
 * @param password 비밀번호 (필수)
 * @param serviceCode 서비스 코드 (선택, 예: SVC_STORE)
 * @param roleNames 역할 이름 목록 (선택, 예: ["ADMIN"])
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 등록 요청 (생성 + 역할 할당)")
public record CreateUserWithRolesApiRequest(
        @Schema(description = "소속 조직 ID", example = "550e8400-e29b-41d4-a716-446655440000")
                @NotBlank(message = "조직 ID는 필수입니다")
                String organizationId,
        @Schema(description = "로그인 식별자 (이메일)", example = "admin@setof.com")
                @NotBlank(message = "식별자는 필수입니다")
                String identifier,
        @Schema(description = "전화번호", example = "010-1234-5678") String phoneNumber,
        @Schema(description = "비밀번호", example = "SecurePassword123!")
                @NotBlank(message = "비밀번호는 필수입니다")
                String password,
        @Schema(description = "서비스 코드", example = "SVC_STORE") String serviceCode,
        @Schema(description = "역할 이름 목록", example = "[\"ADMIN\"]") List<String> roleNames) {}
