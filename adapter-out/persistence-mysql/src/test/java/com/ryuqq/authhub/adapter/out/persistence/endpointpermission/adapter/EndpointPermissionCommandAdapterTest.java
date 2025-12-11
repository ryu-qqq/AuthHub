package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper.EndpointPermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository.EndpointPermissionJpaRepository;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.fixture.EndpointPermissionFixture;
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
 * EndpointPermissionCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("EndpointPermissionCommandAdapter 단위 테스트")
class EndpointPermissionCommandAdapterTest {

    @Mock private EndpointPermissionJpaRepository repository;

    @Mock private EndpointPermissionJpaEntityMapper mapper;

    private EndpointPermissionCommandAdapter adapter;

    private static final UUID ENDPOINT_PERMISSION_UUID = UUID.randomUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new EndpointPermissionCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("Public 엔드포인트 권한을 성공적으로 저장한다")
        void shouldPersistPublicEndpointPermissionSuccessfully() {
            // given
            EndpointPermission domainToSave = EndpointPermissionFixture.createPublic();
            EndpointPermission savedDomain = EndpointPermissionFixture.createPublic();

            EndpointPermissionJpaEntity entityToSave = createPublicEntity(null);
            EndpointPermissionJpaEntity savedEntity = createPublicEntity(1L);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            EndpointPermission result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            assertThat(result.isPublic()).isTrue();
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("Protected 엔드포인트 권한을 성공적으로 저장한다")
        void shouldPersistProtectedEndpointPermissionSuccessfully() {
            // given
            EndpointPermission domainToSave = EndpointPermissionFixture.createProtected();
            EndpointPermission savedDomain = EndpointPermissionFixture.createProtected();

            EndpointPermissionJpaEntity entityToSave = createProtectedEntity(null);
            EndpointPermissionJpaEntity savedEntity = createProtectedEntity(1L);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            EndpointPermission result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            assertThat(result.isPublic()).isFalse();
            verify(repository).save(entityToSave);
        }

        @Test
        @DisplayName("기존 엔드포인트 권한을 수정한다")
        void shouldUpdateExistingEndpointPermission() {
            // given
            EndpointPermission existingDomain = EndpointPermissionFixture.createProtected();
            EndpointPermission updatedDomain = EndpointPermissionFixture.createProtected();

            EndpointPermissionJpaEntity entityToUpdate = createProtectedEntity(1L);
            EndpointPermissionJpaEntity updatedEntity = createProtectedEntity(1L);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            EndpointPermission result = adapter.persist(existingDomain);

            // then
            assertThat(result).isEqualTo(updatedDomain);
            verify(repository).save(entityToUpdate);
        }

        @Test
        @DisplayName("Path Variable 포함 엔드포인트를 저장한다")
        void shouldPersistEndpointWithPathVariable() {
            // given
            EndpointPermission domainToSave = EndpointPermissionFixture.createWithPathVariable();
            EndpointPermission savedDomain = EndpointPermissionFixture.createWithPathVariable();

            EndpointPermissionJpaEntity entityToSave = createPathVariableEntity(null);
            EndpointPermissionJpaEntity savedEntity = createPathVariableEntity(1L);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            EndpointPermission result = adapter.persist(domainToSave);

            // then
            assertThat(result.pathValue()).contains("{userId}");
        }
    }

    private EndpointPermissionJpaEntity createPublicEntity(Long id) {
        return EndpointPermissionJpaEntity.of(
                id,
                ENDPOINT_PERMISSION_UUID,
                "auth-hub",
                "/api/v1/health",
                "GET",
                "헬스체크 엔드포인트",
                true,
                null,
                null,
                0L,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    private EndpointPermissionJpaEntity createProtectedEntity(Long id) {
        return EndpointPermissionJpaEntity.of(
                id,
                ENDPOINT_PERMISSION_UUID,
                "auth-hub",
                "/api/v1/users",
                "GET",
                "사용자 목록 조회",
                false,
                "user:read",
                "ADMIN,USER_MANAGER",
                0L,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }

    private EndpointPermissionJpaEntity createPathVariableEntity(Long id) {
        return EndpointPermissionJpaEntity.of(
                id,
                ENDPOINT_PERMISSION_UUID,
                "auth-hub",
                "/api/v1/users/{userId}",
                "GET",
                "사용자 상세 조회",
                false,
                "user:read",
                "ADMIN,USER_MANAGER",
                0L,
                false,
                FIXED_TIME,
                FIXED_TIME);
    }
}
