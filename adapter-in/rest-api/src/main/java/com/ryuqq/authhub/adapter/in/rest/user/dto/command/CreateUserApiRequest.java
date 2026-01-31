package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * CreateUserApiRequest - 사용자 생성 요청 DTO
 *
 * <p>사용자 등록 API의 요청 본문을 표현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Bean Validation 어노테이션 사용
 *   <li>Lombok 금지
 *   <li>Jackson 어노테이션 금지
 *   <li>Domain 변환 메서드 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 생성 요청")
public record CreateUserApiRequest(
        @Schema(description = "소속 조직 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "조직 ID는 필수입니다")
                String organizationId,
        @Schema(
                        description = "로그인 식별자 (이메일 또는 사용자명)",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 4,
                        maxLength = 100)
                @NotBlank(message = "식별자는 필수입니다")
                @Size(min = 4, max = 100, message = "식별자는 4자 이상 100자 이하여야 합니다")
                String identifier,
        @Schema(description = "전화번호", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
                String phoneNumber,
        @Schema(
                        description = "비밀번호",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 8,
                        maxLength = 100)
                @NotBlank(message = "비밀번호는 필수입니다")
                @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
                String password) {}
