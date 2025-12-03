package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserQueryAdapter - User Query Adapter
 *
 * <p>UserQueryPort 구현체입니다. 조회 작업을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 사용 금지
 *   <li>QueryDslRepository에 조회 로직 위임
 *   <li>Entity → Domain 변환은 Mapper 사용
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserQueryAdapter implements UserQueryPort {

    private final UserQueryDslRepository userQueryDslRepository;
    private final UserJpaEntityMapper userJpaEntityMapper;

    public UserQueryAdapter(
            UserQueryDslRepository userQueryDslRepository,
            UserJpaEntityMapper userJpaEntityMapper) {
        this.userQueryDslRepository = userQueryDslRepository;
        this.userJpaEntityMapper = userJpaEntityMapper;
    }

    /**
     * ID로 User 조회
     *
     * @param id 조회할 User ID
     * @return User Domain 객체 (Optional)
     */
    @Override
    public Optional<User> findById(UserId id) {
        return userQueryDslRepository.findById(id.value()).map(userJpaEntityMapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 확인할 User ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(UserId id) {
        return userQueryDslRepository.existsById(id.value());
    }

    /**
     * Tenant 내 전화번호 중복 확인 (본인 제외)
     *
     * @param tenantId Tenant ID
     * @param phoneNumber 전화번호
     * @param excludeUserId 제외할 User ID (본인, null 가능)
     * @return 중복 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndPhoneNumberExcludingUser(
            TenantId tenantId, PhoneNumber phoneNumber, UserId excludeUserId) {
        return userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                tenantId.value(),
                phoneNumber.value(),
                excludeUserId != null ? excludeUserId.value() : null);
    }

    /**
     * Organization 내 활성 User 존재 여부 확인
     *
     * @param organizationId Organization ID
     * @return 활성 User 존재 여부
     */
    @Override
    public boolean existsActiveByOrganizationId(OrganizationId organizationId) {
        return userQueryDslRepository.existsActiveByOrganizationId(organizationId.value());
    }

    /**
     * Tenant/Identifier로 User 조회 (로그인용)
     *
     * @param tenantId Tenant ID
     * @param identifier 사용자 식별자 (email)
     * @return User Domain 객체 (Optional)
     */
    @Override
    public Optional<User> findByTenantIdAndIdentifier(TenantId tenantId, String identifier) {
        return userQueryDslRepository
                .findByTenantIdAndIdentifier(tenantId.value(), identifier)
                .map(userJpaEntityMapper::toDomain);
    }
}
