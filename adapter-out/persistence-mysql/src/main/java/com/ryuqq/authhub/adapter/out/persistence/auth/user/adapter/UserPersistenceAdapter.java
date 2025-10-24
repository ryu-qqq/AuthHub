package com.ryuqq.authhub.adapter.out.persistence.auth.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.auth.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.auth.user.mapper.UserEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.auth.user.repository.UserJpaRepository;
import com.ryuqq.authhub.application.auth.port.out.LoadUserPort;
import com.ryuqq.authhub.application.auth.port.out.SaveUserPort;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * User Persistence Adapter.
 *
 * <p>User Aggregate를 위한 영속성 어댑터로, Application Port를 구현하여
 * Domain과 Persistence 계층 간의 Anti-Corruption Layer 역할을 수행합니다.</p>
 *
 * <p><strong>구현 Port:</strong></p>
 * <ul>
 *   <li>{@link LoadUserPort} - User 조회</li>
 *   <li>{@link SaveUserPort} - User 저장</li>
 * </ul>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain User ↔ JPA UserJpaEntity 변환 (Mapper 위임)</li>
 *   <li>JPA Repository를 통한 데이터베이스 접근</li>
 *   <li>Domain 모델의 순수성 보호 (Anti-Corruption)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Long FK 전략 - JPA 관계 어노테이션 사용하지 않음</li>
 *   <li>✅ Law of Demeter 준수 - Mapper에 변환 위임</li>
 *   <li>✅ Transaction 경계 준수 - {@code @Transactional}은 Application Service에서 관리</li>
 * </ul>
 *
 * <p><strong>Transaction 관리:</strong></p>
 * <ul>
 *   <li>Adapter는 {@code @Transactional}을 사용하지 않습니다</li>
 *   <li>Transaction 경계는 Application Layer의 UseCase에서 관리</li>
 *   <li>Adapter는 단순히 Repository 호출만 수행</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserPersistenceAdapter implements LoadUserPort, SaveUserPort {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    /**
     * UserPersistenceAdapter 생성자.
     *
     * <p>Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     * {@code @Autowired} 어노테이션은 생성자가 하나일 경우 생략 가능합니다.</p>
     *
     * @param userJpaRepository UserJpaRepository (null 불가)
     * @param userEntityMapper UserEntityMapper (null 불가)
     * @throws NullPointerException 인자 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserPersistenceAdapter(
            final UserJpaRepository userJpaRepository,
            final UserEntityMapper userEntityMapper
    ) {
        this.userJpaRepository = Objects.requireNonNull(userJpaRepository, "userJpaRepository cannot be null");
        this.userEntityMapper = Objects.requireNonNull(userEntityMapper, "userEntityMapper cannot be null");
    }

    /**
     * 사용자 ID로 User Aggregate를 조회합니다.
     *
     * <p>UserId의 UUID 값을 문자열로 변환하여 UserJpaRepository.findByUid()를 호출하고,
     * 조회된 UserJpaEntity를 UserEntityMapper를 통해 Domain User로 변환합니다.</p>
     *
     * <p><strong>동작 순서:</strong></p>
     * <ol>
     *   <li>UserId를 UUID 문자열로 변환</li>
     *   <li>UserJpaRepository.findByUid() 호출</li>
     *   <li>조회된 Entity를 UserEntityMapper.toDomain()으로 변환</li>
     *   <li>Optional로 감싸서 반환</li>
     * </ol>
     *
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @return Optional로 감싼 User (존재하지 않으면 Empty)
     * @throws IllegalArgumentException userId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public Optional<User> load(final UserId userId) {
        Objects.requireNonNull(userId, "UserId cannot be null");

        String uid = userId.value().toString();

        return userJpaRepository.findByUid(uid)
                .map(userEntityMapper::toDomain);
    }

    /**
     * User Aggregate를 영속화합니다.
     *
     * <p>Domain User를 UserEntityMapper를 통해 UserJpaEntity로 변환한 후,
     * UserJpaRepository.save()를 호출하여 데이터베이스에 저장합니다.
     * 저장된 Entity를 다시 Domain User로 변환하여 반환합니다.</p>
     *
     * <p><strong>동작 순서:</strong></p>
     * <ol>
     *   <li>uid로 기존 Entity 조회</li>
     *   <li>기존 Entity가 있으면 updateFrom()으로 업데이트 (JPA Dirty Checking)</li>
     *   <li>기존 Entity가 없으면 새로운 Entity 생성 후 INSERT</li>
     *   <li>저장된 Entity를 UserEntityMapper.toDomain()으로 변환</li>
     *   <li>Domain User 반환 (JPA ID 포함)</li>
     * </ol>
     *
     * <p><strong>UPDATE 전략:</strong></p>
     * <ul>
     *   <li>기존 Entity를 조회하여 updateFrom()으로 필드 업데이트</li>
     *   <li>JPA Dirty Checking이 자동으로 UPDATE 쿼리 생성</li>
     *   <li>명시적인 save() 호출 없이도 트랜잭션 커밋 시 자동 저장</li>
     *   <li>package-private setter를 활용하여 캡슐화 유지</li>
     * </ul>
     *
     * <p><strong>INSERT 전략:</strong></p>
     * <ul>
     *   <li>기존 Entity가 없으면 새로운 Entity 생성</li>
     *   <li>UserJpaRepository.save()로 명시적 INSERT</li>
     * </ul>
     *
     * @param user 저장할 Domain User (null 불가)
     * @return 영속화된 User (JPA ID 포함)
     * @throws IllegalArgumentException user가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public User save(final User user) {
        Objects.requireNonNull(user, "User cannot be null");

        final String uid = user.getId().asString();

        // 1. 기존 Entity 조회
        final Optional<UserJpaEntity> existingEntityOpt = userJpaRepository.findByUid(uid);

        final UserJpaEntity savedEntity;
        if (existingEntityOpt.isPresent()) {
            // 2. 기존 Entity가 있으면 updateFrom()으로 업데이트 (JPA Dirty Checking)
            final UserJpaEntity existingEntity = existingEntityOpt.get();
            final UserJpaEntity updatedEntity = userEntityMapper.toEntity(user);
            existingEntity.updateFrom(updatedEntity);
            savedEntity = existingEntity; // JPA가 자동으로 UPDATE 쿼리 생성
        } else {
            // 3. 기존 Entity가 없으면 새로운 Entity 생성 후 INSERT
            final UserJpaEntity newEntity = userEntityMapper.toEntity(user);
            savedEntity = userJpaRepository.save(newEntity);
        }

        return userEntityMapper.toDomain(savedEntity);
    }
}
