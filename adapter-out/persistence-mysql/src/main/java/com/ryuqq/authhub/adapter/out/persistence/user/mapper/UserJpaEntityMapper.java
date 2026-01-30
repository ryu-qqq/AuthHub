package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.common.vo.DeletionStatus;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.HashedPassword;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Component;

/**
 * UserJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 User 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>User → UserJpaEntity (저장용)
 *   <li>UserJpaEntity → User (조회용)
 * </ul>
 *
 * <p><strong>시간 처리:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: Instant (UTC) - 변환 없이 직접 전달
 * </ul>
 *
 * <p><strong>Hexagonal Architecture 관점:</strong>
 *
 * <ul>
 *   <li>Adapter Layer의 책임
 *   <li>Domain과 Infrastructure 기술 분리
 *   <li>Domain은 JPA 의존성 없음
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserJpaEntityMapper {

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 User 저장
     *   <li>기존 User 수정 (Hibernate Dirty Checking)
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>userId: Domain.userIdValue() → Entity.userId (String)
     *   <li>organizationId: Domain.organizationIdValue() → Entity.organizationId (String)
     *   <li>identifier: Domain.identifierValue() → Entity.identifier
     *   <li>phoneNumber: Domain.phoneNumberValue() → Entity.phoneNumber
     *   <li>hashedPassword: Domain.hashedPasswordValue() → Entity.hashedPassword
     *   <li>status: Domain.getStatus() → Entity.status
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     *   <li>deletedAt: DeletionStatus.deletedAt() → Instant (직접 전달)
     * </ul>
     *
     * @param domain User 도메인
     * @return UserJpaEntity
     */
    public UserJpaEntity toEntity(User domain) {
        DeletionStatus deletionStatus = domain.getDeletionStatus();
        return UserJpaEntity.of(
                domain.userIdValue(),
                domain.organizationIdValue(),
                domain.identifierValue(),
                domain.phoneNumberValue(),
                domain.hashedPasswordValue(),
                domain.getStatus(),
                domain.createdAt(),
                domain.updatedAt(),
                deletionStatus.deletedAt());
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * <p><strong>변환 규칙:</strong>
     *
     * <ul>
     *   <li>userId: Entity.userId (String) → UserId VO
     *   <li>organizationId: Entity.organizationId (String) → OrganizationId VO
     *   <li>identifier: Entity.identifier → Identifier VO
     *   <li>phoneNumber: Entity.phoneNumber → PhoneNumber VO (nullable)
     *   <li>hashedPassword: Entity.hashedPassword → HashedPassword VO
     *   <li>status: Entity.status → UserStatus Enum
     *   <li>deletedAt: Entity.getDeletedAt() → DeletionStatus
     *   <li>createdAt: Instant → Instant (직접 전달)
     *   <li>updatedAt: Instant → Instant (직접 전달)
     * </ul>
     *
     * @param entity UserJpaEntity
     * @return User 도메인
     */
    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getUserId()),
                OrganizationId.of(entity.getOrganizationId()),
                Identifier.of(entity.getIdentifier()),
                entity.getPhoneNumber() != null ? PhoneNumber.of(entity.getPhoneNumber()) : null,
                HashedPassword.of(entity.getHashedPassword()),
                entity.getStatus(),
                DeletionStatus.reconstitute(entity.isDeleted(), entity.getDeletedAt()),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
