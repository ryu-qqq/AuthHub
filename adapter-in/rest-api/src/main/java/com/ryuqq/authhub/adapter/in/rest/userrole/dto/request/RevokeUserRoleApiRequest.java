package com.ryuqq.authhub.adapter.in.rest.userrole.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * RevokeUserRoleApiRequest - 사용자 역할 철회 요청 DTO
 *
 * <p>사용자로부터 하나 이상의 역할을 철회하는 요청입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Record 타입 필수
 *   <li>*ApiRequest 네이밍 규칙
 *   <li>Bean Validation 어노테이션 필수
 *   <li>Lombok 금지
 * </ul>
 *
 * @param roleIds 철회할 역할 ID 목록 (필수, 1개 이상)
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "사용자 역할 철회 요청")
public record RevokeUserRoleApiRequest(
        @Schema(description = "철회할 역할 ID 목록", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "역할 ID 목록은 필수입니다")
                List<Long> roleIds) {}
