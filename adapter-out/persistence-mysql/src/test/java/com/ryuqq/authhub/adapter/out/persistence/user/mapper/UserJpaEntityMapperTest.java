package com.ryuqq.authhub.adapter.out.persistence.user.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserJpaEntityMapper 테스트")
class UserJpaEntityMapperTest {

    private UserJpaEntityMapper mapper;

    private static final UUID ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final Long TENANT_ID = 100L;
    private static final Long ORGANIZATION_ID = 200L;
    private static final UserType USER_TYPE = UserType.PUBLIC;
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final String IDENTIFIER = "test@example.com";
    private static final String HASHED_PASSWORD = "$2a$10$hashedpassword";
    private static final String NAME = "Test User";
    private static final String PHONE_NUMBER = "+821012345678";
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL =
            LocalDateTime.ofInstant(CREATED_AT, ZoneId.of("UTC"));
    private static final LocalDateTime UPDATED_AT_LOCAL =
            LocalDateTime.ofInstant(UPDATED_AT, ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        mapper = new UserJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 User를 Entity로 변환한다")
        void shouldConvertExistingUserToEntity() {
            // Given
            User user =
                    User.reconstitute(
                            UserId.of(ID),
                            TenantId.of(TENANT_ID),
                            OrganizationId.of(ORGANIZATION_ID),
                            USER_TYPE,
                            STATUS,
                            Credential.of(IDENTIFIER, Password.ofHashed(HASHED_PASSWORD)),
                            UserProfile.of(NAME, PhoneNumber.of(PHONE_NUMBER)),
                            CREATED_AT,
                            UPDATED_AT);

            // When
            UserJpaEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
            assertThat(entity.getUserType()).isEqualTo(USER_TYPE);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getIdentifier()).isEqualTo(IDENTIFIER);
            assertThat(entity.getHashedPassword()).isEqualTo(HASHED_PASSWORD);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT_LOCAL);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT_LOCAL);
        }

        @Test
        @DisplayName("Organization이 없는 User를 Entity로 변환한다")
        void shouldConvertUserWithoutOrganization() {
            // Given
            User user =
                    User.reconstitute(
                            UserId.of(ID),
                            TenantId.of(TENANT_ID),
                            null,
                            USER_TYPE,
                            STATUS,
                            Credential.of(IDENTIFIER, Password.ofHashed(HASHED_PASSWORD)),
                            UserProfile.empty(),
                            CREATED_AT,
                            UPDATED_AT);

            // When
            UserJpaEntity entity = mapper.toEntity(user);

            // Then
            assertThat(entity.getOrganizationId()).isNull();
            assertThat(entity.getName()).isNull();
            assertThat(entity.getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT_LOCAL,
                            UPDATED_AT_LOCAL);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.userIdValue()).isEqualTo(ID);
            assertThat(user.tenantIdValue()).isEqualTo(TENANT_ID);
            assertThat(user.organizationIdValue()).isEqualTo(ORGANIZATION_ID);
            assertThat(user.getUserType()).isEqualTo(USER_TYPE);
            assertThat(user.getUserStatus()).isEqualTo(STATUS);
            assertThat(user.getCredential().identifier()).isEqualTo(IDENTIFIER);
            assertThat(user.getHashedPassword()).isEqualTo(HASHED_PASSWORD);
            assertThat(user.getProfile().name()).isEqualTo(NAME);
            assertThat(user.getProfile().phoneNumberValue()).isEqualTo(PHONE_NUMBER);
            assertThat(user.createdAt()).isEqualTo(CREATED_AT);
            assertThat(user.updatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("Organization이 없는 Entity를 Domain으로 변환한다")
        void shouldConvertEntityWithoutOrganization() {
            // Given
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            null,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            null,
                            null,
                            CREATED_AT_LOCAL,
                            UPDATED_AT_LOCAL);

            // When
            User user = mapper.toDomain(entity);

            // Then
            assertThat(user.organizationIdValue()).isNull();
            assertThat(user.getProfile().name()).isNull();
            assertThat(user.getProfile().phoneNumber()).isNull();
        }
    }
}
