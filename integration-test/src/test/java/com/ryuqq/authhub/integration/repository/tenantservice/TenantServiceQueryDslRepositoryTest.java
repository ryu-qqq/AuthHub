package com.ryuqq.authhub.integration.repository.tenantservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.TenantServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.fixture.TenantServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenantservice.repository.TenantServiceQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import com.ryuqq.authhub.integration.common.base.RepositoryTestBase;
import com.ryuqq.authhub.integration.common.tag.TestTags;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TenantService QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findById - ID로 단건 조회
 *   <li>existsById - ID 존재 여부
 *   <li>existsByTenantIdAndServiceId - 테넌트-서비스 복합 존재 여부
 *   <li>findByTenantIdAndServiceId - 테넌트-서비스 복합 조회
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.TENANT_SERVICE)
@DisplayName("테넌트-서비스 QueryDSL Repository 테스트")
class TenantServiceQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private TenantServiceJpaRepository jpaRepository;
    @Autowired private TenantServiceQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("테넌트-서비스 조회 성공")
        void shouldFindTenantService() {
            // given
            TenantServiceJpaEntity entity =
                    jpaRepository.save(TenantServiceJpaEntityFixture.createNew());
            flushAndClear();

            // when
            Optional<TenantServiceJpaEntity> found = queryDslRepository.findById(entity.getId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getTenantId()).isEqualTo(entity.getTenantId());
            assertThat(found.get().getServiceId()).isEqualTo(entity.getServiceId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<TenantServiceJpaEntity> found = queryDslRepository.findById(999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById 테스트")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 테넌트-서비스 ID - true")
        void shouldReturnTrueForExistingId() {
            // given
            TenantServiceJpaEntity entity =
                    jpaRepository.save(TenantServiceJpaEntityFixture.createNew());
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsById(entity.getId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트-서비스 ID - false")
        void shouldReturnFalseForNonExistentId() {
            // when
            boolean exists = queryDslRepository.existsById(999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndServiceId 테스트")
    class ExistsByTenantIdAndServiceIdTest {

        @Test
        @DisplayName("존재하는 테넌트-서비스 조합 - true")
        void shouldReturnTrueForExistingCombo() {
            // given
            TenantServiceJpaEntity entity =
                    jpaRepository.save(
                            TenantServiceJpaEntityFixture.createWithTenantAndService(
                                    "01941234-5678-7000-8000-123456789abc", 1L));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByTenantIdAndServiceId(
                            "01941234-5678-7000-8000-123456789abc", 1L);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트-서비스 조합 - false")
        void shouldReturnFalseForNonExistentCombo() {
            // when
            boolean exists =
                    queryDslRepository.existsByTenantIdAndServiceId(
                            "01941234-5678-7000-8000-999999999999", 999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByTenantIdAndServiceId 테스트")
    class FindByTenantIdAndServiceIdTest {

        @Test
        @DisplayName("테넌트-서비스 조합으로 조회 성공")
        void shouldFindByCombo() {
            // given
            TenantServiceJpaEntity entity =
                    jpaRepository.save(
                            TenantServiceJpaEntityFixture.createWithTenantAndService(
                                    "01941234-5678-7000-8000-123456789abc", 1L));
            flushAndClear();

            // when
            Optional<TenantServiceJpaEntity> found =
                    queryDslRepository.findByTenantIdAndServiceId(
                            "01941234-5678-7000-8000-123456789abc", 1L);

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getTenantId()).isEqualTo("01941234-5678-7000-8000-123456789abc");
            assertThat(found.get().getServiceId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 조합 조회시 빈 Optional 반환")
        void shouldReturnEmptyForNonExistentCombo() {
            // when
            Optional<TenantServiceJpaEntity> found =
                    queryDslRepository.findByTenantIdAndServiceId(
                            "01941234-5678-7000-8000-999999999999", 999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("전체 테넌트-서비스 목록 조회")
        void shouldFindAllTenantServices() {
            // given
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000002", 1L));
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("테넌트 ID로 필터링")
        void shouldFilterByTenantId() {
            // given
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000002", 1L));
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-000000000001",
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTenantId())
                    .isEqualTo("01941234-5678-7000-8000-000000000001");
        }

        @Test
        @DisplayName("서비스 ID로 필터링")
        void shouldFilterByServiceId() {
            // given
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 2L));
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            1L,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getServiceId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("활성 상태 필터")
        void shouldFilterByActiveStatus() {
            // given
            jpaRepository.save(TenantServiceJpaEntityFixture.createNew());
            jpaRepository.save(TenantServiceJpaEntityFixture.createNewInactive());
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            List.of(TenantServiceStatus.ACTIVE),
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(TenantServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("다중 상태 필터")
        void shouldFilterByMultipleStatuses() {
            // given
            jpaRepository.save(TenantServiceJpaEntityFixture.createNew());
            jpaRepository.save(TenantServiceJpaEntityFixture.createNewInactive());
            jpaRepository.save(TenantServiceJpaEntityFixture.createNewSuspended());
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            List.of(TenantServiceStatus.ACTIVE, TenantServiceStatus.SUSPENDED),
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(
                            entity ->
                                    entity.getStatus() == TenantServiceStatus.ACTIVE
                                            || entity.getStatus() == TenantServiceStatus.SUSPENDED);
        }

        @Test
        @DisplayName("날짜 범위 필터 - 구독일시 기준")
        void shouldFilterByDateRange() {
            // given
            LocalDate startDate = LocalDate.parse("2024-12-31");
            LocalDate endDate = LocalDate.parse("2025-01-02");

            jpaRepository.save(TenantServiceJpaEntityFixture.createNew());
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(startDate, endDate),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("구독일시 오름차순 정렬")
        void shouldSortBySubscribedAtAsc() {
            // given
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000002", 1L));
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSubscribedAt())
                    .isBeforeOrEqualTo(result.get(1).getSubscribedAt());
        }

        @Test
        @DisplayName("페이징 - offset과 limit 적용")
        void shouldApplyPagination() {
            // given
            for (int i = 1; i <= 5; i++) {
                jpaRepository.save(
                        TenantServiceJpaEntityFixture.createWithTenantAndService(
                                "01941234-5678-7000-8000-00000000000" + i, 1L));
            }
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(1, 2));

            // when
            List<TenantServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 테넌트-서비스 수 조회")
        void shouldCountByCriteria() {
            // given
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 1L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000001", 2L));
            jpaRepository.save(
                    TenantServiceJpaEntityFixture.createWithTenantAndService(
                            "01941234-5678-7000-8000-000000000002", 1L));
            flushAndClear();

            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-000000000001",
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("조건에 맞는 테넌트-서비스가 없으면 0 반환")
        void shouldReturnZeroWhenNoMatches() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-999999999999",
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
