package com.ryuqq.authhub.adapter.out.persistence.token.repository;

import static com.ryuqq.authhub.adapter.out.persistence.organization.entity.QOrganizationJpaEntity.organizationJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.tenant.entity.QTenantJpaEntity.tenantJpaEntity;
import static com.ryuqq.authhub.adapter.out.persistence.user.entity.QUserJpaEntity.userJpaEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.token.condition.UserContextCompositeConditionBuilder;
import com.ryuqq.authhub.adapter.out.persistence.token.dto.UserContextProjection;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * UserContextCompositeQueryDslRepository 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>JPAQueryFactory + JPAQuery 체인 Mock
 *   <li>UserContextCompositeConditionBuilder 위임 검증
 *   <li>조회 결과 반환 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserContextCompositeQueryDslRepository 단위 테스트")
class UserContextCompositeQueryDslRepositoryTest {

    @Mock private JPAQueryFactory queryFactory;

    @Mock private JPAQuery<UserContextProjection> compositeQuery;

    private UserContextCompositeConditionBuilder conditionBuilder;
    private UserContextCompositeQueryDslRepository sut;

    @BeforeEach
    void setUp() {
        conditionBuilder = new UserContextCompositeConditionBuilder();
        sut = new UserContextCompositeQueryDslRepository(queryFactory, conditionBuilder);
    }

    @Nested
    @DisplayName("findUserContextByUserId 메서드")
    class FindUserContextByUserId {

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("성공: Projection이 있으면 Optional에 담아 반환")
        void shouldReturnOptionalWithProjection_WhenExists() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";
            UserContextProjection projection =
                    new UserContextProjection(
                            userId,
                            "test@example.com",
                            "Test User",
                            "tenant-123",
                            "Test Tenant",
                            "org-456",
                            "Test Organization");

            given(queryFactory.select(any(Expression.class))).willReturn(compositeQuery);
            given(compositeQuery.from(userJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.join(organizationJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.on(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.join(tenantJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.on(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.where(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.fetchOne()).willReturn(projection);

            // when
            Optional<UserContextProjection> result = sut.findUserContextByUserId(userId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(projection);
        }

        @Test
        @SuppressWarnings("unchecked")
        @DisplayName("Projection이 없으면 빈 Optional 반환")
        void shouldReturnEmpty_WhenNotFound() {
            // given
            String userId = "019450eb-4f1e-7000-8000-000000000001";

            given(queryFactory.select(any(Expression.class))).willReturn(compositeQuery);
            given(compositeQuery.from(userJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.join(organizationJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.on(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.join(tenantJpaEntity)).willReturn(compositeQuery);
            given(compositeQuery.on(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.where(any(Predicate.class))).willReturn(compositeQuery);
            given(compositeQuery.fetchOne()).willReturn(null);

            // when
            Optional<UserContextProjection> result = sut.findUserContextByUserId(userId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
