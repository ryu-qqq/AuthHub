package com.ryuqq.authhub.adapter.out.persistence.tenant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * TenantQueryDslRepository 테스트
 *
 * <p>QueryDSL 기반 조회 Repository 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(TenantQueryDslRepository.class)
@DisplayName("TenantQueryDslRepository 테스트")
class TenantQueryDslRepositoryTest {

    @Autowired private TestEntityManager testEntityManager;

    @Autowired private TenantQueryDslRepository tenantQueryDslRepository;

    private static final String NAME = "Test Tenant";
    private static final TenantStatus STATUS = TenantStatus.ACTIVE;
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("ID로 Tenant를 조회한다")
        void shouldFindTenantById() {
            // Given
            TenantJpaEntity entity = TenantJpaEntity.of(null, NAME, STATUS, CREATED_AT, UPDATED_AT);
            TenantJpaEntity savedEntity = testEntityManager.persistAndFlush(entity);

            // When
            Optional<TenantJpaEntity> result =
                    tenantQueryDslRepository.findById(savedEntity.getId());

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(NAME);
            assertThat(result.get().getStatus()).isEqualTo(STATUS);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // When
            Optional<TenantJpaEntity> result = tenantQueryDslRepository.findById(999L);

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
            TenantJpaEntity entity = TenantJpaEntity.of(null, NAME, STATUS, CREATED_AT, UPDATED_AT);
            TenantJpaEntity savedEntity = testEntityManager.persistAndFlush(entity);

            // When
            boolean result = tenantQueryDslRepository.existsById(savedEntity.getId());

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // When
            boolean result = tenantQueryDslRepository.existsById(999L);

            // Then
            assertThat(result).isFalse();
        }
    }
}
