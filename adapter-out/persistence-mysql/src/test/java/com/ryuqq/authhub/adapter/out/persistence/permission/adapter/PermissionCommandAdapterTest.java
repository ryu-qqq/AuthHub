package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
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
 * PermissionCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionCommandAdapter 단위 테스트")
class PermissionCommandAdapterTest {

    @Mock private PermissionJpaRepository repository;

    @Mock private PermissionJpaEntityMapper mapper;

    private PermissionCommandAdapter adapter;

    private static final UUID PERMISSION_UUID = UUID.randomUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new PermissionCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("권한을 성공적으로 저장한다")
        void shouldPersistPermissionSuccessfully() {
            // given
            Permission domainToSave = PermissionFixture.create();
            Permission savedDomain =
                    PermissionFixture.createReconstituted(PERMISSION_UUID, "user:read");

            PermissionJpaEntity entityToSave =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            PermissionJpaEntity savedEntity =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            Permission result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 권한을 수정한다")
        void shouldUpdateExistingPermission() {
            // given
            Permission existingDomain =
                    PermissionFixture.createReconstituted(PERMISSION_UUID, "user:read");
            Permission updatedDomain =
                    PermissionFixture.createReconstituted(PERMISSION_UUID, "user:write");

            PermissionJpaEntity entityToUpdate =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            PermissionJpaEntity updatedEntity =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:write",
                            "user",
                            "write",
                            "사용자 수정 권한",
                            PermissionType.CUSTOM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            Permission result = adapter.persist(existingDomain);

            // then
            assertThat(result.keyValue()).isEqualTo("user:write");
            verify(repository).save(any(PermissionJpaEntity.class));
        }

        @Test
        @DisplayName("시스템 권한을 저장한다")
        void shouldPersistSystemPermission() {
            // given
            Permission systemPermission = PermissionFixture.createSystem();
            Permission savedSystemPermission =
                    PermissionFixture.createReconstitutedSystem(PERMISSION_UUID, "user:read");

            PermissionJpaEntity entityToSave =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한 (시스템)",
                            PermissionType.SYSTEM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);
            PermissionJpaEntity savedEntity =
                    PermissionJpaEntity.of(
                            PERMISSION_UUID,
                            "user:read",
                            "user",
                            "read",
                            "사용자 조회 권한 (시스템)",
                            PermissionType.SYSTEM,
                            false,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(systemPermission)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedSystemPermission);

            // when
            Permission result = adapter.persist(systemPermission);

            // then
            assertThat(result.isSystem()).isTrue();
        }
    }
}
