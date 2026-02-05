package com.ryuqq.authhub.adapter.out.persistence.rolepermission.condition;

import static com.ryuqq.authhub.adapter.out.persistence.rolepermission.entity.QRolePermissionJpaEntity.rolePermissionJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.authhub.domain.common.vo.PageRequest;
import com.ryuqq.authhub.domain.common.vo.SortDirection;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.rolepermission.query.criteria.RolePermissionSearchCriteria;
import com.ryuqq.authhub.domain.rolepermission.vo.RolePermissionSortKey;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * RolePermissionConditionBuilder 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ConditionBuilder는 순수 조건 생성 로직 → Mock 불필요
 *   <li>각 조건 메서드가 올바른 QueryDSL Expression 생성 검증
 *   <li>null-safe 처리 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("RolePermissionConditionBuilder 단위 테스트")
class RolePermissionConditionBuilderTest {

    private RolePermissionConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new RolePermissionConditionBuilder();
    }

    @Nested
    @DisplayName("buildCondition 메서드")
    class BuildCondition {

        @Test
        @DisplayName("성공: roleId 필터가 있으면 BooleanBuilder에 포함")
        void shouldBuildCondition_WithRoleIdFilter() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(RoleId.of(1L), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("성공: permissionId 필터가 있으면 BooleanBuilder에 포함")
        void shouldBuildCondition_WithPermissionIdFilter() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofPermissionId(PermissionId.of(1L), 0, 10);

            // when
            BooleanBuilder result = sut.buildCondition(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasValue()).isTrue();
        }

        @Test
        @DisplayName("roleId와 roleIds가 없으면 해당 조건 제외")
        void shouldExcludeRoleCondition_WhenNoRoleFilter() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            PermissionId.of(1L),
                            null,
                            RolePermissionSortKey.CREATED_AT,
                            SortDirection.DESC,
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
        @DisplayName("ROLE_PERMISSION_ID ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithRolePermissionIdAsc() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            RoleId.of(1L),
                            null,
                            null,
                            null,
                            RolePermissionSortKey.ROLE_PERMISSION_ID,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(rolePermissionJpaEntity.rolePermissionId.asc());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithCreatedAtDesc() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.ofRoleId(RoleId.of(1L), 0, 10);

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(rolePermissionJpaEntity.createdAt.desc());
        }

        @Test
        @DisplayName("ROLE_ID DESC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithRoleIdDesc() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.ROLE_ID,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(rolePermissionJpaEntity.roleId.desc());
        }

        @Test
        @DisplayName("PERMISSION_ID ASC 정렬 조건 생성")
        void shouldBuildOrderSpecifier_WithPermissionIdAsc() {
            // given
            RolePermissionSearchCriteria criteria =
                    RolePermissionSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            RolePermissionSortKey.PERMISSION_ID,
                            SortDirection.ASC,
                            PageRequest.of(0, 10));

            // when
            OrderSpecifier<?> result = sut.buildOrderSpecifier(criteria);

            // then
            assertThat(result).isEqualTo(rolePermissionJpaEntity.permissionId.asc());
        }
    }

    @Nested
    @DisplayName("조건 메서드 테스트")
    class ConditionMethods {

        @Test
        @DisplayName("rolePermissionIdEquals: 조건 생성")
        void shouldCreateRolePermissionIdEqualsCondition() {
            // when
            BooleanExpression result = sut.rolePermissionIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(rolePermissionJpaEntity.rolePermissionId.eq(1L));
        }

        @Test
        @DisplayName("rolePermissionIdEquals: null이면 null 반환")
        void shouldReturnNull_WhenRolePermissionIdIsNull() {
            // when
            BooleanExpression result = sut.rolePermissionIdEquals(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("roleIdEquals: 조건 생성")
        void shouldCreateRoleIdEqualsCondition() {
            // when
            BooleanExpression result = sut.roleIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(rolePermissionJpaEntity.roleId.eq(1L));
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
        @DisplayName("roleIdIn: 목록이 있으면 in 조건 생성")
        void shouldCreateRoleIdInCondition() {
            // when
            BooleanExpression result = sut.roleIdIn(List.of(1L, 2L));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(rolePermissionJpaEntity.roleId.in(1L, 2L));
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
        @DisplayName("permissionIdEquals: 조건 생성")
        void shouldCreatePermissionIdEqualsCondition() {
            // when
            BooleanExpression result = sut.permissionIdEquals(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(rolePermissionJpaEntity.permissionId.eq(1L));
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
        @DisplayName("permissionIdIn: 목록이 있으면 in 조건 생성")
        void shouldCreatePermissionIdInCondition() {
            // when
            BooleanExpression result = sut.permissionIdIn(List.of(1L, 2L));

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(rolePermissionJpaEntity.permissionId.in(1L, 2L));
        }

        @Test
        @DisplayName("permissionIdIn: null이면 null 반환")
        void shouldReturnNull_WhenPermissionIdsIsNull() {
            // when
            BooleanExpression result = sut.permissionIdIn(null);

            // then
            assertThat(result).isNull();
        }
    }
}
