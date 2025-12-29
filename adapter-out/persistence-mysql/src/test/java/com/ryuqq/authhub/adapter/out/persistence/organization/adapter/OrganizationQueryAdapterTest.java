package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
 * OrganizationQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationQueryAdapter 단위 테스트")
class OrganizationQueryAdapterTest {

    @Mock private OrganizationQueryDslRepository repository;

    @Mock private OrganizationJpaEntityMapper mapper;

    private OrganizationQueryAdapter adapter;

    private static final UUID ORG_UUID = OrganizationFixture.defaultUUID();
    private static final UUID TENANT_UUID = OrganizationFixture.defaultTenantUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new OrganizationQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 조직을 성공적으로 조회한다")
        void shouldFindOrganizationByIdSuccessfully() {
            // given
            OrganizationId organizationId = OrganizationId.of(ORG_UUID);
            Organization expectedOrg = OrganizationFixture.create();
            OrganizationJpaEntity entity = createOrganizationEntity();

            given(repository.findByOrganizationId(ORG_UUID)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedOrg);

            // when
            Optional<Organization> result = adapter.findById(organizationId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedOrg);
            verify(repository).findByOrganizationId(ORG_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenOrganizationNotFound() {
            // given
            OrganizationId organizationId = OrganizationId.of(UUID.randomUUID());

            given(repository.findByOrganizationId(organizationId.value()))
                    .willReturn(Optional.empty());

            // when
            Optional<Organization> result = adapter.findById(organizationId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsByIdTest {

        @Test
        @DisplayName("조직 ID가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenIdExists() {
            // given
            OrganizationId organizationId = OrganizationId.of(ORG_UUID);

            given(repository.existsByOrganizationId(ORG_UUID)).willReturn(true);

            // when
            boolean result = adapter.existsById(organizationId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("조직 ID가 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenIdNotExists() {
            // given
            OrganizationId organizationId = OrganizationId.of(UUID.randomUUID());

            given(repository.existsByOrganizationId(organizationId.value())).willReturn(false);

            // when
            boolean result = adapter.existsById(organizationId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 내 조직명이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenNameExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationName name = OrganizationName.of("Test Organization");

            given(repository.existsByTenantIdAndName(TENANT_UUID, "Test Organization"))
                    .willReturn(true);

            // when
            boolean result = adapter.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("테넌트 내 조직명이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenNameNotExists() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationName name = OrganizationName.of("Nonexistent Organization");

            given(repository.existsByTenantIdAndName(TENANT_UUID, "Nonexistent Organization"))
                    .willReturn(false);

            // when
            boolean result = adapter.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("Criteria 기반으로 조직 목록을 조회한다")
        void shouldFindAllByCriteria() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId, null, null, null, null, null, null, PageRequest.of(0, 20));
            Organization org1 = OrganizationFixture.create();
            Organization org2 = OrganizationFixture.createWithName("Another Organization");
            OrganizationJpaEntity entity1 = createOrganizationEntity();
            OrganizationJpaEntity entity2 = createOrganizationEntity();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(any(OrganizationJpaEntity.class))).willReturn(org1, org2);

            // when
            List<Organization> results = adapter.findAllByCriteria(criteria);

            // then
            assertThat(results).hasSize(2);
            verify(repository).findAllByCriteria(criteria);
        }

        @Test
        @DisplayName("이름 필터로 조직을 검색한다")
        void shouldFindAllWithNameFilter() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId, "Test", null, null, null, null, null, PageRequest.of(0, 20));
            Organization org = OrganizationFixture.create();
            OrganizationJpaEntity entity = createOrganizationEntity();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(any(OrganizationJpaEntity.class))).willReturn(org);

            // when
            List<Organization> results = adapter.findAllByCriteria(criteria);

            // then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("상태 필터로 조직을 검색한다")
        void shouldFindAllWithStatusFilter() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId,
                            null,
                            null,
                            List.of(OrganizationStatus.ACTIVE),
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));
            Organization org = OrganizationFixture.create();
            OrganizationJpaEntity entity = createOrganizationEntity();

            given(repository.findAllByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(any(OrganizationJpaEntity.class))).willReturn(org);

            // when
            List<Organization> results = adapter.findAllByCriteria(criteria);

            // then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId,
                            "nonexistent",
                            null,
                            null,
                            null,
                            null,
                            null,
                            PageRequest.of(0, 20));

            given(repository.findAllByCriteria(criteria)).willReturn(List.of());

            // when
            List<Organization> results = adapter.findAllByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria 메서드")
    class CountByCriteriaTest {

        @Test
        @DisplayName("Criteria 기반으로 조직 개수를 조회한다")
        void shouldCountByCriteria() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId, null, null, null, null, null, null, PageRequest.of(0, 20));

            given(repository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
            verify(repository).countByCriteria(criteria);
        }

        @Test
        @DisplayName("이름 필터로 조직 개수를 조회한다")
        void shouldCountWithNameFilter() {
            // given
            TenantId tenantId = TenantId.of(TENANT_UUID);
            OrganizationCriteria criteria =
                    OrganizationCriteria.of(
                            tenantId, "Test", null, null, null, null, null, PageRequest.of(0, 20));

            given(repository.countByCriteria(criteria)).willReturn(2L);

            // when
            long result = adapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(2L);
        }
    }

    private OrganizationJpaEntity createOrganizationEntity() {
        return OrganizationJpaEntity.of(
                1L,
                ORG_UUID,
                TENANT_UUID,
                "Test Organization",
                OrganizationStatus.ACTIVE,
                FIXED_TIME,
                FIXED_TIME);
    }
}
