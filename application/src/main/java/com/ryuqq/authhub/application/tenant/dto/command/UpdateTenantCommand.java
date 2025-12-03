package com.ryuqq.authhub.application.tenant.dto.command;

/**
 * UpdateTenantCommand - 테넌트 수정 커맨드 DTO
 *
 * <p>테넌트 수정에 필요한 정보를 전달하는 커맨드입니다.
 *
 * @param tenantId 수정할 테넌트 ID
 * @param name 새로운 테넌트명
 * @author development-team
 * @since 1.0.0
 */
public record UpdateTenantCommand(Long tenantId, String name) {}
