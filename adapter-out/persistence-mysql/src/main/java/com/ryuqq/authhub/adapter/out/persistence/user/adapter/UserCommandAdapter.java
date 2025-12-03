package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import org.springframework.stereotype.Component;

/**
 * UserCommandAdapter - User Command Adapter
 *
 * <p>UserPersistencePort 구현체입니다. 신규 저장 및 수정 작업을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지 (UseCase에서 관리)
 *   <li>persist() 메서드만 제공
 *   <li>비즈니스 로직 포함 금지
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserCommandAdapter implements UserPersistencePort {

    private final UserJpaRepository userJpaRepository;
    private final UserJpaEntityMapper userJpaEntityMapper;

    public UserCommandAdapter(
            UserJpaRepository userJpaRepository, UserJpaEntityMapper userJpaEntityMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userJpaEntityMapper = userJpaEntityMapper;
    }

    /**
     * User 저장 (신규 및 수정)
     *
     * <p>JPA의 save() 메서드는 ID 유무에 따라 INSERT/UPDATE를 자동 결정합니다.
     *
     * @param user 저장할 User Domain 객체
     * @return 저장된 User의 ID
     */
    @Override
    public UserId persist(User user) {
        UserJpaEntity entity = userJpaEntityMapper.toEntity(user);
        UserJpaEntity savedEntity = userJpaRepository.save(entity);
        return UserId.of(savedEntity.getId());
    }
}
