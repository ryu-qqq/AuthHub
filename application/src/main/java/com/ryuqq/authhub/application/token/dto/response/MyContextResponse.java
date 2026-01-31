package com.ryuqq.authhub.application.token.dto.response;

import java.util.List;

/**
 * MyContextResponse - 내 컨텍스트 조회 Response DTO
 *
 * <p>현재 로그인한 사용자의 전체 컨텍스트 정보입니다.
 *
 * @param userId 사용자 ID (UUIDv7 String)
 * @param email 이메일
 * @param name 사용자 이름
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @param roles 역할 목록
 * @param permissions 권한 키 목록
 * @author development-team
 * @since 1.0.0
 */
public record MyContextResponse(
        String userId,
        String email,
        String name,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName,
        List<RoleInfo> roles,
        List<String> permissions) {

    /**
     * 역할 정보
     *
     * @param id 역할 ID
     * @param name 역할 이름
     */
    public record RoleInfo(String id, String name) {}
}
