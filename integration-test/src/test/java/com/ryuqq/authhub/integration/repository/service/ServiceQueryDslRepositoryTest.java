package com.ryuqq.authhub.integration.repository.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.service.entity.ServiceJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.service.fixture.ServiceJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.service.repository.ServiceQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.service.query.criteria.ServiceSearchCriteria;
import com.ryuqq.authhub.domain.service.vo.ServiceSearchField;
import com.ryuqq.authhub.domain.service.vo.ServiceSortKey;
import com.ryuqq.authhub.domain.service.vo.ServiceStatus;
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
 * Service QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findByServiceId - ID로 단건 조회
 *   <li>existsByServiceId - ID 존재 여부
 *   <li>existsByServiceCode - 서비스 코드 존재 여부
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 *   <li>findAllActive - 활성 상태 전체 조회
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.SERVICE)
@DisplayName("서비스 QueryDSL Repository 테스트")
class ServiceQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private ServiceJpaRepository jpaRepository;
    @Autowired private ServiceQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("findByServiceId 테스트")
    class FindByServiceIdTest {

        @Test
        @DisplayName("서비스 조회 성공")
        void shouldFindService() {
            // given
            ServiceJpaEntity entity = jpaRepository.save(ServiceJpaEntityFixture.createNew());
            flushAndClear();

            // when
            Optional<ServiceJpaEntity> found =
                    queryDslRepository.findByServiceId(entity.getServiceId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getServiceCode()).isEqualTo("SVC_NEW");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<ServiceJpaEntity> found = queryDslRepository.findByServiceId(999L);

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByServiceId 테스트")
    class ExistsByServiceIdTest {

        @Test
        @DisplayName("존재하는 서비스 ID - true")
        void shouldReturnTrueForExistingServiceId() {
            // given
            ServiceJpaEntity entity = jpaRepository.save(ServiceJpaEntityFixture.createNew());
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByServiceId(entity.getServiceId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 서비스 ID - false")
        void shouldReturnFalseForNonExistentServiceId() {
            // when
            boolean exists = queryDslRepository.existsByServiceId(999L);

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByServiceCode 테스트")
    class ExistsByServiceCodeTest {

        @Test
        @DisplayName("서비스 코드 존재 확인 - true")
        void shouldReturnTrueForExistingServiceCode() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createWithCode("SVC_UNIQUE"));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByServiceCode("SVC_UNIQUE");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 서비스 코드 - false")
        void shouldReturnFalseForNonExistentServiceCode() {
            // when
            boolean exists = queryDslRepository.existsByServiceCode("SVC_NONEXISTENT");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("전체 서비스 목록 조회")
        void shouldFindAllServices() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_A", "서비스 A"));
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_B", "서비스 B"));
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("서비스 코드 검색 - 부분 일치")
        void shouldSearchByServiceCode() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createWithCode("SVC_STORE"));
            jpaRepository.save(ServiceJpaEntityFixture.createWithCode("SVC_B2B"));
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "STORE",
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getServiceCode()).contains("STORE");
        }

        @Test
        @DisplayName("이름 검색 - 부분 일치")
        void shouldSearchByName() {
            // given
            jpaRepository.save(
                    ServiceJpaEntityFixture.createWithCodeAndName("SVC_SHOP", "자사몰 서비스"));
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_B2B", "B2B 서비스"));
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "자사",
                            ServiceSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).contains("자사");
        }

        @Test
        @DisplayName("활성 상태 필터")
        void shouldFilterByActiveStatus() {
            // given
            jpaRepository.save(
                    ServiceJpaEntity.of(
                            null,
                            "SVC_ACTIVE",
                            "활성 서비스",
                            "활성 설명",
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime()));
            jpaRepository.save(ServiceJpaEntityFixture.createNewInactive());
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            List.of(ServiceStatus.ACTIVE),
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(ServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("비활성 상태 필터")
        void shouldFilterByInactiveStatus() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createNew());
            jpaRepository.save(ServiceJpaEntityFixture.createNewInactive());
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            List.of(ServiceStatus.INACTIVE),
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(ServiceStatus.INACTIVE);
        }

        @Test
        @DisplayName("날짜 범위 필터 - 생성일시 기준")
        void shouldFilterByDateRange() {
            // given
            LocalDate startDate = LocalDate.parse("2024-12-31");
            LocalDate endDate = LocalDate.parse("2025-01-02");

            jpaRepository.save(ServiceJpaEntityFixture.createNew());
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(startDate, endDate),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("이름 오름차순 정렬")
        void shouldSortByNameAsc() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_Z", "Z 서비스"));
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_A", "A 서비스"));
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.NAME,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("A 서비스");
            assertThat(result.get(1).getName()).isEqualTo("Z 서비스");
        }

        @Test
        @DisplayName("페이징 - offset과 limit 적용")
        void shouldApplyPagination() {
            // given
            for (int i = 1; i <= 5; i++) {
                jpaRepository.save(
                        ServiceJpaEntityFixture.createWithCodeAndName("SVC_P" + i, "서비스 " + i));
            }
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            null,
                            ServiceSearchField.SERVICE_CODE,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(1, 2));

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 서비스 수 조회")
        void shouldCountByCriteria() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_CNT1", "카운트1"));
            jpaRepository.save(ServiceJpaEntityFixture.createWithCodeAndName("SVC_CNT2", "카운트2"));
            jpaRepository.save(
                    ServiceJpaEntityFixture.createWithCodeAndName("SVC_OTHER", "다른 서비스"));
            flushAndClear();

            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "카운트",
                            ServiceSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("조건에 맞는 서비스가 없으면 0 반환")
        void shouldReturnZeroWhenNoMatches() {
            // given
            ServiceSearchCriteria criteria =
                    ServiceSearchCriteria.of(
                            "존재하지않음",
                            ServiceSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            ServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("findAllActive 테스트")
    class FindAllActiveTest {

        @Test
        @DisplayName("활성 상태 서비스만 조회")
        void shouldFindOnlyActiveServices() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createNew());
            jpaRepository.save(ServiceJpaEntityFixture.createNewInactive());
            jpaRepository.save(
                    ServiceJpaEntity.of(
                            null,
                            "SVC_ACTIVE2",
                            "활성 서비스2",
                            "활성 설명",
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllActive();

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(entity -> entity.getStatus() == ServiceStatus.ACTIVE);
        }

        @Test
        @DisplayName("활성 상태 서비스가 없으면 빈 목록 반환")
        void shouldReturnEmptyListWhenNoActiveServices() {
            // given
            jpaRepository.save(ServiceJpaEntityFixture.createNewInactive());
            flushAndClear();

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllActive();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("활성 서비스를 이름 오름차순으로 정렬하여 반환")
        void shouldReturnActiveSortedByName() {
            // given
            jpaRepository.save(
                    ServiceJpaEntity.of(
                            null,
                            "SVC_Z",
                            "Z 서비스",
                            "설명",
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime()));
            jpaRepository.save(
                    ServiceJpaEntity.of(
                            null,
                            "SVC_A",
                            "A 서비스",
                            "설명",
                            ServiceStatus.ACTIVE,
                            ServiceJpaEntityFixture.fixedTime(),
                            ServiceJpaEntityFixture.fixedTime()));
            flushAndClear();

            // when
            List<ServiceJpaEntity> result = queryDslRepository.findAllActive();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("A 서비스");
            assertThat(result.get(1).getName()).isEqualTo("Z 서비스");
        }
    }
}
