package com.ryuqq.authhub.sdk.auth;

import java.util.Objects;
import java.util.Optional;

/** 고정된 서비스 토큰을 제공하는 TokenResolver. 서버 간 통신(M2M)이나 배치 작업에서 사용합니다. */
public final class StaticTokenResolver implements TokenResolver {

    private final String token;

    public StaticTokenResolver(String token) {
        this.token = Objects.requireNonNull(token, "token must not be null");
    }

    @Override
    public Optional<String> resolve() {
        return Optional.of(token);
    }
}
