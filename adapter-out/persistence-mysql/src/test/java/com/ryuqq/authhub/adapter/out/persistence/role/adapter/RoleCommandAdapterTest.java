package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleJpaRepository;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.vo.RoleScope;
import com.ryuqq.authhub.domain.role.vo.RoleType;
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
 * RoleCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RoleCommandAdapter 단위 테스트")
class RoleCommandAdapterTest {

    @Mock private RoleJpaRepository repository;

    @Mock private RoleJpaEntityMapper mapper;

    private RoleCommandAdapter adapter;

    private static final UUID ROLE_UUID = RoleFixture.defaultUUID();
    private static final UUID TENANT_UUID = RoleFixture.defaultTenantUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new RoleCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("역할을 성공적으로 저장한다")
        void shouldPersistRoleSuccessfully() {
            // given
            Role domainToSave = RoleFixture.createNew();
            Role savedDomain = RoleFixture.create();

            RoleJpaEntity entityToSave =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            "Test role description",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            RoleJpaEntity savedEntity =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            "Test role description",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            Role result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 역할을 수정한다")
        void shouldUpdateExistingRole() {
            // given
            Role existingDomain = RoleFixture.create();
            Role updatedDomain = RoleFixture.createWithName("UPDATED_ROLE");

            RoleJpaEntity entityToUpdate =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            TENANT_UUID,
                            "TEST_ROLE",
                            "Test role description",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            RoleJpaEntity updatedEntity =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            TENANT_UUID,
                            "UPDATED_ROLE",
                            "Role with custom name",
                            RoleScope.ORGANIZATION,
                            RoleType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            Role result = adapter.persist(existingDomain);

            // then
            assertThat(result.nameValue()).isEqualTo("UPDATED_ROLE");
            verify(repository).save(any(RoleJpaEntity.class));
        }

        @Test
        @DisplayName("시스템 역할을 저장한다")
        void shouldPersistSystemRole() {
            // given
            Role systemRole = RoleFixture.createSystemGlobal();
            Role savedSystemRole = RoleFixture.createSystemGlobal();

            RoleJpaEntity entityToSave =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            null,
                            "SUPER_ADMIN",
                            "System super admin role",
                            RoleScope.GLOBAL,
                            RoleType.SYSTEM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            RoleJpaEntity savedEntity =
                    RoleJpaEntity.of(
                            ROLE_UUID,
                            null,
                            "SUPER_ADMIN",
                            "System super admin role",
                            RoleScope.GLOBAL,
                            RoleType.SYSTEM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(systemRole)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedSystemRole);

            // when
            Role result = adapter.persist(systemRole);

            // then
            assertThat(result.isSystem()).isTrue();
            assertThat(result.getScope()).isEqualTo(RoleScope.GLOBAL);
        }
    }
}
