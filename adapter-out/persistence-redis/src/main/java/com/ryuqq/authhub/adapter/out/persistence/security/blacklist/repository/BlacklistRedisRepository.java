package com.ryuqq.authhub.adapter.out.persistence.security.blacklist.repository;

import com.ryuqq.authhub.adapter.out.persistence.security.blacklist.entity.BlacklistedTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Blacklist Redis Repository.
 *
 * <p>Spring Data Redis {@link CrudRepository}를 상속하여
 * 블랙리스트 토큰에 대한 기본 CRUD 연산을 제공합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>블랙리스트 토큰을 Redis Hash로 저장</li>
 *   <li>JTI를 기준으로 블랙리스트 토큰 조회</li>
 *   <li>JTI 존재 여부 확인 (existsById)</li>
 *   <li>블랙리스트 토큰 삭제</li>
 * </ul>
 *
 * <p><strong>Redis Key 패턴:</strong></p>
 * <ul>
 *   <li>Redis Key: {@code blacklist_token:{jti}}</li>
 *   <li>예시: {@code blacklist_token:550e8400-e29b-41d4-a716-446655440000}</li>
 * </ul>
 *
 * <p><strong>Spring Data Redis 기본 제공 메서드:</strong></p>
 * <ul>
 *   <li>{@code save(entity)} - 엔티티 저장 (O(1))</li>
 *   <li>{@code findById(jti)} - JTI로 조회 (O(1))</li>
 *   <li>{@code existsById(jti)} - JTI 존재 확인 (O(1))</li>
 *   <li>{@code deleteById(jti)} - JTI로 삭제 (O(1))</li>
 *   <li>{@code count()} - 전체 개수 조회 (O(N))</li>
 *   <li>{@code findAll()} - 전체 조회 (O(N))</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Spring Data Repository 패턴 - 기본 CRUD 메서드만 사용</li>
 *   <li>✅ Lombok 금지 - Spring Data가 자동 생성</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ 단순성 유지 - 복잡한 쿼리는 Adapter에서 처리</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 저장
 * BlacklistedTokenRedisEntity entity = BlacklistedTokenRedisEntity.create(...);
 * repository.save(entity);
 *
 * // 조회
 * Optional&lt;BlacklistedTokenRedisEntity&gt; found = repository.findById("jti-123");
 *
 * // 존재 확인
 * boolean exists = repository.existsById("jti-123");
 *
 * // 삭제
 * repository.deleteById("jti-123");
 * </pre>
 *
 * <p><strong>주의사항:</strong></p>
 * <ul>
 *   <li>Redis SET/ZSET 연산은 Adapter에서 {@link org.springframework.data.redis.core.RedisTemplate}로 처리</li>
 *   <li>이 Repository는 Hash 저장용으로만 사용</li>
 *   <li>TTL은 {@link org.springframework.data.redis.core.TimeToLive}로 자동 관리</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface BlacklistRedisRepository extends CrudRepository<BlacklistedTokenRedisEntity, String> {
    // Spring Data Redis가 기본 CRUD 메서드를 자동 생성
    // 추가 쿼리 메서드가 필요하면 여기에 정의
}
