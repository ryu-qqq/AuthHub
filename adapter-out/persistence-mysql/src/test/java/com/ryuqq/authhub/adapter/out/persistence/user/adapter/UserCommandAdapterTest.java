package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
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
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandAdapter 테스트
 *
 * <p>UserPersistencePort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandAdapter 테스트")
class UserCommandAdapterTest {

    @Mock private UserJpaRepository userJpaRepository;

    @Mock private UserJpaEntityMapper userJpaEntityMapper;

    private UserCommandAdapter userCommandAdapter;

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
        userCommandAdapter = new UserCommandAdapter(userJpaRepository, userJpaEntityMapper);
    }

    @Nested
    @DisplayName("persist() 메서드는")
    class PersistMethod {

        @Test
        @DisplayName("신규 User를 저장하고 생성된 ID를 반환한다")
        void shouldSaveNewUserAndReturnId() {
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
            UserJpaEntity entityToSave =
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
            UserJpaEntity savedEntity =
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

            given(userJpaEntityMapper.toEntity(user)).willReturn(entityToSave);
            given(userJpaRepository.save(entityToSave)).willReturn(savedEntity);

            // When
            UserId result = userCommandAdapter.persist(user);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(userJpaEntityMapper).toEntity(user);
            verify(userJpaRepository).save(entityToSave);
        }

        @Test
        @DisplayName("기존 User를 수정하고 ID를 반환한다")
        void shouldUpdateExistingUserAndReturnId() {
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
            UserJpaEntity entityToSave =
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

            given(userJpaEntityMapper.toEntity(user)).willReturn(entityToSave);
            given(userJpaRepository.save(entityToSave)).willReturn(entityToSave);

            // When
            UserId result = userCommandAdapter.persist(user);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(userJpaRepository).save(entityToSave);
        }
    }
}
