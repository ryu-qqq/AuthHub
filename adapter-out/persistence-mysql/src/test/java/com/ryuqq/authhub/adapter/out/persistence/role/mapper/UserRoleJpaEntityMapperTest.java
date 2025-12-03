package com.ryuqq.authhub.adapter.out.persistence.role.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.domain.role.aggregate.UserRole;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserRoleJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserRoleJpaEntityMapper 테스트")
class UserRoleJpaEntityMapperTest {

    private UserRoleJpaEntityMapper mapper;

    private static final Long ID = 1L;
    private static final UUID USER_UUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long ROLE_ID = 100L;
    private static final Instant ASSIGNED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final LocalDateTime ASSIGNED_AT_LOCAL =
            LocalDateTime.ofInstant(ASSIGNED_AT, ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        mapper = new UserRoleJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 UserRole을 Entity로 변환한다")
        void shouldConvertExistingUserRoleToEntity() {
            // Given
            UserRole userRole =
                    UserRole.reconstitute(
                            ID, UserId.of(USER_UUID), RoleId.of(ROLE_ID), ASSIGNED_AT);

            // When
            UserRoleJpaEntity entity = mapper.toEntity(userRole);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getUserId()).isEqualTo(USER_UUID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getAssignedAt()).isEqualTo(ASSIGNED_AT_LOCAL);
        }

        @Test
        @DisplayName("신규 UserRole은 ID가 null인 Entity로 변환한다")
        void shouldConvertNewUserRoleToEntityWithNullId() {
            // Given
            UserRole userRole =
                    UserRole.of(null, UserId.of(USER_UUID), RoleId.of(ROLE_ID), ASSIGNED_AT);

            // When
            UserRoleJpaEntity entity = mapper.toEntity(userRole);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getUserId()).isEqualTo(USER_UUID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            UserRoleJpaEntity entity =
                    UserRoleJpaEntity.of(ID, USER_UUID, ROLE_ID, ASSIGNED_AT_LOCAL);

            // When
            UserRole userRole = mapper.toDomain(entity);

            // Then
            assertThat(userRole.getId()).isEqualTo(ID);
            assertThat(userRole.getUserId().value()).isEqualTo(USER_UUID);
            assertThat(userRole.getRoleId().value()).isEqualTo(ROLE_ID);
            assertThat(userRole.getAssignedAt()).isEqualTo(ASSIGNED_AT);
        }

        @Test
        @DisplayName("시간 변환이 정확하게 수행된다")
        void shouldConvertTimeCorrectly() {
            // Given
            Instant specificTime = Instant.parse("2025-06-15T14:30:45Z");
            LocalDateTime specificTimeLocal =
                    LocalDateTime.ofInstant(specificTime, ZoneId.of("UTC"));
            UserRoleJpaEntity entity =
                    UserRoleJpaEntity.of(ID, USER_UUID, ROLE_ID, specificTimeLocal);

            // When
            UserRole userRole = mapper.toDomain(entity);

            // Then
            assertThat(userRole.getAssignedAt()).isEqualTo(specificTime);
        }
    }
}
