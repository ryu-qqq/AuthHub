package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserCriteria;
import java.util.List;
import java.util.Optional;

/**
 * UserQueryPort - 사용자 조회 Port-Out
 *
 * <p>Persistence Layer에서 구현할 사용자 조회 인터페이스입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>*QueryPort 네이밍 규칙
 *   <li>Application Layer에서 정의
 *   <li>Adapter-Out에서 구현
 *   <li>Domain Criteria 사용 (Application DTO 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserQueryPort {

    /**
     * 테넌트 ID와 식별자(이메일/사용자명)로 사용자 조회
     *
     * @param tenantId 테넌트 ID
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @return 사용자 Optional
     */
    Optional<User> findByTenantIdAndIdentifier(TenantId tenantId, String identifier);

    /**
     * 식별자(이메일/사용자명)로 사용자 조회
     *
     * <p>테넌트와 무관하게 식별자로 사용자를 조회합니다. 로그인 시 사용됩니다.
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @return 사용자 Optional
     */
    Optional<User> findByIdentifier(String identifier);

    /**
     * 사용자 ID로 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 Optional
     */
    Optional<User> findById(UserId userId);

    /**
     * 테넌트 ID와 조직 ID, 식별자로 중복 체크
     *
     * @param tenantId 테넌트 ID
     * @param organizationId 조직 ID
     * @param identifier 사용자 식별자
     * @return 존재하면 true
     */
    boolean existsByTenantIdAndOrganizationIdAndIdentifier(
            TenantId tenantId, OrganizationId organizationId, String identifier);

    /**
     * 테넌트 내 핸드폰 번호 중복 체크
     *
     * @param tenantId 테넌트 ID
     * @param phoneNumber 핸드폰 번호
     * @return 존재하면 true
     */
    boolean existsByTenantIdAndPhoneNumber(TenantId tenantId, String phoneNumber);

    /**
     * 조건에 맞는 사용자 목록 조회 (페이징)
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return 사용자 Domain 목록
     */
    List<User> findAllByCriteria(UserCriteria criteria);

    /**
     * 조건에 맞는 사용자 개수 조회
     *
     * @param criteria 검색 조건 (UserCriteria)
     * @return 조건에 맞는 사용자 총 개수
     */
    long countByCriteria(UserCriteria criteria);
}
