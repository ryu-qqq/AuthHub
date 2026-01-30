package com.ryuqq.authhub.application.token.manager.query;

import com.ryuqq.authhub.application.token.dto.composite.UserContextComposite;
import com.ryuqq.authhub.application.token.port.out.query.UserContextCompositeQueryPort;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserContextCompositeReadManager - 사용자 컨텍스트 Composite 조회 Manager
 *
 * <p>User, Organization, Tenant를 조인하여 사용자 컨텍스트를 조회합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>메서드 단위 @Transactional(readOnly=true)
 *   <li>QueryPort 1:1 래핑
 *   <li>조인 조회로 N+1 방지
 *   <li>조회 실패 시 UserNotFoundException 발생
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserContextCompositeReadManager {

    private final UserContextCompositeQueryPort userContextCompositeQueryPort;

    public UserContextCompositeReadManager(
            UserContextCompositeQueryPort userContextCompositeQueryPort) {
        this.userContextCompositeQueryPort = userContextCompositeQueryPort;
    }

    /**
     * 사용자 ID로 사용자 컨텍스트 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 컨텍스트
     * @throws UserNotFoundException 사용자가 존재하지 않을 경우
     */
    @Transactional(readOnly = true)
    public UserContextComposite getUserContextByUserId(UserId userId) {
        return userContextCompositeQueryPort
                .findUserContextByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
