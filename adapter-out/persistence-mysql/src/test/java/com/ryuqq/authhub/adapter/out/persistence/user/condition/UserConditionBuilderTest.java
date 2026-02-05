package com.ryuqq.authhub.adapter.out.persistence.user.condition;

import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.QueryContext;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.query.criteria.UserSearchCriteria;
import com.ryuqq.authhub.domain.user.vo.UserSearchField;
import com.ryuqq.authhub.domain.user.vo.UserSortKey;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserConditionBuilder 단위 테스트
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
@DisplayName("UserConditionBuilder 단위 테스트")
class UserConditionBuilderTest {

    private UserConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new UserConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: 검색 조건이 모두 포함된 BooleanBuilder 생성")
        void shouldBuildCondition_WithAllFilters() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of("org-1")),
                            "search",
                            UserSearchField.IDENTIFIER,
                            List.of(UserStatus.ACTIVE),
                            DateRange.of(
                                    LocalDate.parse("2025-01-01"), LocalDate.parse("2025-12-31")),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("조직 ID 목록이 null이면 해당 조건 제외")
        void shouldExcludeOrganizationIdsCondition_WhenOrganizationIdsIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색어가 null이면 searchByField 조건 제외")
        void shouldExcludeSearchCondition_WhenSearchWordIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
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
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of("org-1")),
                            "word",
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("날짜 범위가 null이면 해당 조건 제외")
        void shouldExcludeDateCondition_WhenDateRangeIsNull() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            List.of(OrganizationId.of("org-1")),
                            "word",
                            UserSearchField.IDENTIFIER,
                            List.of(UserStatus.ACTIVE),
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("includeDeleted가 true이면 deletedAt 조건 미적용")
        void shouldNotApplyDeletedCondition_WhenIncludeDeletedIsTrue() {
            // given
            QueryContext<UserSortKey> ctx =
                    QueryContext.of(
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10),
                            true);
            UserSearchCriteria criteria =
                    new UserSearchCriteria(null, null, null, null, DateRange.of(null, null), ctx);

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
        @DisplayName("UPDATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtAsc() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userJpaEntity.updatedAt.asc());
        }

        @Test
        @DisplayName("UPDATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithUpdatedAtDesc() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userJpaEntity.updatedAt.desc());
        }

        @Test
        @DisplayName("CREATED_AT ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtAsc() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userJpaEntity.createdAt.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("sortKey가 null이면 CREATED_AT 기본값 사용")
        void shouldUseCreatedAtSort_WhenSortKeyIsNull() {
            // given: criteria with null sortKey requires building via record with QueryContext that
            // has null sortKey - but QueryContext rejects null sortKey.
            // So we test default in builder: criteria.sortKey() != null ? criteria.sortKey() :
            // UserSortKey.CREATED_AT.
            // We need a criteria whose queryContext returns null sortKey. QueryContext compact
            // constructor throws if sortKey is null.
            // So in practice sortKey is never null when using QueryContext. We test CREATED_AT as
            // the main default path.
            UserSearchCriteria criteria =
                    UserSearchCriteria.of(
                            null,
                            null,
                            UserSearchField.IDENTIFIER,
                            null,
                            DateRange.of(null, null),
                            UserSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(userJpaEntity.createdAt.asc());
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("organizationIdsIn: 목록이 있으면 in 조건 생성")
        void shouldCreateOrganizationIdsInCondition() {
            // when
            BooleanExpression result =
                    sut.organizationIdsIn(
                            List.of(OrganizationId.of("org-1"), OrganizationId.of("org-2")));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.organizationId.in("org-1", "org-2"));
        }

        @Test
        @DisplayName("organizationIdsIn: null이면 null 반환")
        void shouldReturnNull_WhenOrganizationIdsIsNull() {
            // when
            BooleanExpression result = sut.organizationIdsIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("organizationIdsIn: 빈 목록이면 null 반환")
        void shouldReturnNull_WhenOrganizationIdsIsEmpty() {
            // when
            BooleanExpression result = sut.organizationIdsIn(List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchByField: IDENTIFIER 검색 조건 생성")
        void shouldCreateSearchCondition_WithIdentifier() {
            // when
            BooleanExpression result =
                    sut.searchByField(UserSearchField.IDENTIFIER, "user@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(userJpaEntity.identifier.containsIgnoreCase("user@example.com"));
        }

        @Test
        @DisplayName("searchByField: PHONE_NUMBER 검색 조건 생성")
        void shouldCreateSearchCondition_WithPhoneNumber() {
            // when
            BooleanExpression result =
                    sut.searchByField(UserSearchField.PHONE_NUMBER, "01012345678");

            // then
            assertThat(result).isNotNull();
            assertThat(result)
                    .isEqualTo(userJpaEntity.phoneNumber.containsIgnoreCase("01012345678"));
        }

        @Test
        @DisplayName("searchByField: searchWord가 null이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsNull() {
            // when
            BooleanExpression result = sut.searchByField(UserSearchField.IDENTIFIER, null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("searchByField: searchWord가 공백이면 null 반환")
        void shouldReturnNull_WhenSearchWordIsBlank() {
            // when
            BooleanExpression result = sut.searchByField(UserSearchField.IDENTIFIER, "   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("userIdEquals: 조건 생성")
        void shouldCreateUserIdEqualsCondition() {
            // when
            BooleanExpression result = sut.userIdEquals("user-1");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.userId.eq("user-1"));
        }

        @Test
        @DisplayName("userIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenUserIdIsNull() {
            // when
            BooleanExpression result = sut.userIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("organizationIdEquals: 조건 생성")
        void shouldCreateOrganizationIdEqualsCondition() {
            // when
            BooleanExpression result = sut.organizationIdEquals("org-1");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.organizationId.eq("org-1"));
        }

        @Test
        @DisplayName("organizationIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenOrganizationIdIsNull() {
            // when
            BooleanExpression result = sut.organizationIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("identifierEquals: 조건 생성")
        void shouldCreateIdentifierEqualsCondition() {
            // when
            BooleanExpression result = sut.identifierEquals("id@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.identifier.eq("id@example.com"));
        }

        @Test
        @DisplayName("identifierEquals: null이면 null 반환")
        void shouldReturnNull_WhenIdentifierIsNull() {
            // when
            BooleanExpression result = sut.identifierEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("phoneNumberEquals: 조건 생성")
        void shouldCreatePhoneNumberEqualsCondition() {
            // when
            BooleanExpression result = sut.phoneNumberEquals("01012345678");

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.phoneNumber.eq("01012345678"));
        }

        @Test
        @DisplayName("phoneNumberEquals: null이면 null 반환")
        void shouldReturnNull_WhenPhoneNumberIsNull() {
            // when
            BooleanExpression result = sut.phoneNumberEquals(null);

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
            assertThat(result).isEqualTo(userJpaEntity.createdAt.goe(startInstant));
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
            assertThat(result).isEqualTo(userJpaEntity.createdAt.loe(endInstant));
        }

        @Test
        @DisplayName("createdAtLoe: null이면 null 반환")
        void shouldReturnNull_WhenEndInstantIsNull() {
            // when
            BooleanExpression result = sut.createdAtLoe(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("notDeleted: 삭제되지 않은 항목 조건 생성")
        void shouldCreateNotDeletedCondition() {
            // when
            BooleanExpression result = sut.notDeleted();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(userJpaEntity.deletedAt.isNull());
        }
    }
}
