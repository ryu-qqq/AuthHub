package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantQueryAdapter 테스트
 *
 * <p>TenantQueryPort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantQueryAdapter 테스트")
class TenantQueryAdapterTest {

    @Mock private TenantQueryDslRepository tenantQueryDslRepository;

    @Mock private TenantJpaEntityMapper tenantJpaEntityMapper;

    private TenantQueryAdapter tenantQueryAdapter;

    private static final Long ID = 1L;
    private static final String NAME = "Test Tenant";
    private static final TenantStatus STATUS = TenantStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT_LOCAL = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @BeforeEach
    void setUp() {
        tenantQueryAdapter =
                new TenantQueryAdapter(tenantQueryDslRepository, tenantJpaEntityMapper);
    }

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("ID로 Tenant를 조회한다")
        void shouldFindTenantById() {
            // Given
            TenantId tenantId = TenantId.of(ID);
            TenantJpaEntity entity =
                    TenantJpaEntity.of(ID, NAME, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);
            Tenant tenant =
                    Tenant.reconstitute(
                            TenantId.of(ID), TenantName.of(NAME), STATUS, CREATED_AT, UPDATED_AT);

            given(tenantQueryDslRepository.findById(ID)).willReturn(Optional.of(entity));
            given(tenantJpaEntityMapper.toDomain(entity)).willReturn(tenant);

            // When
            Optional<Tenant> result = tenantQueryAdapter.findById(tenantId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().tenantIdValue()).isEqualTo(ID);
            verify(tenantQueryDslRepository).findById(ID);
            verify(tenantJpaEntityMapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentId() {
            // Given
            TenantId tenantId = TenantId.of(999L);
            given(tenantQueryDslRepository.findById(999L)).willReturn(Optional.empty());

            // When
            Optional<Tenant> result = tenantQueryAdapter.findById(tenantId);

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
            TenantId tenantId = TenantId.of(ID);
            given(tenantQueryDslRepository.existsById(ID)).willReturn(true);

            // When
            boolean result = tenantQueryAdapter.existsById(tenantId);

            // Then
            assertThat(result).isTrue();
            verify(tenantQueryDslRepository).existsById(ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // Given
            TenantId tenantId = TenantId.of(999L);
            given(tenantQueryDslRepository.existsById(999L)).willReturn(false);

            // When
            boolean result = tenantQueryAdapter.existsById(tenantId);

            // Then
            assertThat(result).isFalse();
        }
    }
}
