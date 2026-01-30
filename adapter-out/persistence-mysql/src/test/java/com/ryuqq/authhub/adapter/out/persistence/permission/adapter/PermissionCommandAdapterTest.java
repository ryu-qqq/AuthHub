package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionJpaRepository;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
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
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>Repository/Mapper를 Mock으로 대체
 *   <li>위임 및 변환 흐름 검증
 *   <li>Global Only 설계 (tenantId 없음)
 * </ul>
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

    private PermissionCommandAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class Persist {

        @Test
        @DisplayName("성공: Domain → Entity 변환 후 저장하고 ID 반환")
        void shouldConvertAndPersist_ThenReturnId() {
            // given
            Permission domain = PermissionFixture.create();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            Long expectedId = PermissionJpaEntityFixture.defaultPermissionId();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(domain);

            // then
            assertThat(result).isEqualTo(expectedId);
        }

        @Test
        @DisplayName("Mapper를 통해 Domain → Entity 변환")
        void shouldUseMapper_ToConvertDomainToEntity() {
            // given
            Permission domain = PermissionFixture.create();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
        }

        @Test
        @DisplayName("Repository를 통해 Entity 저장")
        void shouldUseRepository_ToSaveEntity() {
            // given
            Permission domain = PermissionFixture.create();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            sut.persist(domain);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("새 Domain 저장 시에도 동일한 흐름")
        void shouldFollowSameFlow_WhenPersistingNewDomain() {
            // given
            Permission newDomain = PermissionFixture.createNewCustomPermission();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();

            given(mapper.toEntity(newDomain)).willReturn(entity);
            given(repository.save(any(PermissionJpaEntity.class))).willReturn(entity);

            // when
            Long result = sut.persist(newDomain);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(newDomain);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("시스템 권한 저장 시에도 동일한 흐름")
        void shouldFollowSameFlow_WhenPersistingSystemPermission() {
            // given
            Permission systemPermission = PermissionFixture.createSystemPermission();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.createSystemPermission();

            given(mapper.toEntity(systemPermission)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = sut.persist(systemPermission);

            // then
            assertThat(result).isNotNull();
            then(mapper).should().toEntity(systemPermission);
            then(repository).should().save(entity);
        }
    }
}
