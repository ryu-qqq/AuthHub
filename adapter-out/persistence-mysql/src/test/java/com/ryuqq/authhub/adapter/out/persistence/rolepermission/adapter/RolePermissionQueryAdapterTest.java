package com.ryuqq.authhub.adapter.out.persistence.rolepermission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.RolePermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.fixture.RolePermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.mapper.RolePermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.rolepermission.repository.RolePermissionQueryDslRepository;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.rolepermission.fixture.RolePermissionFixture;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * RolePermissionQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>QueryDslRepository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RolePermissionQueryAdapter 단위 테스트")
class RolePermissionQueryAdapterTest {

    @Mock private RolePermissionQueryDslRepository repository;

    @Mock private RolePermissionJpaEntityMapper mapper;

    private RolePermissionQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("exists 메서드")
    class Exists {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(2L);
            given(repository.exists(1L, 2L)).willReturn(true);

            // when
            boolean result = sut.exists(roleId, permissionId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(2L);
            given(repository.exists(1L, 2L)).willReturn(false);

            // when
            boolean result = sut.exists(roleId, permissionId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByRoleIdAndPermissionId 메서드")
    class FindByRoleIdAndPermissionId {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(2L);
            RolePermissionJpaEntity entity = RolePermissionJpaEntityFixture.create();
            RolePermission expectedDomain = RolePermissionFixture.create();
            given(repository.findByRoleIdAndPermissionId(1L, 2L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<RolePermission> result = sut.findByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            RoleId roleId = RoleId.of(1L);
            PermissionId permissionId = PermissionId.of(2L);
            given(repository.findByRoleIdAndPermissionId(1L, 2L)).willReturn(Optional.empty());

            // when
            Optional<RolePermission> result = sut.findByRoleIdAndPermissionId(roleId, permissionId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByRoleId 메서드")
    class FindAllByRoleId {

        @Test
        @DisplayName("성공: Entity 목록 조회 후 Domain 목록으로 변환")
        void shouldFindAllAndConvert() {
            // given
            RoleId roleId = RoleId.of(1L);
            List<RolePermissionJpaEntity> entities =
                    List.of(RolePermissionJpaEntityFixture.create());
            List<RolePermission> domains = List.of(RolePermissionFixture.create());
            given(repository.findAllByRoleId(1L)).willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(domains.get(0));

            // when
            List<RolePermission> result = sut.findAllByRoleId(roleId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domains.get(0));
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResult() {
            // given
            RoleId roleId = RoleId.of(1L);
            given(repository.findAllByRoleId(1L)).willReturn(List.of());

            // when
            List<RolePermission> result = sut.findAllByRoleId(roleId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByPermissionId 메서드")
    class FindAllByPermissionId {

        @Test
        @DisplayName("성공: Entity 목록 조회 후 Domain 목록으로 변환")
        void shouldFindAllAndConvert() {
            // given
            PermissionId permissionId = PermissionId.of(2L);
            List<RolePermissionJpaEntity> entities =
                    List.of(RolePermissionJpaEntityFixture.create());
            given(repository.findAllByPermissionId(2L)).willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(RolePermissionFixture.create());

            // when
            List<RolePermission> result = sut.findAllByPermissionId(permissionId);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("existsByPermissionId 메서드")
    class ExistsByPermissionId {

        @Test
        @DisplayName("사용 중이면 true 반환")
        void shouldReturnTrue_WhenInUse() {
            // given
            PermissionId permissionId = PermissionId.of(2L);
            given(repository.existsByPermissionId(2L)).willReturn(true);

            // when
            boolean result = sut.existsByPermissionId(permissionId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("사용 중이 아니면 false 반환")
        void shouldReturnFalse_WhenNotInUse() {
            // given
            PermissionId permissionId = PermissionId.of(2L);
            given(repository.existsByPermissionId(2L)).willReturn(false);

            // when
            boolean result = sut.existsByPermissionId(permissionId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: 조건 검색 후 Domain 목록 반환")
        void shouldFindByCriteriaAndConvert() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(RoleId.of(1L), 0, 10);
            List<RolePermissionJpaEntity> entities =
                    List.of(RolePermissionJpaEntityFixture.create());
            given(repository.findAllByCriteria(criteria)).willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(RolePermissionFixture.create());

            // when
            List<RolePermission> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(repository).should().findAllByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: 조건에 맞는 개수 반환")
        void shouldReturnCount() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(RoleId.of(1L), 0, 10);
            given(repository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("findGrantedPermissionIds 메서드")
    class FindGrantedPermissionIds {

        @Test
        @DisplayName("성공: 이미 부여된 권한 ID 목록 반환")
        void shouldReturnGrantedPermissionIds() {
            // given
            RoleId roleId = RoleId.of(1L);
            List<PermissionId> permissionIds = List.of(PermissionId.of(2L), PermissionId.of(3L));
            given(repository.findGrantedPermissionIds(1L, List.of(2L, 3L))).willReturn(List.of(2L));

            // when
            List<PermissionId> result = sut.findGrantedPermissionIds(roleId, permissionIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).value()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("findAllByRoleIds 메서드")
    class FindAllByRoleIds {

        @Test
        @DisplayName("성공: 여러 역할의 권한 목록 조회 후 Domain 목록 반환")
        void shouldFindAllByRoleIdsAndConvert() {
            // given
            List<RoleId> roleIds = List.of(RoleId.of(1L), RoleId.of(2L));
            List<RolePermissionJpaEntity> entities =
                    List.of(RolePermissionJpaEntityFixture.create());
            given(repository.findAllByRoleIds(List.of(1L, 2L))).willReturn(entities);
            given(mapper.toDomain(entities.get(0))).willReturn(RolePermissionFixture.create());

            // when
            List<RolePermission> result = sut.findAllByRoleIds(roleIds);

            // then
            assertThat(result).hasSize(1);
        }
    }
}
