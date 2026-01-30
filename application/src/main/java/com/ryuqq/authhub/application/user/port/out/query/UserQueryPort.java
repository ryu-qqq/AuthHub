package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.util.List;
import java.util.Optional;

/**
 * UserQueryPort - User Aggregate 조회 포트 (Query)
 *
 * <p>Domain Aggregate를 조회하는 읽기 전용 Port입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>조회 메서드만 제공 (findById, existsById)
 *   <li>저장/수정/삭제 메서드 금지 (PersistencePort로 분리)
 *   <li>Value Object 파라미터 (원시 타입 금지)
 *   <li>Domain 반환 (DTO/Entity 반환 금지)
 *   <li>Optional 반환 (단건 조회 시 null 방지)
 *   <li>Criteria 기반 조회 (개별 파라미터 금지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface UserQueryPort {

    /**
     * ID로 User 단건 조회
     *
     * @param id User ID (Value Object)
     * @return User Domain (Optional)
     */
    Optional<User> findById(UserId id);

    /**
     * ID로 User 존재 여부 확인
     *
     * @param id User ID (Value Object)
     * @return 존재 여부
     */
    boolean existsById(UserId id);

    /**
     * 조직 내 식별자로 존재 여부 확인
     *
     * @param organizationId 조직 ID
     * @param identifier 식별자 (이메일 또는 사용자명)
     * @return 존재 여부
     */
    boolean existsByOrganizationIdAndIdentifier(
            OrganizationId organizationId, Identifier identifier);

    /**
     * 조직 내 전화번호로 존재 여부 확인
     *
     * @param organizationId 조직 ID
     * @param phoneNumber 전화번호
     * @return 존재 여부
     */
    boolean existsByOrganizationIdAndPhoneNumber(
            OrganizationId organizationId, PhoneNumber phoneNumber);

    /**
     * 식별자로 User 조회 (로그인 시 사용)
     *
     * @param identifier 식별자 (이메일 또는 사용자명)
     * @return User Domain (Optional)
     */
    Optional<User> findByIdentifier(Identifier identifier);

    /**
     * 조건에 맞는 사용자 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return User Domain 목록
     */
    List<User> findAllBySearchCriteria(UserSearchCriteria criteria);

    /**
     * 조건에 맞는 사용자 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return 조건에 맞는 User 총 개수
     */
    long countBySearchCriteria(UserSearchCriteria criteria);
}
