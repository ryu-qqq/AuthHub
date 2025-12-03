package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

/**
 * UserJpaEntityMapper - User Domain ↔ Entity 변환
 *
 * <p>Domain 객체와 JPA Entity 간의 양방향 변환을 담당합니다.
 *
 * <p><strong>시간 변환 전략:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC Zone 기준 변환)
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class UserJpaEntityMapper {

    private static final ZoneId UTC = ZoneId.of("UTC");

    /**
     * Domain → Entity 변환
     *
     * @param user User Domain 객체
     * @return UserJpaEntity
     */
    public UserJpaEntity toEntity(User user) {
        return UserJpaEntity.of(
                user.userIdValue(),
                user.tenantIdValue(),
                user.organizationIdValue(),
                user.getUserType(),
                user.getUserStatus(),
                user.getCredential().identifier(),
                user.getHashedPassword(),
                user.getProfile().name(),
                user.getProfile().phoneNumberValue(),
                toLocalDateTime(user.createdAt()),
                toLocalDateTime(user.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p>Domain.reconstitute() 메서드를 사용하여 영속화된 데이터를 복원합니다.
     *
     * @param entity UserJpaEntity
     * @return User Domain 객체
     */
    public User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                TenantId.of(entity.getTenantId()),
                toOrganizationId(entity.getOrganizationId()),
                entity.getUserType(),
                entity.getStatus(),
                toCredential(entity),
                toUserProfile(entity),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    private OrganizationId toOrganizationId(Long organizationId) {
        return organizationId != null ? OrganizationId.of(organizationId) : null;
    }

    private Credential toCredential(UserJpaEntity entity) {
        return Credential.of(entity.getIdentifier(), Password.ofHashed(entity.getHashedPassword()));
    }

    private UserProfile toUserProfile(UserJpaEntity entity) {
        return UserProfile.of(entity.getName(), toPhoneNumber(entity.getPhoneNumber()));
    }

    private PhoneNumber toPhoneNumber(String phoneNumber) {
        return phoneNumber != null ? PhoneNumber.of(phoneNumber) : null;
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, UTC);
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(UTC).toInstant();
    }
}
