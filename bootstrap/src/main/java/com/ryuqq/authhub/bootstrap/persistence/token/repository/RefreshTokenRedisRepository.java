package com.ryuqq.authhub.bootstrap.persistence.token.repository;

import com.ryuqq.authhub.bootstrap.persistence.token.entity.RefreshTokenRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Refresh Token Redis Repository.
 *
 * <p>Redis에 저장된 Refresh Token Entity를 관리하는 Repository 인터페이스입니다.
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
 * <p><strong>커스텀 메서드:</strong></p>
 * <ul>
 *   <li>{@code findByUserId(String userId)}: 사용자 ID로 Refresh Token 조회</li>
 * </ul>
 *
 * <p><strong>Redis Key 패턴:</strong></p>
 * <ul>
 *   <li>Primary Key: {@code refresh_token:{tokenId}}</li>
 *   <li>Index Key: {@code refresh_token:userId:{userId}}</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Repository는 인터페이스만 정의, 구현체는 Spring Data가 자동 생성</li>
 *   <li>✅ Javadoc 완비 - 인터페이스 및 메서드 설명 포함</li>
 *   <li>✅ @Repository 어노테이션으로 Spring Bean 명시</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>{@code
 * // Entity 저장
 * RefreshTokenRedisEntity entity = RefreshTokenRedisEntity.create(
 *     tokenId.asString(),
 *     userId.asString(),
 *     jwtToken,
 *     issuedAtMillis,
 *     expiresAtMillis
 * );
 * repository.save(entity);
 *
 * // ID로 조회
 * Optional<RefreshTokenRedisEntity> found = repository.findById(tokenId.asString());
 *
 * // 사용자 ID로 조회
 * Optional<RefreshTokenRedisEntity> byUser = repository.findByUserId(userId.asString());
 *
 * // 삭제
 * repository.deleteById(tokenId.asString());
 * }</pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshTokenRedisEntity, String> {

    /**
     * 사용자 ID로 Refresh Token Entity를 조회합니다.
     *
     * <p>사용자별로 하나의 Refresh Token만 존재해야 하므로, 이 메서드는 최대 1개의 결과를 반환합니다.
     * {@code @Indexed} 어노테이션이 userId 필드에 설정되어 있어 효율적인 조회가 가능합니다.</p>
     *
     * <p><strong>Redis Index 사용:</strong></p>
     * <ul>
     *   <li>Index Key: {@code refresh_token:userId:{userId}}</li>
     *   <li>O(1) 시간 복잡도로 조회 가능</li>
     * </ul>
     *
     * <p><strong>사용 시나리오:</strong></p>
     * <ul>
     *   <li>사용자 로그아웃 시 해당 사용자의 Refresh Token 삭제</li>
     *   <li>Refresh Token Rotation 전략에서 기존 토큰 확인</li>
     * </ul>
     *
     * @param userId 사용자 식별자 (UUID String) (null 불가)
     * @return Refresh Token Entity가 존재하면 {@code Optional<RefreshTokenRedisEntity>}, 아니면 {@code Optional.empty()}
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<RefreshTokenRedisEntity> findByUserId(String userId);
}
