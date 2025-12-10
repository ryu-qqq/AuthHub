package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.application.user.port.out.persistence.UserPersistencePort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import org.springframework.stereotype.Component;

/**
 * UserCommandAdapter - 사용자 Command Adapter (CUD 전용)
 *
 * <p>UserPersistencePort 구현체로서 사용자 저장 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>UserJpaRepository (1개) + UserJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>persist() - 사용자 저장 (생성/수정)
 *   <li>delete() - 사용자 삭제
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserCommandAdapter implements UserPersistencePort {

    private final UserJpaRepository repository;
    private final UserJpaEntityMapper mapper;

    public UserCommandAdapter(UserJpaRepository repository, UserJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * 사용자 저장 (생성/수정)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>Domain → Entity 변환 (Mapper)
     *   <li>Entity 저장 (JpaRepository)
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param user 저장할 사용자 도메인
     * @return 저장된 사용자 도메인 (ID 할당됨)
     */
    @Override
    public User persist(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        UserJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    /**
     * 사용자 삭제
     *
     * <p><strong>참고:</strong> 실제로는 Soft Delete를 사용하므로 이 메서드는 persist()를 통해 status를 DELETED로 변경하는
     * 방식을 권장합니다.
     *
     * @param user 삭제할 사용자 도메인
     */
    @Override
    public void delete(User user) {
        UserJpaEntity entity = mapper.toEntity(user);
        repository.delete(entity);
    }
}
