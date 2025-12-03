package com.ryuqq.authhub.adapter.out.persistence.organization.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * OrganizationQueryDslRepository 테스트
 *
 * <p>QueryDSL 기반 조회 Repository 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(OrganizationQueryDslRepository.class)
@DisplayName("OrganizationQueryDslRepository 테스트")
class OrganizationQueryDslRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;

    @Autowired private OrganizationQueryDslRepository organizationQueryDslRepository;

    private static final String NAME = "Test Organization";
    private static final Long TENANT_ID = 100L;
    private static final OrganizationStatus STATUS = OrganizationStatus.ACTIVE;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("ID로 Organization을 조회한다")
        void shouldFindOrganizationById() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(null, NAME, TENANT_ID, STATUS, CREATED_AT, UPDATED_AT);
            OrganizationJpaEntity savedEntity = testEntityManager.persistAndFlush(entity);

            // When
            Optional<OrganizationJpaEntity> result =
                    organizationQueryDslRepository.findById(savedEntity.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(NAME);
            assertThat(result.get().getTenantId()).isEqualTo(TENANT_ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // When
            Optional<OrganizationJpaEntity> result = organizationQueryDslRepository.findById(999L);

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById() 메서드는")
    class ExistsByIdMethod {

        @Test
        @DisplayName("존재하는 ID는 true를 반환한다")
        void shouldReturnTrueForExistingId() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(null, NAME, TENANT_ID, STATUS, CREATED_AT, UPDATED_AT);
            OrganizationJpaEntity savedEntity = testEntityManager.persistAndFlush(entity);

            // When
            boolean result = organizationQueryDslRepository.existsById(savedEntity.getId());

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // When
            boolean result = organizationQueryDslRepository.existsById(999L);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantId() 메서드는")
    class ExistsByTenantIdMethod {

        @Test
        @DisplayName("해당 Tenant의 Organization이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenOrganizationExistsForTenant() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(null, NAME, TENANT_ID, STATUS, CREATED_AT, UPDATED_AT);
            testEntityManager.persistAndFlush(entity);

            // When
            boolean result = organizationQueryDslRepository.existsByTenantId(TENANT_ID);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("해당 Tenant의 Organization이 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoOrganizationExistsForTenant() {
            // When
            boolean result = organizationQueryDslRepository.existsByTenantId(999L);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsActiveByTenantId() 메서드는")
    class ExistsActiveByTenantIdMethod {

        @Test
        @DisplayName("해당 Tenant의 활성 Organization이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenActiveOrganizationExistsForTenant() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            null,
                            NAME,
                            TENANT_ID,
                            OrganizationStatus.ACTIVE,
                            CREATED_AT,
                            UPDATED_AT);
            testEntityManager.persistAndFlush(entity);

            // When
            boolean result = organizationQueryDslRepository.existsActiveByTenantId(TENANT_ID);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("해당 Tenant의 Organization이 비활성이면 false를 반환한다")
        void shouldReturnFalseWhenOnlyInactiveOrganizationExists() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            null,
                            NAME,
                            TENANT_ID,
                            OrganizationStatus.INACTIVE,
                            CREATED_AT,
                            UPDATED_AT);
            testEntityManager.persistAndFlush(entity);

            // When
            boolean result = organizationQueryDslRepository.existsActiveByTenantId(TENANT_ID);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("해당 Tenant의 Organization이 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoOrganizationExistsForTenant() {
            // When
            boolean result = organizationQueryDslRepository.existsActiveByTenantId(999L);

            // Then
            assertThat(result).isFalse();
        }
    }
}
