package com.ryuqq.authhub.sdk.auth;

import java.util.Optional;

/** ThreadLocal에서 토큰을 조회하는 TokenResolver. 요청별로 다른 사용자 토큰을 사용해야 하는 경우에 사용합니다. */
public final class ThreadLocalTokenResolver implements TokenResolver {

    public static final ThreadLocalTokenResolver INSTANCE = new ThreadLocalTokenResolver();

    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    private ThreadLocalTokenResolver() {}

    @Override
    public Optional<String> resolve() {
        return Optional.ofNullable(TOKEN_HOLDER.get());
    }

    /**
     * 현재 스레드에 토큰을 설정합니다.
     *
     * @param token 설정할 토큰
     */
    public static void set(String token) {
        TOKEN_HOLDER.set(token);
    }

    /** 현재 스레드의 토큰을 제거합니다. 요청 처리 완료 후 반드시 호출해야 메모리 누수를 방지할 수 있습니다. */
    public static void clear() {
        TOKEN_HOLDER.remove();
    }
}
