package com.ryuqq.authhub.adapter.out.persistence.user.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.vo.UserStatus;
import com.ryuqq.authhub.domain.user.vo.UserType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("UserJpaEntity 테스트")
class UserJpaEntityTest {

    private static final Long ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "hashedPassword123";
    private static final String USERNAME = "testuser";
    private static final Long ORGANIZATION_ID = 100L;
    private static final Long TENANT_ID = 10L;
    private static final UserType USER_TYPE = UserType.PUBLIC;
    private static final UserStatus STATUS = UserStatus.ACTIVE;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("[of] 모든 필드를 지정하여 UserJpaEntity 생성")
        void of_shouldCreateUserJpaEntityWithAllFields() {
            // When
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            ID,
                            EMAIL,
                            PASSWORD,
                            USERNAME,
                            ORGANIZATION_ID,
                            TENANT_ID,
                            USER_TYPE,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getEmail()).isEqualTo(EMAIL);
            assertThat(entity.getPassword()).isEqualTo(PASSWORD);
            assertThat(entity.getUsername()).isEqualTo(USERNAME);
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getUserType()).isEqualTo(USER_TYPE);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("[of] ID가 null인 신규 엔티티 생성")
        void of_shouldCreateNewEntityWithNullId() {
            // When
            UserJpaEntity entity =
                    UserJpaEntity.of(
                            null,
                            EMAIL,
                            PASSWORD,
                            USERNAME,
                            ORGANIZATION_ID,
                            TENANT_ID,
                            USER_TYPE,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getEmail()).isEqualTo(EMAIL);
        }
    }

    @Nested
    @DisplayName("Getter 테스트")
    class GetterTest {

        @Test
        @DisplayName("[getId] ID 반환")
        void getId_shouldReturnId() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
        }

        @Test
        @DisplayName("[getEmail] 이메일 반환")
        void getEmail_shouldReturnEmail() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("[getPassword] 비밀번호 반환")
        void getPassword_shouldReturnPassword() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getPassword()).isEqualTo(PASSWORD);
        }

        @Test
        @DisplayName("[getUsername] 사용자명 반환")
        void getUsername_shouldReturnUsername() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getUsername()).isEqualTo(USERNAME);
        }

        @Test
        @DisplayName("[getOrganizationId] 조직 ID 반환")
        void getOrganizationId_shouldReturnOrganizationId() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
        }

        @Test
        @DisplayName("[getTenantId] 테넌트 ID 반환")
        void getTenantId_shouldReturnTenantId() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
        }

        @Test
        @DisplayName("[getUserType] 사용자 유형 반환")
        void getUserType_shouldReturnUserType() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getUserType()).isEqualTo(USER_TYPE);
        }

        @Test
        @DisplayName("[getStatus] 상태 반환")
        void getStatus_shouldReturnStatus() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getStatus()).isEqualTo(STATUS);
        }
    }

    @Nested
    @DisplayName("상속 필드 테스트")
    class InheritedFieldsTest {

        @Test
        @DisplayName("BaseAuditEntity의 createdAt, updatedAt 상속")
        void shouldInheritAuditFields() {
            // Given
            UserJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }

    private UserJpaEntity createEntity() {
        return UserJpaEntity.of(
                ID,
                EMAIL,
                PASSWORD,
                USERNAME,
                ORGANIZATION_ID,
                TENANT_ID,
                USER_TYPE,
                STATUS,
                CREATED_AT,
                UPDATED_AT);
    }
}
