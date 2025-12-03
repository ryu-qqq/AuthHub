package com.ryuqq.authhub.application.user.port.out.query;

import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
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
     * Tenant 내 전화번호 중복 확인 (본인 제외)
     *
     * <p>같은 Tenant 내에서 동일한 전화번호를 가진 다른 사용자가 존재하는지 확인합니다. 본인의 전화번호는 중복으로 판단하지 않습니다.
     *
     * @param tenantId Tenant ID (Value Object)
     * @param phoneNumber 전화번호 (Value Object)
     * @param excludeUserId 제외할 User ID (본인, null 가능)
     * @return 중복 존재 여부
     */
    boolean existsByTenantIdAndPhoneNumberExcludingUser(
            TenantId tenantId, PhoneNumber phoneNumber, UserId excludeUserId);

    /**
     * Organization 내 활성 User 존재 여부 확인
     *
     * <p>Organization 비활성화 전 활성 상태의 User가 존재하는지 확인합니다.
     *
     * @param organizationId Organization ID (Value Object)
     * @return 활성 User 존재 여부
     */
    boolean existsActiveByOrganizationId(
            com.ryuqq.authhub.domain.organization.identifier.OrganizationId organizationId);

    /**
     * Tenant 내 Identifier로 User 조회 (로그인용)
     *
     * <p>로그인 시 Tenant와 Identifier(email)로 사용자를 조회합니다.
     *
     * @param tenantId Tenant ID (Value Object)
     * @param identifier 사용자 식별자 (email)
     * @return User Domain (Optional)
     */
    Optional<User> findByTenantIdAndIdentifier(TenantId tenantId, String identifier);
}
