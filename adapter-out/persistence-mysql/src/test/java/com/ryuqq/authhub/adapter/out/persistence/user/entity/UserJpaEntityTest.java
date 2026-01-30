package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.vo.UserStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserJpaEntity 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>Entity는 데이터 컨테이너 → of() 팩토리, getter, 상태 확인 메서드 검증
 *   <li>Mock 불필요 (순수 객체 생성/조회)
 *   <li>SoftDeletableEntity 상속 기능 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserJpaEntity 단위 테스트")
class UserJpaEntityTest {

    private static final String USER_ID = "user-uuid-123";
    private static final String ORG_ID = "org-uuid-456";
    private static final String IDENTIFIER = "test@example.com";
    private static final String PHONE_NUMBER = "010-1234-5678";
    private static final String HASHED_PASSWORD = "$2a$10$hashedpassword";
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T00:00:00Z");

    @Nested
    @DisplayName("of() 팩토리 메서드")
    class OfFactoryMethod {

        @Test
        @DisplayName("성공: 모든 필드가 올바르게 설정됨")
        void shouldSetAllFields_Correctly() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getUserId()).isEqualTo(USER_ID);
            assertThat(entity.getOrganizationId()).isEqualTo(ORG_ID);
            assertThat(entity.getIdentifier()).isEqualTo(IDENTIFIER);
            assertThat(entity.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
            assertThat(entity.getHashedPassword()).isEqualTo(HASHED_PASSWORD);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("deletedAt이 null이면 활성 상태")
        void shouldBeActive_WhenDeletedAtIsNull() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("deletedAt이 설정되면 삭제 상태")
        void shouldBeDeleted_WhenDeletedAtIsSet() {
            // given
            Instant deletedAt = Instant.parse("2025-01-03T00:00:00Z");

            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            deletedAt);

            // then
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.isActive()).isFalse();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("phoneNumber가 null인 경우도 허용")
        void shouldAllowNullPhoneNumber() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            null,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("상태 관련 메서드")
    class StatusMethods {

        @Test
        @DisplayName("ACTIVE 상태가 올바르게 설정됨")
        void shouldSetActiveStatus() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            UserStatus.ACTIVE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("INACTIVE 상태가 올바르게 설정됨")
        void shouldSetInactiveStatus() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            UserStatus.INACTIVE,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("SUSPENDED 상태가 올바르게 설정됨")
        void shouldSetSuspendedStatus() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            UserStatus.SUSPENDED,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getStatus()).isEqualTo(UserStatus.SUSPENDED);
        }
    }

    @Nested
    @DisplayName("감사 필드 (BaseAuditEntity 상속)")
    class AuditFields {

        @Test
        @DisplayName("createdAt이 올바르게 설정됨")
        void shouldSetCreatedAt() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("updatedAt이 올바르게 설정됨")
        void shouldSetUpdatedAt() {
            // when
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            USER_ID,
                            ORG_ID,
                            IDENTIFIER,
                            PHONE_NUMBER,
                            HASHED_PASSWORD,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT,
                            null);

            // then
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }
}
