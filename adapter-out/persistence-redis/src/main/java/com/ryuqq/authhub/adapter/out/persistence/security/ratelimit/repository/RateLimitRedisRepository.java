package com.ryuqq.authhub.adapter.out.persistence.security.ratelimit.repository;

import com.ryuqq.authhub.adapter.out.persistence.security.ratelimit.entity.RateLimitRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Rate Limit Redis Repository.
 *
 * <p>Redis에 저장된 Rate Limit Entity를 관리하는 Repository 인터페이스입니다.
 * Spring Data Redis의 {@link CrudRepository}를 상속받아 기본적인 CRUD 기능을 자동으로 제공받습니다.</p>
 *
 * <p><strong>제공되는 기본 메서드 (CrudRepository):</strong></p>
 * <ul>
 *   <li>{@code save(S entity)}: Redis에 Entity 저장 (Insert/Update)</li>
 *   <li>{@code findById(ID id)}: ID로 Entity 조회</li>
 *   <li>{@code existsById(ID id)}: ID로 Entity 존재 여부 확인</li>
 *   <li>{@code deleteById(ID id)}: ID로 Entity 삭제</li>
 *   <li>{@code count()}: 전체 Entity 개수</li>
 * </ul>
 *
 * <p><strong>Redis Key 패턴:</strong></p>
 * <ul>
 *   <li>Primary Key: {@code rate_limit:{type}:{identifier}:{endpoint}}</li>
 *   <li>IP 기반 예시: {@code rate_limit:ip:192.168.0.1:/api/auth/login}</li>
 *   <li>User 기반 예시: {@code rate_limit:user:uuid-1234:/api/users}</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Repository는 인터페이스만 정의, 구현체는 Spring Data가 자동 생성</li>
 *   <li>✅ Javadoc 완비 - 인터페이스 설명 포함</li>
 *   <li>✅ @Repository 어노테이션으로 Spring Bean 명시</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // Entity 저장
 * RateLimitRedisEntity entity = RateLimitRedisEntity.create(
 *     "rate_limit:ip:192.168.0.1:/api/login",
 *     5,
 *     System.currentTimeMillis()
 * );
 * repository.save(entity);
 *
 * // ID로 조회
 * Optional<RateLimitRedisEntity> found = repository.findById("rate_limit:ip:192.168.0.1:/api/login");
 *
 * // 삭제
 * repository.deleteById("rate_limit:ip:192.168.0.1:/api/login");
 * }</pre>
 *
 * <p><strong>주의사항:</strong></p>
 * <ul>
 *   <li>커스텀 메서드 없음 - 기본 CRUD만 사용</li>
 *   <li>INCR 명령어는 Adapter에서 RedisTemplate을 통해 직접 수행</li>
 *   <li>Key 생성 로직은 Adapter 계층에서 담당</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface RateLimitRedisRepository extends CrudRepository<RateLimitRedisEntity, String> {
    // Spring Data Redis가 기본 CRUD 메서드 구현 자동 제공
    // 추가 커스텀 메서드 불필요 - 모든 작업은 key 기반으로 수행됨
}
