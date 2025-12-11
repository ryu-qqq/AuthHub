package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserJpaRepository;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * UserCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandAdapter 단위 테스트")
class UserCommandAdapterTest {

    @Mock private UserJpaRepository repository;

    @Mock private UserJpaEntityMapper mapper;

    private UserCommandAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID TENANT_UUID = UserFixture.defaultTenantUUID();
    private static final UUID ORG_UUID = UserFixture.defaultOrganizationUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new UserCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("사용자를 성공적으로 저장한다")
        void shouldPersistUserSuccessfully() {
            // given
            User domainToSave = UserFixture.createNew();
            User savedDomain = UserFixture.create();

            UserJpaEntity entityToSave =
                    UserJpaEntity.of(
                            null,
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            UserJpaEntity savedEntity =
                    UserJpaEntity.of(
                            1L,
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            User result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 사용자를 수정한다")
        void shouldUpdateExistingUser() {
            // given
            User existingDomain = UserFixture.create();
            User updatedDomain = UserFixture.createWithIdentifier("updated@example.com");

            UserJpaEntity entityToUpdate =
                    UserJpaEntity.of(
                            1L,
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            UserJpaEntity updatedEntity =
                    UserJpaEntity.of(
                            1L,
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "updated@example.com",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            User result = adapter.persist(existingDomain);

            // then
            assertThat(result.getIdentifier()).isEqualTo("updated@example.com");
            verify(repository).save(any(UserJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("사용자를 성공적으로 삭제한다")
        void shouldDeleteUserSuccessfully() {
            // given
            User domainToDelete = UserFixture.create();
            UserJpaEntity entityToDelete =
                    UserJpaEntity.of(
                            1L,
                            USER_UUID,
                            TENANT_UUID,
                            ORG_UUID,
                            "user@example.com",
                            "hashed_password",
                            UserStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToDelete)).willReturn(entityToDelete);

            // when
            adapter.delete(domainToDelete);

            // then
            verify(mapper).toEntity(domainToDelete);
            verify(repository).delete(entityToDelete);
        }
    }
}
