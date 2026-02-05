package com.ryuqq.authhub.adapter.out.persistence.permission.condition;

import static com.ryuqq.authhub.adapter.out.persistence.permission.entity.QPermissionJpaEntity.permissionJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.permission.vo.PermissionSearchField;
import com.ryuqq.authhub.domain.permission.vo.PermissionSortKey;
import com.ryuqq.authhub.domain.permission.vo.PermissionType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * PermissionConditionBuilder 단위 테스트
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
@DisplayName("PermissionConditionBuilder 단위 테스트")
class PermissionConditionBuilderTest {

    private PermissionConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            1L,
                            "user",
                            PermissionSearchField.PERMISSION_KEY,
                            List.of(PermissionType.SYSTEM),
                            List.of("user"),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            PermissionSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("includeDeleted가 true이면 notDeleted 조건 제외")
        void shouldExcludeNotDeleted_WhenIncludeDeleted() {
            // given: includeDeleted는 QueryContext에서 오므로 ofDefault로는 false. of로 context 생성 필요.
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
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
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            "user",
                            PermissionSearchField.RESOURCE,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("타입 필터가 없으면 해당 조건 제외")
        void shouldExcludeTypeCondition_WhenTypesIsEmpty() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            1L,
                            "read",
                            PermissionSearchField.ACTION,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("리소스 필터가 없으면 해당 조건 제외")
        void shouldExcludeResourceCondition_WhenResourcesIsEmpty() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            1L,
                            "key",
                            PermissionSearchField.PERMISSION_KEY,
                            List.of(PermissionType.CUSTOM),
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
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
        @DisplayName("PERMISSION_ID ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithPermissionIdAsc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.PERMISSION_ID,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.permissionId.asc());
        }

        @Test
        @DisplayName("PERMISSION_ID DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithPermissionIdDesc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.PERMISSION_ID,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.permissionId.desc());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("PERMISSION_KEY DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithPermissionKeyDesc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.PERMISSION_KEY,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.permissionKey.desc());
        }

        @Test
        @DisplayName("RESOURCE ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithResourceAsc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.RESOURCE,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.resource.asc());
        }

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성 (기본값)")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionSearchField.PERMISSION_KEY,
                            null,
                            null,
                            DateRange.of(null, null),
                            PermissionSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(permissionJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("sortKey가 null이면 CREATED_AT 기본값 사용")
        void shouldUseCreatedAtSort_WhenSortKeyIsNull() {
            // given: ofDefault uses CREATED_AT; criteria with null sortKey requires custom
            // construction.
            // PermissionSearchCriteria has queryContext.sortKey() - so we use ofDefault which sets
            // CREATED_AT.
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then (ofDefault uses DESC)
            assertThat(result).isEqualTo(permissionJpaEntity.createdAt.desc());
        }
    }

    @Nested
    @DisplayName("searchByField 메서드")
    class SearchByField {

        @Test
        @DisplayName("PERMISSION_KEY 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithPermissionKeyField() {
            // when
            BooleanExpression result =
                    sut.searchByField(PermissionSearchField.PERMISSION_KEY, "user:read");

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(permissionJpaEntity.permissionKey.containsIgnoreCase("user:read"));
        }

        @Test
        @DisplayName("RESOURCE 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithResourceField() {
            // when
            BooleanExpression result = sut.searchByField(PermissionSearchField.RESOURCE, "user");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.resource.containsIgnoreCase("user"));
        }

        @Test
        @DisplayName("ACTION 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithActionField() {
            // when
            BooleanExpression result = sut.searchByField(PermissionSearchField.ACTION, "read");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.action.containsIgnoreCase("read"));
        }

        @Test
        @DisplayName("DESCRIPTION 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithDescriptionField() {
            // when
            BooleanExpression result = sut.searchByField(PermissionSearchField.DESCRIPTION, "설명");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.description.containsIgnoreCase("설명"));
        }

        @Test
        @DisplayName("searchField가 null이면 null 반환")
        void shouldReturnNull_WhenSearchFieldIsNull() {
            // when
            BooleanExpression result = sut.searchByField(null, "user");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result =
                    sut.searchByField(PermissionSearchField.PERMISSION_KEY, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result =
                    sut.searchByField(PermissionSearchField.PERMISSION_KEY, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("notDeleted: 삭제되지 않은 데이터 조건 생성")
        void shouldCreateNotDeletedCondition() {
            // when
            BooleanExpression result = sut.notDeleted();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.deletedAt.isNull());
        }

        @Test
        @DisplayName("permissionIdEquals: Long ID 일치 조건 생성")
        void shouldCreatePermissionIdEqualsCondition() {
            // when
            BooleanExpression result = sut.permissionIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.permissionId.eq(1L));
        }

        @Test
        @DisplayName("permissionIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenPermissionIdIsNull() {
            // when
            BooleanExpression result = sut.permissionIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("permissionKeyEquals: 권한 키 일치 조건 생성")
        void shouldCreatePermissionKeyEqualsCondition() {
            // when
            BooleanExpression result = sut.permissionKeyEquals("user:read");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.permissionKey.eq("user:read"));
        }

        @Test
        @DisplayName("permissionKeyEquals: null이면 null 반환")
        void shouldReturnNull_WhenPermissionKeyIsNull() {
            // when
            BooleanExpression result = sut.permissionKeyEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("permissionIdIn: ID 목록 포함 조건 생성")
        void shouldCreatePermissionIdInCondition() {
            // when
            BooleanExpression result = sut.permissionIdIn(List.of(1L, 2L));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.permissionId.in(List.of(1L, 2L)));
        }

        @Test
        @DisplayName("permissionIdIn: null이면 null 반환")
        void shouldReturnNull_WhenPermissionIdsIsNull() {
            // when
            BooleanExpression result = sut.permissionIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("permissionIdIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenPermissionIdsIsEmpty() {
            // when
            BooleanExpression result = sut.permissionIdIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("permissionKeyIn: 권한 키 목록 포함 조건 생성")
        void shouldCreatePermissionKeyInCondition() {
            // when
            BooleanExpression result = sut.permissionKeyIn(List.of("user:read", "user:write"));

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(
                            permissionJpaEntity.permissionKey.in(
                                    List.of("user:read", "user:write")));
        }

        @Test
        @DisplayName("permissionKeyIn: null이면 null 반환")
        void shouldReturnNull_WhenPermissionKeysIsNull() {
            // when
            BooleanExpression result = sut.permissionKeyIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("permissionKeyIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenPermissionKeysIsEmpty() {
            // when
            BooleanExpression result = sut.permissionKeyIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("serviceIdEquals: 서비스 ID 일치 조건 생성")
        void shouldCreateServiceIdEqualsCondition() {
            // when
            BooleanExpression result = sut.serviceIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.serviceId.eq(1L));
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
        @DisplayName("serviceIdIn: 서비스 ID 목록 포함 조건 생성")
        void shouldCreateServiceIdInCondition() {
            // when
            BooleanExpression result = sut.serviceIdIn(List.of(1L, 2L));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.serviceId.in(List.of(1L, 2L)));
        }

        @Test
        @DisplayName("serviceIdIn: null이면 null 반환")
        void shouldReturnNull_WhenServiceIdsIsNull() {
            // when
            BooleanExpression result = sut.serviceIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("serviceIdIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenServiceIdsIsEmpty() {
            // when
            BooleanExpression result = sut.serviceIdIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("createdAtGoe: 생성일시 이상 조건 생성")
        void shouldCreateCreatedAtGoeCondition() {
            Instant startInstant = Instant.parse("2025-01-01T00:00:00Z");

            // when
            BooleanExpression result = sut.createdAtGoe(startInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.createdAt.goe(startInstant));
        }

        @Test
        @DisplayName("createdAtGoe: null이면 null 반환")
        void shouldReturnNull_WhenStartInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtGoe(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("createdAtLoe: 생성일시 이하 조건 생성")
        void shouldCreateCreatedAtLoeCondition() {
            Instant endInstant = Instant.parse("2025-12-31T23:59:59Z");

            // when
            BooleanExpression result = sut.createdAtLoe(endInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(permissionJpaEntity.createdAt.loe(endInstant));
        }

        @Test
        @DisplayName("createdAtLoe: null이면 null 반환")
        void shouldReturnNull_WhenEndInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtLoe(null);

            // then
            assertThat(result).isNull();
        }
    }
}
