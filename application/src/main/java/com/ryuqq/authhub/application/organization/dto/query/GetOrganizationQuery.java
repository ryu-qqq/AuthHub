package com.ryuqq.authhub.application.organization.dto.query;

import java.util.UUID;

/**
 * GetOrganizationQuery - 조직 단건 조회 Query DTO
 *
 * @param organizationId 조직 ID
 * @author development-team
 * @since 1.0.0
 */
public record GetOrganizationQuery(UUID organizationId) {}
