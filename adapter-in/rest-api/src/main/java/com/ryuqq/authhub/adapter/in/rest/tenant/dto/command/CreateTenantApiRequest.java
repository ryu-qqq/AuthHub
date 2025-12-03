package com.ryuqq.authhub.adapter.in.rest.tenant.dto.command;

import jakarta.validation.constraints.NotBlank;

/**
 * 테넌트 생성 API 요청 DTO
 *
 * <p>REST API로 테넌트 생성 요청을 받을 때 사용되는 불변 DTO입니다.
 *
 * <p><strong>Validation 규칙:</strong>
 * <ul>
 *   <li>name: 필수, 빈 문자열 불가 (NotBlank)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantApiRequest(
        @NotBlank(message = "테넌트 이름은 필수입니다")
        String name
) {}
