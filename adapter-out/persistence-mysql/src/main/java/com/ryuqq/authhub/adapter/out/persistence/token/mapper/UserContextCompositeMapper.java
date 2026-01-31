package com.ryuqq.authhub.adapter.out.persistence.token.mapper;

import com.ryuqq.authhub.adapter.out.persistence.token.dto.UserContextProjection;
import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import org.springframework.stereotype.Component;

/**
 * UserContextCompositeMapper - Projection → Application DTO 변환 Mapper
 *
 * <p>Persistence Layer의 Projection을 Application Layer의 Composite DTO로 변환합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>UserContextProjection → UserContextComposite
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserContextCompositeMapper {

    /**
     * Projection → Composite 변환
     *
     * @param projection 조인 조회 결과
     * @return Application layer Composite DTO
     */
    public UserContextComposite toComposite(UserContextProjection projection) {
        return UserContextComposite.builder()
                .userId(projection.userId())
                .email(projection.email())
                .name(projection.name())
                .tenantId(projection.tenantId())
                .tenantName(projection.tenantName())
                .organizationId(projection.organizationId())
                .organizationName(projection.organizationName())
                .build();
    }
}
