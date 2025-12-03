package com.ryuqq.authhub.adapter.in.rest.organization.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 조직 생성 API 요청 DTO
 *
 * <p>REST API로 조직 생성 요청을 받을 때 사용되는 불변 DTO입니다.
 *
 * <p><strong>Validation 규칙:</strong>
 * <ul>
 *   <li>tenantId: 필수 (NotNull)</li>
 *   <li>name: 필수, 빈 문자열 불가 (NotBlank)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateOrganizationApiRequest(
        @NotNull(message = "테넌트 ID는 필수입니다")
        Long tenantId,

        @NotBlank(message = "조직 이름은 필수입니다")
        String name
) {}
