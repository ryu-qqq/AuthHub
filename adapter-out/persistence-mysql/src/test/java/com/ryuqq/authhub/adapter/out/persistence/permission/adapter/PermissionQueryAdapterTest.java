package com.ryuqq.authhub.adapter.out.persistence.permission.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.permission.fixture.PermissionJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.permission.mapper.PermissionJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.repository.PermissionQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.service.id.ServiceId;
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
 * PermissionQueryAdapter 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Adapter는 Repository 위임 + Mapper 변환 담당
 *   <li>QueryDslRepository/Mapper를 Mock으로 대체
 *   <li>Entity → Domain 변환 흐름 검증
 *   <li>Global Only 설계 (tenantId 없음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionQueryAdapter 단위 테스트")
class PermissionQueryAdapterTest {

    @Mock private PermissionQueryDslRepository repository;

    @Mock private PermissionJpaEntityMapper mapper;

    private PermissionQueryAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            Permission expectedDomain = PermissionFixture.create();

            given(repository.findByPermissionId(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Permission> result = sut.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(repository.findByPermissionId(id.value())).willReturn(Optional.empty());

            // when
            Optional<Permission> result = sut.findById(id);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PermissionId에서 value 추출하여 Repository 호출")
        void shouldExtractIdValue_AndCallRepository() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(repository.findByPermissionId(id.value())).willReturn(Optional.empty());

            // when
            sut.findById(id);

            // then
            then(repository).should().findByPermissionId(id.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(repository.existsByPermissionId(id.value())).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(repository.existsByPermissionId(id.value())).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByServiceIdAndPermissionKey 메서드")
    class ExistsByServiceIdAndPermissionKey {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            String permissionKey = PermissionFixture.defaultPermissionKey();

            given(repository.existsByServiceIdAndPermissionKey(1L, permissionKey)).willReturn(true);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            String permissionKey = PermissionFixture.defaultPermissionKey();

            given(repository.existsByServiceIdAndPermissionKey(1L, permissionKey))
                    .willReturn(false);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Repository에 serviceId와 permissionKey 전달")
        void shouldPassServiceIdAndPermissionKey_ToRepository() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            String permissionKey = "organization:manage";

            given(repository.existsByServiceIdAndPermissionKey(1L, permissionKey)).willReturn(true);

            // when
            sut.existsByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            then(repository).should().existsByServiceIdAndPermissionKey(1L, permissionKey);
        }
    }

    @Nested
    @DisplayName("findByServiceIdAndPermissionKey 메서드")
    class FindByServiceIdAndPermissionKey {

        @Test
        @DisplayName("성공: Entity 조회 후 Domain으로 변환하여 반환")
        void shouldFindAndConvert_WhenEntityExists() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            String permissionKey = PermissionFixture.defaultPermissionKey();
            PermissionJpaEntity entity = PermissionJpaEntityFixture.create();
            Permission expectedDomain = PermissionFixture.create();

            given(repository.findByServiceIdAndPermissionKey(1L, permissionKey))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Permission> result =
                    sut.findByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
        }

        @Test
        @DisplayName("Entity가 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenEntityNotFound() {
            // given
            ServiceId serviceId = ServiceId.of(1L);
            String permissionKey = "nonexistent:key";

            given(repository.findByServiceIdAndPermissionKey(1L, permissionKey))
                    .willReturn(Optional.empty());

            // when
            Optional<Permission> result =
                    sut.findByServiceIdAndPermissionKey(serviceId, permissionKey);

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
            PermissionSearchCriteria criteria = createTestCriteria();
            PermissionJpaEntity entity1 =
                    PermissionJpaEntityFixture.createWithResourceAndAction("user", "read");
            PermissionJpaEntity entity2 =
                    PermissionJpaEntityFixture.createWithResourceAndAction("user", "write");
            Permission domain1 = PermissionFixture.createWithResourceAndAction("user", "read");
            Permission domain2 = PermissionFixture.createWithResourceAndAction("user", "write");

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Permission> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoResults() {
            // given
            PermissionSearchCriteria criteria = createTestCriteria();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Permission> result = sut.findAllBySearchCriteria(criteria);

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
            PermissionSearchCriteria criteria = createTestCriteria();

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
            PermissionSearchCriteria criteria = createTestCriteria();

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
            List<PermissionId> ids = List.of(PermissionId.of(1L), PermissionId.of(2L));
            List<Long> idValues = List.of(1L, 2L);
            PermissionJpaEntity entity1 = PermissionJpaEntityFixture.createWithId(1L);
            PermissionJpaEntity entity2 = PermissionJpaEntityFixture.createWithId(2L);
            Permission domain1 = PermissionFixture.create();
            Permission domain2 = PermissionFixture.create();

            given(repository.findAllByIds(idValues)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Permission> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록이면 빈 목록 반환")
        void shouldReturnEmptyList_WhenIdsEmpty() {
            // given
            List<PermissionId> ids = List.of();
            List<Long> idValues = List.of();

            given(repository.findAllByIds(idValues)).willReturn(List.of());

            // when
            List<Permission> result = sut.findAllByIds(ids);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ==================== Helper Methods ====================

    private PermissionSearchCriteria createTestCriteria() {
        return PermissionSearchCriteria.ofDefault(
                null, null, null, DateRange.of(null, null), 0, 10);
    }
}
