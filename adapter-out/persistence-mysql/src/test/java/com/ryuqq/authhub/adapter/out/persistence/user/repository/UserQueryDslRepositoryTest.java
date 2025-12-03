package com.ryuqq.authhub.adapter.out.persistence.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * UserQueryDslRepository 테스트
 *
 * <p>QueryDSL 기반 조회 Repository 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(UserQueryDslRepository.class)
@DisplayName("UserQueryDslRepository 테스트")
class UserQueryDslRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;

    @Autowired private UserQueryDslRepository userQueryDslRepository;

    private static final Long TENANT_ID = 100L;
    private static final Long ORGANIZATION_ID = 200L;
    private static final UserType USER_TYPE = UserType.PUBLIC;
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final String IDENTIFIER = "test@example.com";
    private static final String HASHED_PASSWORD = "$2a$10$hashedpassword";
    private static final String NAME = "Test User";
    private static final String PHONE_NUMBER = "+821012345678";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    private UserJpaEntity createAndSaveUser(
            UUID id,
            Long tenantId,
            Long organizationId,
            UserStatus status,
            String identifier,
            String phoneNumber) {
        UserJpaEntity entity =
                UserJpaEntity.of(
                        id,
                        tenantId,
                        organizationId,
                        USER_TYPE,
                        status,
                        identifier,
                        HASHED_PASSWORD,
                        NAME,
                        phoneNumber,
                        CREATED_AT,
                        UPDATED_AT);
        return testEntityManager.persistAndFlush(entity);
    }

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("ID로 User를 조회한다")
        void shouldFindUserById() {
            // Given
            UUID id = UUID.randomUUID();
            UserJpaEntity savedEntity =
                    createAndSaveUser(
                            id, TENANT_ID, ORGANIZATION_ID, STATUS, IDENTIFIER, PHONE_NUMBER);

            // When
            Optional<UserJpaEntity> result = userQueryDslRepository.findById(savedEntity.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getIdentifier()).isEqualTo(IDENTIFIER);
            assertThat(result.get().getTenantId()).isEqualTo(TENANT_ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // When
            Optional<UserJpaEntity> result = userQueryDslRepository.findById(UUID.randomUUID());

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById() 메서드는")
    class ExistsByIdMethod {

        @Test
        @DisplayName("존재하는 ID는 true를 반환한다")
        void shouldReturnTrueForExistingId() {
            // Given
            UUID id = UUID.randomUUID();
            UserJpaEntity savedEntity =
                    createAndSaveUser(
                            id, TENANT_ID, ORGANIZATION_ID, STATUS, IDENTIFIER, PHONE_NUMBER);

            // When
            boolean result = userQueryDslRepository.existsById(savedEntity.getId());

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // When
            boolean result = userQueryDslRepository.existsById(UUID.randomUUID());

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndPhoneNumberExcludingUser() 메서드는")
    class ExistsByTenantIdAndPhoneNumberExcludingUserMethod {

        @Test
        @DisplayName("동일 Tenant 내 같은 전화번호가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenPhoneNumberExistsInTenant() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    STATUS,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            boolean result =
                    userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                            TENANT_ID, PHONE_NUMBER, null);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("본인은 제외하고 중복 확인한다")
        void shouldExcludeOwnUser() {
            // Given
            UUID userId = UUID.randomUUID();
            createAndSaveUser(userId, TENANT_ID, ORGANIZATION_ID, STATUS, IDENTIFIER, PHONE_NUMBER);

            // When
            boolean result =
                    userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                            TENANT_ID, PHONE_NUMBER, userId);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 Tenant의 동일 전화번호는 false를 반환한다")
        void shouldReturnFalseForDifferentTenant() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    STATUS,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            boolean result =
                    userQueryDslRepository.existsByTenantIdAndPhoneNumberExcludingUser(
                            999L, PHONE_NUMBER, null);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsActiveByOrganizationId() 메서드는")
    class ExistsActiveByOrganizationIdMethod {

        @Test
        @DisplayName("Organization 내 활성 User가 있으면 true를 반환한다")
        void shouldReturnTrueWhenActiveUserExistsInOrganization() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    UserStatus.ACTIVE,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            boolean result = userQueryDslRepository.existsActiveByOrganizationId(ORGANIZATION_ID);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Organization 내 비활성 User만 있으면 false를 반환한다")
        void shouldReturnFalseWhenOnlyInactiveUsersExist() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    UserStatus.INACTIVE,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            boolean result = userQueryDslRepository.existsActiveByOrganizationId(ORGANIZATION_ID);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Organization에 User가 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoUsersInOrganization() {
            // When
            boolean result = userQueryDslRepository.existsActiveByOrganizationId(999L);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndIdentifier() 메서드는")
    class FindByTenantIdAndIdentifierMethod {

        @Test
        @DisplayName("Tenant/Identifier로 User를 조회한다")
        void shouldFindUserByTenantIdAndIdentifier() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    STATUS,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            Optional<UserJpaEntity> result =
                    userQueryDslRepository.findByTenantIdAndIdentifier(TENANT_ID, IDENTIFIER);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getIdentifier()).isEqualTo(IDENTIFIER);
            assertThat(result.get().getTenantId()).isEqualTo(TENANT_ID);
        }

        @Test
        @DisplayName("다른 Tenant의 동일 Identifier는 조회되지 않는다")
        void shouldNotFindUserFromDifferentTenant() {
            // Given
            createAndSaveUser(
                    UUID.randomUUID(),
                    TENANT_ID,
                    ORGANIZATION_ID,
                    STATUS,
                    IDENTIFIER,
                    PHONE_NUMBER);

            // When
            Optional<UserJpaEntity> result =
                    userQueryDslRepository.findByTenantIdAndIdentifier(999L, IDENTIFIER);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 Identifier는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentIdentifier() {
            // When
            Optional<UserJpaEntity> result =
                    userQueryDslRepository.findByTenantIdAndIdentifier(
                            TENANT_ID, "nonexistent@example.com");

            // Then
            assertThat(result).isEmpty();
        }
    }
}
