package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RolePermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.identifier.PermissionId;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
import com.ryuqq.authhub.domain.role.vo.RolePermission;
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
 * RolePermissionCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionCommandAdapter 단위 테스트")
class RolePermissionCommandAdapterTest {

    @Mock private RolePermissionJpaRepository repository;

    @Mock private RolePermissionJpaEntityMapper mapper;

    private RolePermissionCommandAdapter adapter;

    private static final UUID ROLE_UUID = RoleFixture.defaultUUID();
    private static final UUID PERMISSION_UUID = UUID.randomUUID();
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        adapter = new RolePermissionCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("save 메서드")
    class SaveTest {

        @Test
        @DisplayName("RolePermission을 성공적으로 저장한다")
        void shouldSaveRolePermissionSuccessfully() {
            // given
            RolePermission domainToSave =
                    RolePermission.of(
                            RoleId.of(ROLE_UUID), PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);
            RolePermission savedDomain =
                    RolePermission.reconstitute(
                            RoleId.of(ROLE_UUID), PermissionId.of(PERMISSION_UUID), FIXED_INSTANT);

            RolePermissionJpaEntity entityToSave =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);
            RolePermissionJpaEntity savedEntity =
                    RolePermissionJpaEntity.of(
                            UUID.randomUUID(), ROLE_UUID, PERMISSION_UUID, FIXED_INSTANT);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            RolePermission result = adapter.save(domainToSave);

            // then
            assertThat(result.roleIdValue()).isEqualTo(ROLE_UUID);
            assertThat(result.permissionIdValue()).isEqualTo(PERMISSION_UUID);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteTest {

        @Test
        @DisplayName("RoleId와 PermissionId로 RolePermission을 삭제한다")
        void shouldDeleteRolePermissionByRoleIdAndPermissionId() {
            // given
            RoleId roleId = RoleId.of(ROLE_UUID);
            PermissionId permissionId = PermissionId.of(PERMISSION_UUID);

            // when
            adapter.delete(roleId, permissionId);

            // then
            verify(repository).deleteByRoleIdAndPermissionId(ROLE_UUID, PERMISSION_UUID);
        }
    }
}
