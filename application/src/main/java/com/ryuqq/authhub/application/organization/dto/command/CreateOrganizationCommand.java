package com.ryuqq.authhub.application.organization.dto.command;

/**
 * Create Organization Command DTO
 *
 * <p>조직 생성 요청 데이터를 전달하는 명령 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateOrganizationCommand(Long tenantId, String name) {}
