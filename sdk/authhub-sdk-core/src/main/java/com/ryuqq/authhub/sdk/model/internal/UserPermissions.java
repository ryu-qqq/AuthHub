package com.ryuqq.authhub.sdk.model.internal;

import java.util.Set;

/**
 * 사용자 권한 모델.
 *
 * <p>Gateway가 사용자 인가 검증을 위해 사용합니다.
 *
 * @param userId 사용자 ID
 * @param roles 역할 이름 Set
 * @param permissions 권한 키 Set
 */
public record UserPermissions(String userId, Set<String> roles, Set<String> permissions) {}
