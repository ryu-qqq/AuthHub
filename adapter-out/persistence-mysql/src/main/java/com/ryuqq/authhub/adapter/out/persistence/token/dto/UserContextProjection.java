package com.ryuqq.authhub.adapter.out.persistence.token.dto;

/**
 * UserContextProjection - 사용자 컨텍스트 조인 조회 결과 Projection
 *
 * <p>User, Organization, Tenant 조인 쿼리 결과를 담는 DTO입니다.
 *
 * @param userId 사용자 ID
 * @param email 이메일 (identifier)
 * @param name 사용자 이름 (현재는 identifier와 동일)
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @author development-team
 * @since 1.0.0
 */
public record UserContextProjection(
        String userId,
        String email,
        String name,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName) {}
