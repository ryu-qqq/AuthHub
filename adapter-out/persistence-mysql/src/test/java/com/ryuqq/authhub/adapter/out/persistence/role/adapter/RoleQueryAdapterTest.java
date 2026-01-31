package com.ryuqq.authhub.adapter.out.persistence.role.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.role.entity.RoleJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.role.fixture.RoleJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.role.mapper.RoleJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.role.repository.RoleQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
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
 * RoleQueryAdapter 단위 테스트
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
@DisplayName("RoleQueryAdapter 단위 테스트")
class RoleQueryAdapterTest {

    @Mock private RoleQueryDslRepository repository;

    @Mock private RoleJpaEntityMapper mapper;

    private RoleQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RoleQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            RoleId id = RoleFixture.defaultId();
            RoleJpaEntity entity = RoleJpaEntityFixture.create();
            Role expectedDomain = RoleFixture.create();

            given(repository.findByRoleId(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Role> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(repository.findByRoleId(id.value())).willReturn(Optional.empty());

            // when
            Optional<Role> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("RoleId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(repository.findByRoleId(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByRoleId(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(repository.existsByRoleId(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(repository.existsByRoleId(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleFixture.defaultRoleName();

            given(repository.existsByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(true);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleFixture.defaultRoleName();

            given(repository.existsByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(false);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Global 역할 확인 시 tenantId가 null로 전달됨")
        void shouldPassNullTenantId_WhenCheckingGlobalRole() {
            // given
            RoleName name = RoleFixture.defaultRoleName();

            given(repository.existsByTenantIdAndName(null, name.value())).willReturn(true);

            // when
            boolean result = sut.existsByTenantIdAndName(null, name);

            // then
            assertThat(result).isTrue();
            then(repository).should().existsByTenantIdAndName(null, name.value());
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndName 메서드")
    class FindByTenantIdAndName {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleFixture.defaultRoleName();
            RoleJpaEntity entity = RoleJpaEntityFixture.createWithTenant();
            Role expectedDomain = RoleFixture.createTenantRole();

            given(repository.findByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Role> result = sut.findByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleFixture.defaultRoleName();

            given(repository.findByTenantIdAndName(tenantId.value(), name.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<Role> result = sut.findByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Entity 목록을 Domain 목록으로 변환하여 반환")
        void shouldFindAndConvertAll_ThenReturnDomainList() {
            // given
            RoleSearchCriteria criteria = createTestCriteria();
            RoleJpaEntity entity1 = RoleJpaEntityFixture.createWithName("ROLE_1");
            RoleJpaEntity entity2 = RoleJpaEntityFixture.createWithName("ROLE_2");
            Role domain1 = RoleFixture.createCustomRoleWithName("ROLE_1");
            Role domain2 = RoleFixture.createCustomRoleWithName("ROLE_2");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Role> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            RoleSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Role> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Repository 결과를 그대로 반환")
        void shouldReturnCount_FromRepository() {
            // given
            RoleSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(10L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(10L);
        }

        @Test
        @DisplayName("결과가 없으면 0 반환")
        void shouldReturnZero_WhenNoResults() {
            // given
            RoleSearchCriteria criteria = createTestCriteria();

            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIds {

        @Test
        @DisplayName("성공: ID 목록으로 Entity 조회 후 Domain 변환")
        void shouldFindAndConvertAll_ByIds() {
            // given
            List<RoleId> ids = List.of(RoleId.of(1L), RoleId.of(2L));
            List<Long> idValues = List.of(1L, 2L);
            RoleJpaEntity entity1 = RoleJpaEntityFixture.createWithId(1L);
            RoleJpaEntity entity2 = RoleJpaEntityFixture.createWithId(2L);
            Role domain1 = RoleFixture.create();
            Role domain2 = RoleFixture.create();

            given(repository.findAllByIds(idValues)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Role> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록이면 빈 목록 반환")
        void shouldReturnEmptyList_WhenIdsEmpty() {
            // given
            List<RoleId> ids = List.of();
            List<Long> idValues = List.of();

            given(repository.findAllByIds(idValues)).willReturn(List.of());

            // when
            List<Role> result = sut.findAllByIds(ids);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ==================== Helper Methods ====================

    private RoleSearchCriteria createTestCriteria() {
        return RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);
    }
}
