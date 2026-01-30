package com.ryuqq.authhub.adapter.out.persistence.token.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserContextCompositeConditionBuilder 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserContextCompositeConditionBuilder 단위 테스트")
class UserContextCompositeConditionBuilderTest {

    private UserContextCompositeConditionBuilder sut;

    @BeforeEach
    void setUp() {
        sut = new UserContextCompositeConditionBuilder();
    }

    @Nested
    @DisplayName("buildConditionByUserId 메서드")
    class BuildConditionByUserId {

        @Test
        @DisplayName("성공: userId로 BooleanBuilder 생성")
        void shouldBuildCondition_WithUserId() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";

            // when
            BooleanBuilder result = sut.buildConditionByUserId(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getValue()).isNotNull();
        }
    }

    @Nested
    @DisplayName("userIdEquals 메서드")
    class UserIdEquals {

        @Test
        @DisplayName("성공: userId가 있으면 BooleanExpression 반환")
        void shouldReturnExpression_WhenUserIdProvided() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";

            // when
            BooleanExpression result = sut.userIdEquals(userId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null userId면 null 반환")
        void shouldReturnNull_WhenUserIdIsNull() {
            // when
            BooleanExpression result = sut.userIdEquals(null);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("notDeleted 조건 메서드")
    class NotDeletedConditions {

        @Test
        @DisplayName("userNotDeleted는 BooleanExpression 반환")
        void shouldReturnExpression_ForUserNotDeleted() {
            // when
            BooleanExpression result = sut.userNotDeleted();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("organizationNotDeleted는 BooleanExpression 반환")
        void shouldReturnExpression_ForOrganizationNotDeleted() {
            // when
            BooleanExpression result = sut.organizationNotDeleted();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("tenantNotDeleted는 BooleanExpression 반환")
        void shouldReturnExpression_ForTenantNotDeleted() {
            // when
            BooleanExpression result = sut.tenantNotDeleted();

            // then
            assertThat(result).isNotNull();
        }
    }
}
