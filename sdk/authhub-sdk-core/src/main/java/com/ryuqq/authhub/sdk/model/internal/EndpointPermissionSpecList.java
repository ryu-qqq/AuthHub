package com.ryuqq.authhub.sdk.model.internal;

import java.time.Instant;
import java.util.List;

/**
 * 엔드포인트-권한 스펙 목록 모델.
 *
 * <p>Gateway가 전체 스펙을 캐싱하기 위해 사용합니다.
 *
 * @param version 스펙 버전 (ETag용, epoch millis)
 * @param updatedAt 마지막 수정 시간
 * @param endpoints 엔드포인트-권한 매핑 목록
 */
public record EndpointPermissionSpecList(
        String version, Instant updatedAt, List<EndpointPermissionSpec> endpoints) {}
