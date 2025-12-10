package com.ryuqq.authhub.application.user.manager.query;

import com.ryuqq.authhub.application.user.dto.query.SearchUsersQuery;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserReadManager - 사용자 조회 관리자
 *
 * <p>사용자 조회 작업을 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>{@code @Transactional(readOnly = true)} 클래스 레벨
 *   <li>Port-Out 의존
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class UserReadManager {

    private final UserQueryPort queryPort;

    public UserReadManager(UserQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 사용자 조회 (없으면 예외)
     *
     * @param userId 사용자 ID
     * @return 사용자
     * @throws UserNotFoundException 사용자가 없으면 발생
     */
    public User getById(UserId userId) {
        return queryPort
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.value()));
    }

    /**
     * 테넌트 ID와 식별자로 사용자 조회 (없으면 예외)
     *
     * @param tenantId 테넌트 ID
     * @param identifier 사용자 식별자
     * @return 사용자
     * @throws UserNotFoundException 사용자가 없으면 발생
     */
    public User getByTenantIdAndIdentifier(TenantId tenantId, String identifier) {
        return queryPort
                .findByTenantIdAndIdentifier(tenantId, identifier)
                .orElseThrow(() -> new UserNotFoundException(identifier));
    }

    /**
     * 중복 식별자 체크
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param identifier 식별자
     * @return 존재하면 true
     */
    public boolean existsByTenantIdAndOrganizationIdAndIdentifier(
            TenantId tenantId, OrganizationId organizationId, String identifier) {
        return queryPort.existsByTenantIdAndOrganizationIdAndIdentifier(
                tenantId, organizationId, identifier);
    }

    /**
     * 사용자 목록 검색
     *
     * @param query 검색 조건
     * @return 사용자 목록
     */
    public List<User> search(SearchUsersQuery query) {
        return queryPort.search(query);
    }
}
