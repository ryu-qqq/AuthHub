package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * UserQueryAdapter - 사용자 Query Adapter (조회 전용)
 *
 * <p>UserQueryPort 구현체로서 사용자 조회 작업을 담당합니다.
 *
 * <p><strong>1:1 매핑 원칙:</strong>
 *
 * <ul>
 *   <li>UserQueryDslRepository (1개) + UserJpaEntityMapper (1개)
 *   <li>필드 2개만 허용
 *   <li>다른 Repository 주입 금지
 * </ul>
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findById() - ID로 단건 조회
 *   <li>findByTenantIdAndIdentifier() - 테넌트 내 식별자로 조회
 *   <li>existsByTenantIdAndOrganizationIdAndIdentifier() - 식별자 존재 여부 확인
 *   <li>findAllByCriteria() - 조건 검색
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Domain 반환 (Mapper로 변환)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserQueryAdapter implements UserQueryPort {

    private final UserQueryDslRepository repository;
    private final UserJpaEntityMapper mapper;

    public UserQueryAdapter(UserQueryDslRepository repository, UserJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * ID로 사용자 단건 조회
     *
     * @param userId 사용자 ID
     * @return Optional<User>
     */
    @Override
    public Optional<User> findById(UserId userId) {
        return repository.findByUserId(userId.value()).map(mapper::toDomain);
    }

    /**
     * 테넌트 내 식별자로 사용자 단건 조회
     *
     * @param tenantId 테넌트 ID
     * @param identifier 사용자 식별자
     * @return Optional<User>
     */
    @Override
    public Optional<User> findByTenantIdAndIdentifier(TenantId tenantId, String identifier) {
        return repository
                .findByTenantIdAndIdentifier(tenantId.value(), identifier)
                .map(mapper::toDomain);
    }

    /**
     * 식별자로 사용자 단건 조회 (로그인용)
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @return Optional<User>
     */
    @Override
    public Optional<User> findByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier).map(mapper::toDomain);
    }

    /**
     * 테넌트/조직 내 식별자 존재 여부 확인
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param identifier 사용자 식별자
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndOrganizationIdAndIdentifier(
            TenantId tenantId, OrganizationId organizationId, String identifier) {
        return repository.existsByTenantIdAndOrganizationIdAndIdentifier(
                tenantId.value(), organizationId.value(), identifier);
    }

    /**
     * 테넌트 내 핸드폰 번호 존재 여부 확인
     *
     * @param tenantId 테넌트 ID
     * @param phoneNumber 핸드폰 번호
     * @return 존재 여부
     */
    @Override
    public boolean existsByTenantIdAndPhoneNumber(TenantId tenantId, String phoneNumber) {
        return repository.existsByTenantIdAndPhoneNumber(tenantId.value(), phoneNumber);
    }

    /**
     * 조건에 맞는 사용자 목록 조회 (페이징)
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>UserCriteria에서 검색 조건 추출
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return User Domain 목록
     */
    @Override
    public List<User> findAllByCriteria(UserCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * 조건에 맞는 사용자 개수 조회
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return 조건에 맞는 사용자 총 개수
     */
    @Override
    public long countByCriteria(UserCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
