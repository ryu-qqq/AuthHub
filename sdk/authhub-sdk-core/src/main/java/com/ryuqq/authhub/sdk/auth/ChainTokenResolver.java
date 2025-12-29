package com.ryuqq.authhub.sdk.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 여러 TokenResolver를 순차적으로 시도하는 체인 TokenResolver. 첫 번째로 토큰을 반환하는 resolver의 결과를 사용합니다.
 *
 * <p>일반적인 사용 패턴:
 *
 * <pre>
 * ChainTokenResolver.withFallback("service-token")
 * // ThreadLocal 우선 → 없으면 서비스 토큰 사용
 * </pre>
 */
public final class ChainTokenResolver implements TokenResolver {

    private final List<TokenResolver> resolvers;

    private ChainTokenResolver(List<TokenResolver> resolvers) {
        this.resolvers = List.copyOf(resolvers);
    }

    /**
     * 주어진 resolver들을 순차적으로 시도하는 ChainTokenResolver를 생성합니다.
     *
     * @param resolvers 토큰 resolver 목록
     * @return ChainTokenResolver
     */
    public static ChainTokenResolver of(TokenResolver... resolvers) {
        Objects.requireNonNull(resolvers, "resolvers must not be null");
        return new ChainTokenResolver(Arrays.asList(resolvers));
    }

    /**
     * ThreadLocal을 우선 시도하고, 없으면 주어진 서비스 토큰을 사용하는 ChainTokenResolver를 생성합니다.
     *
     * @param serviceToken 폴백으로 사용할 서비스 토큰
     * @return ChainTokenResolver
     */
    public static ChainTokenResolver withFallback(String serviceToken) {
        Objects.requireNonNull(serviceToken, "serviceToken must not be null");
        return of(ThreadLocalTokenResolver.INSTANCE, new StaticTokenResolver(serviceToken));
    }

    @Override
    public Optional<String> resolve() {
        for (TokenResolver resolver : resolvers) {
            Optional<String> token = resolver.resolve();
            if (token.isPresent()) {
                return token;
            }
        }
        return Optional.empty();
    }
}
