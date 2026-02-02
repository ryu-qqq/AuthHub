package com.ryuqq.authhub.sdk.model.internal;

import java.util.List;

/**
 * 공개키 목록 모델 (RFC 7517 JWKS 형식).
 *
 * <p>Gateway가 JWT 서명 검증을 위해 사용합니다.
 *
 * @param keys 공개키 목록
 */
public record PublicKeys(List<PublicKey> keys) {}
