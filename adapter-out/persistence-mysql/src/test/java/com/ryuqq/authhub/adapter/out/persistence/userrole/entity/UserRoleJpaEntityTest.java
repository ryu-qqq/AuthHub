package com.ryuqq.authhub.adapter.out.persistence.userrole.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.userrole.fixture.UserRoleJpaEntityFixture;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserRoleJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of()/forNew() 팩토리, getter 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>BaseAuditEntity 상속 (createdAt, updatedAt) 검증
 *   <li>Hard Delete (Soft Delete 미적용) 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserRoleJpaEntity 단위 테스트")
class UserRoleJpaEntityTest {

    private static final Long USER_ROLE_ID = 1L;
    private static final String USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final Long ROLE_ID = 1L;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("forNew() 팩토리 메서드")
    class ForNewFactoryMethod {

        @Test
        @DisplayName("성공: userRoleId가 null로 설정됨")
        void shouldSetUserRoleIdToNull() {
            // when
            UserRoleJpaEntity entity = UserRoleJpaEntity.forNew(USER_ID, ROLE_ID, CREATED_AT);

            // then
            assertThat(entity.getUserRoleId()).isNull();
        }

        @Test
        @DisplayName("String userId (UUID)가 올바르게 설정됨")
        void shouldSetUserId_Correctly() {
            // when
            UserRoleJpaEntity entity = UserRoleJpaEntity.forNew(USER_ID, ROLE_ID, CREATED_AT);

            // then
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            UserRoleJpaEntity entity = UserRoleJpaEntity.forNew(USER_ID, ROLE_ID, CREATED_AT);

            // then
            assertThat(entity.getUserRoleId()).isNull();
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(CREATED_AT);
        }
    }

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            UserRoleJpaEntity entity =
                    UserRoleJpaEntity.of(USER_ROLE_ID, USER_ID, ROLE_ID, CREATED_AT, UPDATED_AT);

            // then
            assertThat(entity.getUserRoleId()).isEqualTo(USER_ROLE_ID);
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getRoleId()).isEqualTo(ROLE_ID);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }

    @Nested
    @DisplayName("BaseAuditEntity 상속")
    class BaseAuditEntityInheritance {

        @Test
        @DisplayName("createdAt과 updatedAt이 올바르게 설정됨")
        void shouldSetAuditFields_Correctly() {
            // when
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(UserRoleJpaEntityFixture.fixedTime());
            assertThat(entity.getUpdatedAt()).isEqualTo(UserRoleJpaEntityFixture.fixedTime());
        }
    }

    @Nested
    @DisplayName("Getter 메서드")
    class GetterMethods {

        @Test
        @DisplayName("모든 getter가 올바르게 동작")
        void shouldReturnCorrectValues() {
            // given
            UserRoleJpaEntity entity = UserRoleJpaEntityFixture.create();

            // then
            assertThat(entity.getUserRoleId())
                    .isEqualTo(UserRoleJpaEntityFixture.defaultUserRoleId());
            assertThat(entity.getUserId()).isEqualTo(UserRoleJpaEntityFixture.defaultUserId());
            assertThat(entity.getRoleId()).isEqualTo(UserRoleJpaEntityFixture.defaultRoleId());
            assertThat(entity.getCreatedAt()).isNotNull();
            assertThat(entity.getUpdatedAt()).isNotNull();
        }
    }
}
