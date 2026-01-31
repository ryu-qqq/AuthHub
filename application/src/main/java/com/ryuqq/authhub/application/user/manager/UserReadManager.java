package com.ryuqq.authhub.application.user.manager;

import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserReadManager - 단일 Port 조회 관리
 *
 * <p>QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 *   <li>Criteria 기반 조회
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserReadManager {

    private final UserQueryPort queryPort;

    public UserReadManager(UserQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 User 조회 (필수)
     *
     * @param id User ID
     * @return User Domain
     * @throws UserNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public User findById(UserId id) {
        return queryPort.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * ID로 User 존재 여부 확인
     *
     * @param id User ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(UserId id) {
        return queryPort.existsById(id);
    }

    /**
     * 식별자로 User 조회 (로그인 시 사용)
     *
     * @param identifier 식별자 (이메일 또는 사용자명)
     * @return User Domain (Optional)
     */
    @Transactional(readOnly = true)
    public java.util.Optional<User> findByIdentifier(Identifier identifier) {
        return queryPort.findByIdentifier(identifier);
    }

    /**
     * 조직 내 식별자로 존재 여부 확인
     *
     * @param organizationId 조직 ID
     * @param identifier 식별자
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByOrganizationIdAndIdentifier(
            OrganizationId organizationId, Identifier identifier) {
        return queryPort.existsByOrganizationIdAndIdentifier(organizationId, identifier);
    }

    /**
     * 조직 내 전화번호로 존재 여부 확인
     *
     * @param organizationId 조직 ID
     * @param phoneNumber 전화번호
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByOrganizationIdAndPhoneNumber(
            OrganizationId organizationId, PhoneNumber phoneNumber) {
        return queryPort.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber);
    }

    /**
     * 조건에 맞는 사용자 목록 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return User Domain 목록
     */
    @Transactional(readOnly = true)
    public List<User> findAllBySearchCriteria(UserSearchCriteria criteria) {
        return queryPort.findAllBySearchCriteria(criteria);
    }

    /**
     * 조건에 맞는 사용자 개수 조회 (SearchCriteria 기반)
     *
     * @param criteria 검색 조건 (UserSearchCriteria)
     * @return 조건에 맞는 User 총 개수
     */
    @Transactional(readOnly = true)
    public long countBySearchCriteria(UserSearchCriteria criteria) {
        return queryPort.countBySearchCriteria(criteria);
    }
}
