package com.ryuqq.authhub.integration.repository.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.fixture.TenantJpaEntityFixture;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantSearchField;
import com.ryuqq.authhub.domain.tenant.vo.TenantSortKey;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * Tenant QueryDSL Repository 통합 테스트.
 *
 * <p>QueryDSL 기반의 복잡한 쿼리 동작을 검증합니다.
 *
 * <ul>
 *   <li>findByTenantId - ID로 단건 조회
 *   <li>existsByName - 이름 존재 여부
 *   <li>existsByTenantId - ID 존재 여부
 *   <li>existsByNameAndIdNot - 이름 중복 검사 (자기 제외)
 *   <li>findAllByCriteria - 조건 검색
 *   <li>countByCriteria - 조건 개수
 * </ul>
 */
@Tag(TestTags.REPOSITORY)
@Tag(TestTags.TENANT)
@DisplayName("테넌트 QueryDSL Repository 테스트")
class TenantQueryDslRepositoryTest extends RepositoryTestBase {

    @Autowired private TenantJpaRepository jpaRepository;
    @Autowired private TenantQueryDslRepository queryDslRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        flushAndClear();
    }

    @Nested
    @DisplayName("findByTenantId 테스트")
    class FindByTenantIdTest {

        @Test
        @DisplayName("활성 테넌트 조회 성공")
        void shouldFindActiveTenant() {
            // given
            TenantJpaEntity entity = jpaRepository.save(TenantJpaEntityFixture.create());
            flushAndClear();

            // when
            Optional<TenantJpaEntity> found =
                    queryDslRepository.findByTenantId(entity.getTenantId());

            // then
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Tenant");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회시 빈 Optional 반환")
        void shouldReturnEmptyWhenNotFound() {
            // when
            Optional<TenantJpaEntity> found =
                    queryDslRepository.findByTenantId("01941234-5678-7000-8000-000000000000");

            // then
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByTenantId 테스트")
    class ExistsByTenantIdTest {

        @Test
        @DisplayName("존재하는 테넌트 ID - true")
        void shouldReturnTrueForExistingTenantId() {
            // given
            TenantJpaEntity entity = jpaRepository.save(TenantJpaEntityFixture.create());
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByTenantId(entity.getTenantId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트 ID - false")
        void shouldReturnFalseForNonExistentTenantId() {
            // when
            boolean exists =
                    queryDslRepository.existsByTenantId("01941234-5678-7000-8000-000000000000");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByName 테스트")
    class ExistsByNameTest {

        @Test
        @DisplayName("존재하는 테넌트명 - true")
        void shouldReturnTrueForExistingName() {
            // given
            jpaRepository.save(TenantJpaEntityFixture.createWithName("고유한테넌트"));
            flushAndClear();

            // when
            boolean exists = queryDslRepository.existsByName("고유한테넌트");

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 테넌트명 - false")
        void shouldReturnFalseForNonExistentName() {
            // when
            boolean exists = queryDslRepository.existsByName("존재하지않는테넌트");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 테스트")
    class ExistsByNameAndIdNotTest {

        @Test
        @DisplayName("다른 테넌트가 같은 이름 사용중이면 true")
        void shouldReturnTrueWhenOtherTenantHasSameName() {
            // given
            jpaRepository.save(
                    TenantJpaEntity.of(
                            "01941234-5678-7000-8000-123456789001",
                            "중복테넌트",
                            TenantStatus.ACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null));
            TenantJpaEntity tenant2 =
                    jpaRepository.save(
                            TenantJpaEntity.of(
                                    "01941234-5678-7000-8000-123456789002",
                                    "현재테넌트",
                                    TenantStatus.ACTIVE,
                                    TenantJpaEntityFixture.fixedTime(),
                                    TenantJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByNameAndIdNot("중복테넌트", tenant2.getTenantId());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("자기 자신의 이름은 제외하고 검사")
        void shouldExcludeSelfWhenCheckingName() {
            // given
            TenantJpaEntity tenant =
                    jpaRepository.save(
                            TenantJpaEntity.of(
                                    "01941234-5678-7000-8000-123456789003",
                                    "유일한테넌트",
                                    TenantStatus.ACTIVE,
                                    TenantJpaEntityFixture.fixedTime(),
                                    TenantJpaEntityFixture.fixedTime(),
                                    null));
            flushAndClear();

            // when
            boolean exists =
                    queryDslRepository.existsByNameAndIdNot("유일한테넌트", tenant.getTenantId());

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 테스트")
    class FindAllByCriteriaTest {

        @Test
        @DisplayName("전체 조회")
        void shouldFindAll() {
            // given
            jpaRepository.save(TenantJpaEntityFixture.createWithName("테넌트1"));
            jpaRepository.save(
                    TenantJpaEntity.of(
                            "01941234-5678-7000-8000-123456789004",
                            "테넌트2",
                            TenantStatus.ACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("이름 검색 - 부분 일치")
        void shouldSearchByName() {
            // given
            jpaRepository.save(TenantJpaEntityFixture.createWithName("ABC테넌트"));
            jpaRepository.save(
                    TenantJpaEntity.of(
                            "01941234-5678-7000-8000-123456789005",
                            "XYZ테넌트",
                            TenantStatus.ACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "테넌트",
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(TenantJpaEntity::getName)
                    .allMatch(n -> n.contains("테넌트"));
        }

        @Test
        @DisplayName("활성 상태 필터")
        void shouldFilterByActiveStatus() {
            // given
            jpaRepository.save(TenantJpaEntityFixture.createWithName("활성테넌트"));
            jpaRepository.save(TenantJpaEntityFixture.createInactive());
            flushAndClear();

            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            null,
                            TenantSearchField.NAME,
                            List.of(TenantStatus.ACTIVE),
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            List<TenantJpaEntity> result = queryDslRepository.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(TenantStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 테넌트 수 조회")
        void shouldCountByCriteria() {
            // given
            jpaRepository.save(TenantJpaEntityFixture.createWithName("카운트1"));
            jpaRepository.save(
                    TenantJpaEntity.of(
                            "01941234-5678-7000-8000-123456789006",
                            "카운트2",
                            TenantStatus.ACTIVE,
                            TenantJpaEntityFixture.fixedTime(),
                            TenantJpaEntityFixture.fixedTime(),
                            null));
            flushAndClear();

            TenantSearchCriteria criteria =
                    TenantSearchCriteria.of(
                            "카운트",
                            TenantSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            TenantSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));

            // when
            long count = queryDslRepository.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2);
        }
    }
}
