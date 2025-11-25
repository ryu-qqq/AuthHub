package com.ryuqq.authhub.adapter.out.persistence.tenant.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TenantJpaEntity 테스트")
class TenantJpaEntityTest {

    private static final Long ID = 1L;
    private static final String NAME = "Test Tenant";
    private static final String DESCRIPTION = "Test tenant description";
    private static final Long ORGANIZATION_ID = 100L;
    private static final TenantStatus STATUS = TenantStatus.ACTIVE;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("팩토리 메서드 테스트")
    class FactoryMethodTest {

        @Test
        @DisplayName("[of] 모든 필드를 지정하여 TenantJpaEntity 생성")
        void of_shouldCreateTenantJpaEntityWithAllFields() {
            // When
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            ID, NAME, DESCRIPTION, ORGANIZATION_ID, STATUS, CREATED_AT, UPDATED_AT);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("[of] ID가 null인 신규 엔티티 생성")
        void of_shouldCreateNewEntityWithNullId() {
            // When
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            null,
                            NAME,
                            DESCRIPTION,
                            ORGANIZATION_ID,
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo(NAME);
        }

        @Test
        @DisplayName("[of] Description이 null인 엔티티 생성")
        void of_shouldCreateEntityWithNullDescription() {
            // When
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            ID, NAME, null, ORGANIZATION_ID, STATUS, CREATED_AT, UPDATED_AT);

            // Then
            assertThat(entity.getDescription()).isNull();
        }
    }

    @Nested
    @DisplayName("Getter 테스트")
    class GetterTest {

        @Test
        @DisplayName("[getId] ID 반환")
        void getId_shouldReturnId() {
            // Given
            TenantJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
        }

        @Test
        @DisplayName("[getName] 이름 반환")
        void getName_shouldReturnName() {
            // Given
            TenantJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getName()).isEqualTo(NAME);
        }

        @Test
        @DisplayName("[getDescription] 설명 반환")
        void getDescription_shouldReturnDescription() {
            // Given
            TenantJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getDescription()).isEqualTo(DESCRIPTION);
        }

        @Test
        @DisplayName("[getOrganizationId] 조직 ID 반환")
        void getOrganizationId_shouldReturnOrganizationId() {
            // Given
            TenantJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
        }

        @Test
        @DisplayName("[getStatus] 상태 반환")
        void getStatus_shouldReturnStatus() {
            // Given
            TenantJpaEntity entity = createEntity();

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
            TenantJpaEntity entity = createEntity();

            // Then
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT);
        }
    }

    private TenantJpaEntity createEntity() {
        return TenantJpaEntity.of(
                ID, NAME, DESCRIPTION, ORGANIZATION_ID, STATUS, CREATED_AT, UPDATED_AT);
    }
}
