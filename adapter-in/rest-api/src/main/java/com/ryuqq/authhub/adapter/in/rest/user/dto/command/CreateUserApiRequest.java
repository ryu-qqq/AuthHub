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
        @Schema(description = "테넌트 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "테넌트 ID는 필수입니다")
                String tenantId,
        @Schema(description = "조직 ID", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "조직 ID는 필수입니다")
                String organizationId,
        @Schema(
                        description = "사용자 식별자 (이메일 또는 사용자명)",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 1,
                        maxLength = 255)
                @NotBlank(message = "사용자 식별자는 필수입니다")
                @Size(min = 1, max = 255, message = "사용자 식별자는 1자 이상 255자 이하여야 합니다")
                String identifier,
        @Schema(
                        description = "핸드폰 번호 (한국 형식)",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 10,
                        maxLength = 20)
                @NotBlank(message = "핸드폰 번호는 필수입니다")
                @Size(min = 10, max = 20, message = "핸드폰 번호는 10자 이상 20자 이하여야 합니다")
                String phoneNumber,
        @Schema(
                        description = "비밀번호",
                        requiredMode = Schema.RequiredMode.REQUIRED,
                        minLength = 8,
                        maxLength = 100)
                @NotBlank(message = "비밀번호는 필수입니다")
                @Size(min = 8, max = 100, message = "비밀번호는 8자 이상 100자 이하여야 합니다")
                String password) {}
