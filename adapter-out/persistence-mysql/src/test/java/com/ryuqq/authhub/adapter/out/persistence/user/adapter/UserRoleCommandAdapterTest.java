package com.ryuqq.authhub.adapter.out.persistence.user.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.user.entity.UserRoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.user.mapper.UserRoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.user.repository.UserRoleJpaRepository;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserRole;
import java.time.Instant;
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
 * UserRoleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleCommandAdapter 단위 테스트")
class UserRoleCommandAdapterTest {

    @Mock private UserRoleJpaRepository repository;

    @Mock private UserRoleJpaEntityMapper mapper;

    private UserRoleCommandAdapter adapter;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID ROLE_UUID = UUID.randomUUID();
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        adapter = new UserRoleCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("save 메서드")
    class SaveTest {

        @Test
        @DisplayName("UserRole을 성공적으로 저장한다")
        void shouldSaveUserRoleSuccessfully() {
            // given
            UserRole domainToSave =
                    UserRole.of(UserId.of(USER_UUID), RoleId.of(ROLE_UUID), FIXED_INSTANT);
            UserRole savedDomain =
                    UserRole.reconstitute(
                            UserId.of(USER_UUID), RoleId.of(ROLE_UUID), FIXED_INSTANT);

            UserRoleJpaEntity entityToSave =
                    UserRoleJpaEntity.of(null, USER_UUID, ROLE_UUID, FIXED_INSTANT);
            UserRoleJpaEntity savedEntity =
                    UserRoleJpaEntity.of(1L, USER_UUID, ROLE_UUID, FIXED_INSTANT);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            UserRole result = adapter.save(domainToSave);

            // then
            assertThat(result.userIdValue()).isEqualTo(USER_UUID);
            assertThat(result.roleIdValue()).isEqualTo(ROLE_UUID);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("UserId와 RoleId로 UserRole을 삭제한다")
        void shouldDeleteUserRoleByUserIdAndRoleId() {
            // given
            UserId userId = UserId.of(USER_UUID);
            RoleId roleId = RoleId.of(ROLE_UUID);

            // when
            adapter.delete(userId, roleId);

            // then
            verify(repository).deleteByUserIdAndRoleId(USER_UUID, ROLE_UUID);
        }
    }
}
