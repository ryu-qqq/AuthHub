package com.ryuqq.authhub.sdk.model.user;

/**
 * 사용자 생성 + 역할 할당 응답.
 *
 * @param userId 생성된 사용자 ID (UUIDv7)
 * @param assignedRoleCount 할당된 역할 수
 */
public record CreateUserWithRolesResponse(String userId, int assignedRoleCount) {}
