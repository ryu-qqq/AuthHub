package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserQueryDslRepository;
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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserQueryAdapter 테스트
 *
 * <p>UserQueryPort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryAdapter 테스트")
class UserQueryAdapterTest {

    @Mock private UserQueryDslRepository userQueryDslRepository;

    @Mock private UserJpaEntityMapper userJpaEntityMapper;

    private UserQueryAdapter userQueryAdapter;

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
    private static final LocalDateTime CREATED_AT_LOCAL = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT_LOCAL = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @BeforeEach
    void setUp() {
        userQueryAdapter = new UserQueryAdapter(userQueryDslRepository, userJpaEntityMapper);
    }

    private UserJpaEntity createEntity() {
        return UserJpaEntity.of(
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
    }

    private User createDomain() {
        return User.reconstitute(
                UserId.of(ID),
                TenantId.of(TENANT_ID),
                OrganizationId.of(ORGANIZATION_ID),
                USER_TYPE,
                STATUS,
                Credential.of(IDENTIFIER, Password.ofHashed(HASHED_PASSWORD)),
                UserProfile.of(NAME, PhoneNumber.of(PHONE_NUMBER)),
                CREATED_AT,
                UPDATED_AT);
    }

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("존재하는 User를 조회한다")
        void shouldFindUserById() {
            // Given
            UserId userId = UserId.of(ID);
            UserJpaEntity entity = createEntity();
            User user = createDomain();

            given(userQueryDslRepository.findById(ID)).willReturn(Optional.of(entity));
            given(userJpaEntityMapper.toDomain(entity)).willReturn(user);

            // When
            Optional<User> result = userQueryAdapter.findById(userId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().userIdValue()).isEqualTo(ID);
            verify(userQueryDslRepository).findById(ID);
            verify(userJpaEntityMapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 User는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentUser() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            UserId userId = UserId.of(nonExistentId);
            given(userQueryDslRepository.findById(nonExistentId)).willReturn(Optional.empty());

            // When
            Optional<User> result = userQueryAdapter.findById(userId);

            // Then
            assertThat(result).isEmpty();
            verify(userQueryDslRepository).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("existsById() 메서드는")
    class ExistsByIdMethod {

        @Test
        @DisplayName("존재하는 ID는 true를 반환한다")
        void shouldReturnTrueForExistingId() {
            // Given
            UserId userId = UserId.of(ID);
            given(userQueryDslRepository.existsById(ID)).willReturn(true);

            // When
            boolean result = userQueryAdapter.existsById(userId);

            // Then
            assertThat(result).isTrue();
            verify(userQueryDslRepository).existsById(ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            UserId userId = UserId.of(nonExistentId);
            given(userQueryDslRepository.existsById(nonExistentId)).willReturn(false);

            // When
            boolean result = userQueryAdapter.existsById(userId);

            // Then
            assertThat(result).isFalse();
            verify(userQueryDslRepository).existsById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndPhoneNumberExcludingUser() 메서드는")
    class ExistsByTenantIdAndPhoneNumberExcludingUserMethod {

        @Test
        @DisplayName("동일 Tenant 내 같은 전화번호가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenPhoneNumberExists() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            PhoneNumber phoneNumber = PhoneNumber.of(PHONE_NUMBER);
            given(
                            userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                                    TENANT_ID, PHONE_NUMBER, null))
                    .willReturn(true);

            // When
            boolean result =
                    userQueryAdapter.existsByTenantIdAndPhoneNumberExcludingUser(
                            tenantId, phoneNumber, null);

            // Then
            assertThat(result).isTrue();
            verify(userQueryDslRepository)
                    .existsByTenantIdAndPhoneNumberExcludingUser(TENANT_ID, PHONE_NUMBER, null);
        }

        @Test
        @DisplayName("본인을 제외하고 중복 확인한다")
        void shouldExcludeOwnUser() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            PhoneNumber phoneNumber = PhoneNumber.of(PHONE_NUMBER);
            UserId excludeUserId = UserId.of(ID);
            given(
                            userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                                    TENANT_ID, PHONE_NUMBER, ID))
                    .willReturn(false);

            // When
            boolean result =
                    userQueryAdapter.existsByTenantIdAndPhoneNumberExcludingUser(
                            tenantId, phoneNumber, excludeUserId);

            // Then
            assertThat(result).isFalse();
            verify(userQueryDslRepository)
                    .existsByTenantIdAndPhoneNumberExcludingUser(TENANT_ID, PHONE_NUMBER, ID);
        }

        @Test
        @DisplayName("동일 Tenant 내 같은 전화번호가 없으면 false를 반환한다")
        void shouldReturnFalseWhenPhoneNumberNotExists() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            String otherPhoneNumber = "+821099999999";
            PhoneNumber phoneNumber = PhoneNumber.of(otherPhoneNumber);
            given(
                            userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                                    TENANT_ID, otherPhoneNumber, null))
                    .willReturn(false);

            // When
            boolean result =
                    userQueryAdapter.existsByTenantIdAndPhoneNumberExcludingUser(
                            tenantId, phoneNumber, null);

            // Then
            assertThat(result).isFalse();
            verify(userQueryDslRepository)
                    .existsByTenantIdAndPhoneNumberExcludingUser(TENANT_ID, otherPhoneNumber, null);
        }
    }

    @Nested
    @DisplayName("existsActiveByOrganizationId() 메서드는")
    class ExistsActiveByOrganizationIdMethod {

        @Test
        @DisplayName("Organization 내 활성 User가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenActiveUserExists() {
            // Given
            OrganizationId organizationId = OrganizationId.of(ORGANIZATION_ID);
            given(userQueryDslRepository.existsActiveByOrganizationId(ORGANIZATION_ID))
                    .willReturn(true);

            // When
            boolean result = userQueryAdapter.existsActiveByOrganizationId(organizationId);

            // Then
            assertThat(result).isTrue();
            verify(userQueryDslRepository).existsActiveByOrganizationId(ORGANIZATION_ID);
        }

        @Test
        @DisplayName("Organization 내 활성 User가 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoActiveUserExists() {
            // Given
            OrganizationId organizationId = OrganizationId.of(999L);
            given(userQueryDslRepository.existsActiveByOrganizationId(999L)).willReturn(false);

            // When
            boolean result = userQueryAdapter.existsActiveByOrganizationId(organizationId);

            // Then
            assertThat(result).isFalse();
            verify(userQueryDslRepository).existsActiveByOrganizationId(999L);
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndIdentifier() 메서드는")
    class FindByTenantIdAndIdentifierMethod {

        @Test
        @DisplayName("Tenant/Identifier로 User를 조회한다")
        void shouldFindUserByTenantIdAndIdentifier() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            UserJpaEntity entity = createEntity();
            User user = createDomain();

            given(userQueryDslRepository.findByTenantIdAndIdentifier(TENANT_ID, IDENTIFIER))
                    .willReturn(Optional.of(entity));
            given(userJpaEntityMapper.toDomain(entity)).willReturn(user);

            // When
            Optional<User> result =
                    userQueryAdapter.findByTenantIdAndIdentifier(tenantId, IDENTIFIER);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getCredential().identifier()).isEqualTo(IDENTIFIER);
            verify(userQueryDslRepository).findByTenantIdAndIdentifier(TENANT_ID, IDENTIFIER);
            verify(userJpaEntityMapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 Identifier는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentIdentifier() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            String nonExistentIdentifier = "nonexistent@example.com";
            given(
                            userQueryDslRepository.findByTenantIdAndIdentifier(
                                    TENANT_ID, nonExistentIdentifier))
                    .willReturn(Optional.empty());

            // When
            Optional<User> result =
                    userQueryAdapter.findByTenantIdAndIdentifier(tenantId, nonExistentIdentifier);

            // Then
            assertThat(result).isEmpty();
            verify(userQueryDslRepository)
                    .findByTenantIdAndIdentifier(TENANT_ID, nonExistentIdentifier);
        }
    }
}
