package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.token.TokenId;

/**
 * CheckBlacklist Port Interface.
 *
 * <p>Token이 Blacklist에 등록되었는지 확인하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Persistence Layer(Redis)에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/auth/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-persistence/auth/adapter/} (Persistence Layer - Redis)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그아웃 시 Token을 Blacklist에 등록하여 재사용 방지</li>
 *   <li>Refresh Token 재발급 시 Blacklist 확인</li>
 *   <li>보안 침해 시 특정 Token을 강제로 무효화</li>
 * </ul>
 *
 * <p><strong>Blacklist 관리 전략:</strong></p>
 * <ul>
 *   <li>Redis SET 자료구조 사용 (O(1) 조회)</li>
 *   <li>TTL(Time To Live)을 Token 만료 시각까지 설정 (자동 삭제)</li>
 *   <li>로그아웃된 Token은 만료 시각까지 Blacklist에 유지</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Redis는 내부 시스템 (DB와 동일한 내부 I/O)</li>
 *   <li>✅ @Transactional 내부에서 호출 가능 (외부 API 아님)</li>
 *   <li>✅ S3, SQS, HTTP Client 같은 외부 API와 구분</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckBlacklistPort {

    /**
     * TokenId가 Blacklist에 등록되어 있는지 확인합니다.
     *
     * <p>Blacklist에 등록된 Token은 로그아웃되었거나 강제로 무효화된 Token입니다.
     * Redis SET에서 TokenId 존재 여부를 확인하여 {@code true/false}를 반환합니다.</p>
     *
     * <p><strong>트랜잭션 경계:</strong></p>
     * <ul>
     *   <li>이 메서드는 @Transactional 내부에서 호출 가능합니다.</li>
     *   <li>Redis는 내부 시스템으로, MySQL과 동일하게 취급합니다.</li>
     *   <li>외부 API (S3, HTTP, SQS)와 구분됩니다.</li>
     * </ul>
     *
     * <p><strong>반환값:</strong></p>
     * <ul>
     *   <li>{@code true}: Blacklist에 등록됨 (Token 사용 불가)</li>
     *   <li>{@code false}: Blacklist에 없음 (Token 사용 가능)</li>
     * </ul>
     *
     * @param tokenId 확인할 Token의 고유 식별자 (null 불가)
     * @return Blacklist에 등록되어 있으면 {@code true}, 없으면 {@code false}
     * @throws IllegalArgumentException tokenId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean isBlacklisted(TokenId tokenId);
}
