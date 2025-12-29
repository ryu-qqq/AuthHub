package com.ryuqq.authhub.sdk.model.user;

/**
 * 사용자 상세 조회 시 포함되는 역할 요약 정보.
 *
 * @param roleId 역할 ID
 * @param name 역할 이름
 * @param description 역할 설명
 * @param scope 역할 범위 (GLOBAL, TENANT, ORGANIZATION)
 * @param type 역할 유형 (SYSTEM, CUSTOM)
 */
public record UserRoleSummaryResponse(
        String roleId, String name, String description, String scope, String type) {}
