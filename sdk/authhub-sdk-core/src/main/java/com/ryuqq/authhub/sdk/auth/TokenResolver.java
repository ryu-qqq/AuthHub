package com.ryuqq.authhub.sdk.auth;

import java.util.Optional;

/** AuthHub API 호출 시 사용할 인증 토큰을 제공하는 인터페이스. 다양한 토큰 소스(ThreadLocal, 환경변수, 설정 파일 등)를 추상화합니다. */
@FunctionalInterface
public interface TokenResolver {

    /**
     * 현재 컨텍스트에서 인증 토큰을 조회합니다.
     *
     * @return 인증 토큰 (없는 경우 Optional.empty())
     */
    Optional<String> resolve();
}
