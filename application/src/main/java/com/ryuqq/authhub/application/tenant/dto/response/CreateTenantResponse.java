package com.ryuqq.authhub.application.tenant.dto.response;

/**
 * CreateTenantResponse - 테넌트 생성 응답 DTO
 *
 * <p>테넌트 생성 후 반환되는 응답 DTO입니다.
 *
 * @param tenantId 생성된 테넌트 ID
 * @author development-team
 * @since 1.0.0
 */
public record CreateTenantResponse(Long tenantId) {}
