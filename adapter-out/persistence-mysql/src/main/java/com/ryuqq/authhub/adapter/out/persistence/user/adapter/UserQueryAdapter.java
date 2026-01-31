package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
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
 *   <li>existsById() - ID 존재 여부 확인
 *   <li>existsByOrganizationIdAndIdentifier() - 조직 내 식별자 중복 확인
 *   <li>existsByOrganizationIdAndPhoneNumber() - 조직 내 전화번호 중복 확인
 *   <li>findByIdentifier() - 식별자로 단건 조회 (로그인용)
 *   <li>findAllBySearchCriteria() - SearchCriteria 기반 조건 검색
 *   <li>countBySearchCriteria() - SearchCriteria 기반 개수 조회
 *   <li>findAllByIds() - ID 목록으로 다건 조회
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>@Transactional 금지 (Manager/Facade에서 관리)
 *   <li>비즈니스 로직 금지 (단순 위임 + 변환만)
 *   <li>Domain 반환 (Mapper로 변환)
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
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
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>UserId에서 UUID 추출
     *   <li>QueryDSL Repository로 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param id 사용자 ID
     * @return Optional<User>
     */
    @Override
    public Optional<User> findById(UserId id) {
        return repository.findByUserId(id.value()).map(mapper::toDomain);
    }

    /**
     * ID 존재 여부 확인
     *
     * @param id 사용자 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(UserId id) {
        return repository.existsByUserId(id.value());
    }

    /**
     * 조직 내 식별자 중복 확인
     *
     * @param organizationId 조직 ID
     * @param identifier 로그인 식별자
     * @return 존재 여부
     */
    @Override
    public boolean existsByOrganizationIdAndIdentifier(
            OrganizationId organizationId, Identifier identifier) {
        return repository.existsByOrganizationIdAndIdentifier(
                organizationId.value(), identifier.value());
    }

    /**
     * 조직 내 전화번호 중복 확인
     *
     * @param organizationId 조직 ID
     * @param phoneNumber 전화번호
     * @return 존재 여부
     */
    @Override
    public boolean existsByOrganizationIdAndPhoneNumber(
            OrganizationId organizationId, PhoneNumber phoneNumber) {
        return repository.existsByOrganizationIdAndPhoneNumber(
                organizationId.value(), phoneNumber.value());
    }

    /**
     * 식별자로 사용자 단건 조회 (로그인용)
     *
     * @param identifier 로그인 식별자
     * @return Optional<User>
     */
    @Override
    public Optional<User> findByIdentifier(Identifier identifier) {
        return repository.findByIdentifier(identifier.value()).map(mapper::toDomain);
    }

    /**
     * SearchCriteria 기반 사용자 목록 조회
     *
     * <p><strong>처리 흐름:</strong>
     *
     * <ol>
     *   <li>QueryDSL Repository로 조건 조회
     *   <li>Entity → Domain 변환 (Mapper)
     * </ol>
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return User Domain 목록
     */
    @Override
    public List<User> findAllBySearchCriteria(UserSearchCriteria criteria) {
        return repository.findAllByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    /**
     * SearchCriteria 기반 사용자 개수 조회
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return 조건에 맞는 사용자 총 개수
     */
    @Override
    public long countBySearchCriteria(UserSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }
}
