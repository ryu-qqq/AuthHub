package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * UserJpaEntity 테스트
 *
 * <p>User JPA Entity 생성 및 속성 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("UserJpaEntity 테스트")
class UserJpaEntityTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Long TENANT_ID = 100L;
    private static final Long ORGANIZATION_ID = 200L;
    private static final UserType USER_TYPE = UserType.PUBLIC;
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final String IDENTIFIER = "test@example.com";
    private static final String HASHED_PASSWORD = "$2a$10$hashedpassword";
    private static final String NAME = "Test User";
    private static final String PHONE_NUMBER = "+821012345678";
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("of() 팩토리 메서드는")
    class OfMethod {

        @Test
        @DisplayName("모든 필드가 설정된 Entity를 생성한다")
        void shouldCreateEntityWithAllFields() {
            // When
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
            assertThat(entity.getUserType()).isEqualTo(USER_TYPE);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getIdentifier()).isEqualTo(IDENTIFIER);
            assertThat(entity.getHashedPassword()).isEqualTo(HASHED_PASSWORD);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getPhoneNumber()).isEqualTo(PHONE_NUMBER);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("Organization 없이 Entity를 생성한다")
        void shouldCreateEntityWithoutOrganization() {
            // When
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            null,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity.getOrganizationId()).isNull();
        }

        @Test
        @DisplayName("Profile 정보 없이 Entity를 생성한다")
        void shouldCreateEntityWithoutProfile() {
            // When
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            null,
                            null,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity.getName()).isNull();
            assertThat(entity.getPhoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("equals() 메서드는")
    class EqualsMethod {

        @Test
        @DisplayName("동일한 ID를 가진 Entity는 동등하다")
        void shouldBeEqualForSameId() {
            // Given
            UserJpaEntity entity1 =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT,
                            UPDATED_AT);

            UserJpaEntity entity2 =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            UserType.INTERNAL,
                            UserStatus.INACTIVE,
                            "other@example.com",
                            "otherpassword",
                            "Other Name",
                            "010-9999-9999",
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity1).isEqualTo(entity2);
        }

        @Test
        @DisplayName("다른 ID를 가진 Entity는 동등하지 않다")
        void shouldNotBeEqualForDifferentId() {
            // Given
            UserJpaEntity entity1 =
                    UserJpaEntity.of(
                            ID,
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT,
                            UPDATED_AT);

            UserJpaEntity entity2 =
                    UserJpaEntity.of(
                            UUID.randomUUID(),
                            TENANT_ID,
                            ORGANIZATION_ID,
                            USER_TYPE,
                            STATUS,
                            IDENTIFIER,
                            HASHED_PASSWORD,
                            NAME,
                            PHONE_NUMBER,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity1).isNotEqualTo(entity2);
        }
    }
}
