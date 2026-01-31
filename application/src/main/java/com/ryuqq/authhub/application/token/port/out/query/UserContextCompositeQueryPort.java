package com.ryuqq.authhub.application.token.port.out.query;

import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.util.Optional;

/**
 * UserContextCompositeQueryPort - 사용자 컨텍스트 Composite 조회 Port
 *
 * <p>User, Organization, Tenant를 조인하여 사용자 컨텍스트를 조회합니다.
 *
 * <p><strong>조회 방식:</strong>
 *
 * <ul>
 *   <li>User → Organization → Tenant 조인 조회
 *   <li>단일 쿼리로 모든 정보 조회 (N+1 방지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserContextCompositeQueryPort {

    /**
     * 사용자 ID로 사용자 컨텍스트 조회 (조인)
     *
     * <p>User, Organization, Tenant를 조인하여 한 번에 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 컨텍스트 (없으면 empty)
     */
    Optional<UserContextComposite> findUserContextByUserId(UserId userId);
}
