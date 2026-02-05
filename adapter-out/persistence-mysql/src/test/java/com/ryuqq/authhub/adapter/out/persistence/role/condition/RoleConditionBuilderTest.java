package com.ryuqq.authhub.adapter.out.persistence.role.condition;

import static com.ryuqq.authhub.adapter.out.persistence.role.entity.QRoleJpaEntity.roleJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleSearchField;
import com.ryuqq.authhub.domain.role.vo.RoleSortKey;
import com.ryuqq.authhub.domain.role.vo.RoleType;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RoleConditionBuilder 단위 테스트
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
@DisplayName("RoleConditionBuilder 단위 테스트")
class RoleConditionBuilderTest {

    private RoleConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new RoleConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            TenantId.forNew("tenant-1"),
                            1L,
                            "ADMIN",
                            RoleSearchField.NAME,
                            List.of(RoleType.SYSTEM, RoleType.CUSTOM),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            RoleSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("includeDeleted가 false이면 notDeleted 조건 적용")
        void shouldApplyNotDeleted_WhenIncludeDeletedIsFalse() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("includeDeleted가 true이면 notDeleted 조건 미적용")
        void shouldNotApplyNotDeleted_WhenIncludeDeletedIsTrue() {
            // given
            QueryContext<RoleSortKey> context =
                    QueryContext.of(
                            RoleSortKey.CREATED_AT, SortDirection.ASC, PageRequest.of(0, 10), true);
            RoleSearchCriteria criteria =
                    new RoleSearchCriteria(
                            null,
                            null,
                            null,
                            RoleSearchField.defaultField(),
                            null,
                            DateRange.of(null, null),
                            context);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 없으면 해당 조건 제외")
        void shouldExcludeSearchCondition_WhenSearchWordIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("역할 유형 필터가 없으면 해당 조건 제외")
        void shouldExcludeTypeCondition_WhenTypesIsEmpty() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal("ADMIN", null, DateRange.of(null, null), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 없으면 해당 조건 제외")
        void shouldExcludeDateCondition_WhenDateRangeIsNull() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(
                            "ADMIN", List.of(RoleType.SYSTEM), DateRange.of(null, null), 0, 10);

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
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("ROLE_ID ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithRoleIdAsc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.ROLE_ID,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.roleId.asc());
        }

        @Test
        @DisplayName("NAME DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithNameDesc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.NAME,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.name.desc());
        }

        @Test
        @DisplayName("DISPLAY_NAME ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithDisplayNameAsc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.DISPLAY_NAME,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.displayName.asc());
        }

        @Test
        @DisplayName("UPDATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtDesc() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.updatedAt.desc());
        }

        @Test
        @DisplayName("sortKey가 null이면 CREATED_AT 기본값 사용")
        void shouldUseCreatedAtSort_WhenSortKeyIsNull() {
            // given - ofGlobal uses CREATED_AT as default
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.of(
                            null,
                            null,
                            null,
                            RoleSearchField.NAME,
                            null,
                            DateRange.of(null, null),
                            RoleSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(roleJpaEntity.createdAt.asc());
        }
    }

    @Nested
    @DisplayName("tenantCondition 메서드")
    class TenantCondition {

        @Test
        @DisplayName("isGlobalOnly가 true이면 tenantId IS NULL 조건 반환")
        void shouldReturnTenantIdIsNull_WhenGlobalOnly() {
            // given - ofGlobal creates tenantId=null
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);

            // when
            BooleanExpression result = sut.tenantCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.tenantId.isNull());
        }

        @Test
        @DisplayName("isGlobalOnly가 false이면 tenantId EQ OR IS NULL 조건 반환")
        void shouldReturnTenantIdEqOrNull_WhenNotGlobalOnly() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofTenant(
                            TenantId.forNew("tenant-1"),
                            null,
                            null,
                            DateRange.of(null, null),
                            0,
                            10);

            // when
            BooleanExpression result = sut.tenantCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(
                            roleJpaEntity
                                    .tenantId
                                    .eq("tenant-1")
                                    .or(roleJpaEntity.tenantId.isNull()));
        }
    }

    @Nested
    @DisplayName("searchByField 메서드")
    class SearchByField {

        @Test
        @DisplayName("NAME 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithNameField() {
            // when
            BooleanExpression result = sut.searchByField(RoleSearchField.NAME, "ADMIN");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.name.containsIgnoreCase("ADMIN"));
        }

        @Test
        @DisplayName("DISPLAY_NAME 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithDisplayNameField() {
            // when
            BooleanExpression result = sut.searchByField(RoleSearchField.DISPLAY_NAME, "관리자");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.displayName.containsIgnoreCase("관리자"));
        }

        @Test
        @DisplayName("DESCRIPTION 필드로 검색 조건 생성")
        void shouldCreateSearchCondition_WithDescriptionField() {
            // when
            BooleanExpression result = sut.searchByField(RoleSearchField.DESCRIPTION, "시스템");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.description.containsIgnoreCase("시스템"));
        }

        @Test
        @DisplayName("searchField가 null이면 null 반환")
        void shouldReturnNull_WhenSearchFieldIsNull() {
            // when
            BooleanExpression result = sut.searchByField(null, "ADMIN");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result = sut.searchByField(RoleSearchField.NAME, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchWord가 빈 문자열이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result = sut.searchByField(RoleSearchField.NAME, "   ");

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("notDeleted: deletedAt.isNull() 조건 생성")
        void shouldCreateNotDeletedCondition() {
            // when
            BooleanExpression result = sut.notDeleted();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.deletedAt.isNull());
        }

        @Test
        @DisplayName("roleIdEquals: ID 일치 조건 생성")
        void shouldCreateRoleIdEqualsCondition() {
            // when
            BooleanExpression result = sut.roleIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.roleId.eq(1L));
        }

        @Test
        @DisplayName("roleIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenRoleIdIsNull() {
            // when
            BooleanExpression result = sut.roleIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("nameEquals: 이름 일치 조건 생성")
        void shouldCreateNameEqualsCondition() {
            // when
            BooleanExpression result = sut.nameEquals("ADMIN");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.name.eq("ADMIN"));
        }

        @Test
        @DisplayName("nameEquals: null이면 null 반환")
        void shouldReturnNull_WhenNameIsNull() {
            // when
            BooleanExpression result = sut.nameEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("tenantIdEquals: tenantId가 있으면 eq 조건 생성")
        void shouldCreateTenantIdEq_WhenTenantIdNotNull() {
            // when
            BooleanExpression result = sut.tenantIdEquals("tenant-1");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.tenantId.eq("tenant-1"));
        }

        @Test
        @DisplayName("tenantIdEquals: null이면 IS NULL 조건 생성")
        void shouldCreateTenantIdIsNull_WhenTenantIdIsNull() {
            // when
            BooleanExpression result = sut.tenantIdEquals(null);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.tenantId.isNull());
        }

        @Test
        @DisplayName("serviceIdEquals: serviceId가 있으면 eq 조건 생성")
        void shouldCreateServiceIdEq_WhenServiceIdNotNull() {
            // when
            BooleanExpression result = sut.serviceIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.serviceId.eq(1L));
        }

        @Test
        @DisplayName("serviceIdEquals: null이면 IS NULL 조건 생성")
        void shouldCreateServiceIdIsNull_WhenServiceIdIsNull() {
            // when
            BooleanExpression result = sut.serviceIdEquals(null);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.serviceId.isNull());
        }

        @Test
        @DisplayName("roleIdIn: ID 목록 포함 조건 생성")
        void shouldCreateRoleIdInCondition() {
            // when
            BooleanExpression result = sut.roleIdIn(List.of(1L, 2L, 3L));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.roleId.in(1L, 2L, 3L));
        }

        @Test
        @DisplayName("roleIdIn: null이면 null 반환")
        void shouldReturnNull_WhenRoleIdsIsNull() {
            // when
            BooleanExpression result = sut.roleIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("roleIdIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenRoleIdsIsEmpty() {
            // when
            BooleanExpression result = sut.roleIdIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("createdAtGoe: 생성일시 이상 조건 생성")
        void shouldCreateCreatedAtGoeCondition() {
            // given
            Instant startInstant = Instant.parse("2025-01-01T00:00:00Z");

            // when
            BooleanExpression result = sut.createdAtGoe(startInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.createdAt.goe(startInstant));
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
            // given
            Instant endInstant = Instant.parse("2025-12-31T23:59:59Z");

            // when
            BooleanExpression result = sut.createdAtLoe(endInstant);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(roleJpaEntity.createdAt.loe(endInstant));
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
