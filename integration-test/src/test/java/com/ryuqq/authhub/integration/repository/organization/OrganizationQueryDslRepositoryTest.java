package com.ryuqq.authhub.integration.repository.organization;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.fixture.OrganizationJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSearchField;
import com.ryuqq.authhub.domain.organization.vo.OrganizationSortKey;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Organization QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findByOrganizationId - ID로 단건 조회
 *   <li>existsByOrganizationId - ID 존재 여부
 *   <li>existsByTenantIdAndName - 테넌트 내 이름 중복 확인
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.ORGANIZATION)
@DisplayName("조직 QueryDSL Repository 테스트")
class OrganizationQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private OrganizationJpaRepository jpaRepository;
    @Autowired private OrganizationQueryDslRepository queryDslRepository;
    @Autowired private TenantJpaRepository tenantJpaRepository;

    private TenantJpaEntity savedTenant;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        tenantJpaRepository.deleteAll();

        savedTenant = tenantJpaRepository.save(TenantJpaEntityFixture.create());

        flushAndClear();
    }

    @Nested
    @DisplayName("findByOrganizationId 테스트")
    class FindByOrganizationIdTest {

        @Test
        @DisplayName("조직 조회 성공")
        void shouldFindOrganization() {
            // given
            OrganizationJpaEntity entity =
                    jpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenant(
                                    savedTenant.getTenantId()));
            flushAndClear();

            // when
            Optional<OrganizationJpaEntity> found =
                    queryDslRepository.findByOrganizationId(entity.getOrganizationId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Organization");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<OrganizationJpaEntity> found =
                    queryDslRepository.findByOrganizationId("01941234-5678-7000-8000-000000000000");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByOrganizationId 테스트")
    class ExistsByOrganizationIdTest {

        @Test
        @DisplayName("존재하는 조직 ID - true")
        void shouldReturnTrueForExistingOrganizationId() {
            // given
            OrganizationJpaEntity entity =
                    jpaRepository.save(
                            OrganizationJpaEntityFixture.createWithTenant(
                                    savedTenant.getTenantId()));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByOrganizationId(entity.getOrganizationId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 조직 ID - false")
        void shouldReturnFalseForNonExistentOrganizationId() {
            // when
            boolean exists =
                    queryDslRepository.existsByOrganizationId(
                            "01941234-5678-7000-8000-000000000000");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 테스트")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 내 조직명 존재 확인 - true")
        void shouldReturnTrueForExistingNameInTenant() {
            // given
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "고유조직"));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByTenantIdAndName(savedTenant.getTenantId(), "고유조직");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 조직명 - false")
        void shouldReturnFalseForNonExistentName() {
            // when
            boolean exists =
                    queryDslRepository.existsByTenantIdAndName(
                            savedTenant.getTenantId(), "존재하지않는조직");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("테넌트별 조직 목록 조회")
        void shouldFindAllByTenantId() {
            // given
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "조직1"));
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "조직2"));
            flushAndClear();

            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.of(savedTenant.getTenantId())),
                            null,
                            OrganizationSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<OrganizationJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("이름 검색 - 부분 일치")
        void shouldSearchByName() {
            // given
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "ABC조직"));
            flushAndClear();

            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.of(savedTenant.getTenantId())),
                            "ABC",
                            OrganizationSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<OrganizationJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).contains("ABC");
        }

        @Test
        @DisplayName("활성 상태 필터")
        void shouldFilterByActiveStatus() {
            // given
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "활성조직"));
            jpaRepository.save(OrganizationJpaEntityFixture.createInactive());
            flushAndClear();

            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.of(savedTenant.getTenantId())),
                            null,
                            OrganizationSearchField.NAME,
                            List.of(OrganizationStatus.ACTIVE),
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<OrganizationJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(OrganizationStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 조직 수 조회")
        void shouldCountByCriteria() {
            // given
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "카운트1"));
            jpaRepository.save(
                    OrganizationJpaEntityFixture.createWithTenantAndName(
                            savedTenant.getTenantId(), "카운트2"));
            flushAndClear();

            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of(
                            List.of(TenantId.of(savedTenant.getTenantId())),
                            "카운트",
                            OrganizationSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            OrganizationSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }
    }
}
