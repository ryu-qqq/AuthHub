package com.ryuqq.authhub.adapter.out.persistence.token.adapter;

import com.ryuqq.authhub.adapter.out.persistence.token.mapper.UserContextCompositeMapper;
import com.ryuqq.authhub.adapter.out.persistence.token.repository.UserContextCompositeQueryDslRepository;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.port.out.query.UserContextCompositeQueryPort;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserContextCompositeQueryAdapter - 사용자 컨텍스트 Composite 조회 Adapter
 *
 * <p>UserContextCompositeQueryPort 구현체입니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>Repository (1개) + Mapper (1개)
 *   <li>필드 2개만 허용
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>Repository 조회 결과를 Mapper로 Application DTO 변환
 *   <li>도메인 VO와 Persistence 타입 간 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserContextCompositeQueryAdapter implements UserContextCompositeQueryPort {

    private final UserContextCompositeQueryDslRepository repository;
    private final UserContextCompositeMapper mapper;

    public UserContextCompositeQueryAdapter(
            UserContextCompositeQueryDslRepository repository, UserContextCompositeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<UserContextComposite> findUserContextByUserId(UserId userId) {
        return repository.findUserContextByUserId(userId.value()).map(mapper::toComposite);
    }
}
