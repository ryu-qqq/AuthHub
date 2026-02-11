package com.ryuqq.authhub.sdk.model.internal;

import java.util.List;

/**
 * 사용자 컨텍스트 모델.
 *
 * <p>내부 서비스가 사용자의 전체 컨텍스트 정보를 조회할 때 사용합니다.
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 * @param email 이메일
 * @param name 사용자 이름
 * @param phoneNumber 전화번호 (null 가능)
 * @param tenant 테넌트 정보
 * @param organization 조직 정보
 * @param roles 역할 목록
 * @param permissions 권한 키 목록
 */
public record UserContext(
        String userId,
        String email,
        String name,
        String phoneNumber,
        TenantInfo tenant,
        OrganizationInfo organization,
        List<RoleInfo> roles,
        List<String> permissions) {

    /**
     * 테넌트 정보
     *
     * @param id 테넌트 ID
     * @param name 테넌트 이름
     */
    public record TenantInfo(String id, String name) {}

    /**
     * 조직 정보
     *
     * @param id 조직 ID
     * @param name 조직 이름
     */
    public record OrganizationInfo(String id, String name) {}

    /**
     * 역할 정보
     *
     * @param id 역할 ID
     * @param name 역할 이름
     */
    public record RoleInfo(String id, String name) {}
}
