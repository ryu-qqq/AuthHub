package com.ryuqq.authhub.adapter.out.persistence.tenantservice.condition;

import static com.ryuqq.authhub.adapter.out.persistence.tenantservice.entity.QTenantServiceJpaEntity.tenantServiceJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.tenantservice.query.criteria.TenantServiceSearchCriteria;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceSortKey;
import com.ryuqq.authhub.domain.tenantservice.vo.TenantServiceStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * TenantServiceConditionBuilder 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder는 순수 조건 생성 로직 → Mock 불필요
 *   <li>각 조건 메서드가 올바른 QueryDSL Expression 생성 검증
 *   <li>null-safe 처리 검증
 *   <li>복합 조건 조합 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("TenantServiceConditionBuilder 단위 테스트")
class TenantServiceConditionBuilderTest {

    private TenantServiceConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new TenantServiceConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-123456789abc",
                            1L,
                            List.of(TenantServiceStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("테넌트 ID가 없으면 해당 조건 제외")
        void shouldExcludeTenantIdCondition_WhenTenantIdIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            1L,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("서비스 ID가 없으면 해당 조건 제외")
        void shouldExcludeServiceIdCondition_WhenServiceIdIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-123456789abc",
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 해당 조건 제외")
        void shouldExcludeStatusCondition_WhenStatusesIsEmpty() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-123456789abc",
                            1L,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 없으면 해당 조건 제외")
        void shouldExcludeDateCondition_WhenDateRangeIsNull() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            "01941234-5678-7000-8000-123456789abc",
                            1L,
                            List.of(TenantServiceStatus.ACTIVE),
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("buildOrderSpecifier 메서드")
    class BuildOrderSpecifier {

        @Test
        @DisplayName("SUBSCRIBED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithSubscribedAtAsc() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantServiceJpaEntity.subscribedAt.asc());
        }

        @Test
        @DisplayName("SUBSCRIBED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithSubscribedAtDesc() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantServiceJpaEntity.subscribedAt.desc());
        }

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantServiceJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantServiceJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("sortKey가 기본값일 때 SUBSCRIBED_AT 정렬 사용")
        void shouldUseSubscribedAtSort_WhenDefaultSortKey() {
            // given
            TenantServiceSearchCriteria criteria =
                    TenantServiceSearchCriteria.of(
                            null,
                            null,
                            null,
                            DateRange.of(null, null),
                            TenantServiceSortKey.SUBSCRIBED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(tenantServiceJpaEntity.subscribedAt.asc());
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("tenantIdEquals: String 테넌트 ID 일치 조건 생성")
        void shouldCreateTenantIdEqualsCondition() {
            // when
            BooleanExpression result = sut.tenantIdEquals("01941234-5678-7000-8000-123456789abc");

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(
                            tenantServiceJpaEntity.tenantId.eq(
                                    "01941234-5678-7000-8000-123456789abc"));
        }

        @Test
        @DisplayName("tenantIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenTenantIdIsNull() {
            // when
            BooleanExpression result = sut.tenantIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("serviceIdEquals: Long 서비스 ID 일치 조건 생성")
        void shouldCreateServiceIdEqualsCondition() {
            // when
            BooleanExpression result = sut.serviceIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantServiceJpaEntity.serviceId.eq(1L));
        }

        @Test
        @DisplayName("serviceIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenServiceIdIsNull() {
            // when
            BooleanExpression result = sut.serviceIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("statusActive: 활성 상태 조건 생성")
        void shouldCreateStatusActiveCondition() {
            // when
            BooleanExpression result = sut.statusActive();

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(tenantServiceJpaEntity.status.eq(TenantServiceStatus.ACTIVE));
        }

        @Test
        @DisplayName("subscribedAtGoe: 구독일시 이상 조건 생성")
        void shouldCreateSubscribedAtGoeCondition() {
            // given
            Instant startInstant = Instant.parse("2025-01-01T00:00:00Z");

            // when
            BooleanExpression result = sut.subscribedAtGoe(startInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantServiceJpaEntity.subscribedAt.goe(startInstant));
        }

        @Test
        @DisplayName("subscribedAtGoe: null이면 null 반환")
        void shouldReturnNull_WhenStartInstantIsNull() {
            // when
            BooleanExpression result = sut.subscribedAtGoe(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("subscribedAtLoe: 구독일시 이하 조건 생성")
        void shouldCreateSubscribedAtLoeCondition() {
            // given
            Instant endInstant = Instant.parse("2025-12-31T23:59:59Z");

            // when
            BooleanExpression result = sut.subscribedAtLoe(endInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(tenantServiceJpaEntity.subscribedAt.loe(endInstant));
        }

        @Test
        @DisplayName("subscribedAtLoe: null이면 null 반환")
        void shouldReturnNull_WhenEndInstantIsNull() {
            // when
            BooleanExpression result = sut.subscribedAtLoe(null);

            // then
            assertThat(result).isNull();
        }
    }
}
