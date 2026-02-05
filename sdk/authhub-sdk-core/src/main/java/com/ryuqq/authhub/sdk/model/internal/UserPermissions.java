package com.ryuqq.authhub.sdk.model.internal;

import java.time.Instant;
import java.util.Set;

/**
 * 사용자 권한 모델.
 *
 * <p>Gateway가 사용자 인가 검증을 위해 사용합니다.
 *
 * @param userId 사용자 ID
 * @param roles 역할 이름 Set
 * @param permissions 권한 키 Set
 * @param hash 권한 해시 (변경 감지용)
 * @param generatedAt 권한 생성 시점
 */
public record UserPermissions(
        String userId,
        Set<String> roles,
        Set<String> permissions,
        String hash,
        Instant generatedAt) {}
