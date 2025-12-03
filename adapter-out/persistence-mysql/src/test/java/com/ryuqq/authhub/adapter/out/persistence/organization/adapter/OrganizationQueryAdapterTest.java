package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationQueryDslRepository;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
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
 * OrganizationQueryAdapter 테스트
 *
 * <p>OrganizationQueryPort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationQueryAdapter 테스트")
class OrganizationQueryAdapterTest {

    @Mock private OrganizationQueryDslRepository organizationQueryDslRepository;

    @Mock private OrganizationJpaEntityMapper organizationJpaEntityMapper;

    private OrganizationQueryAdapter organizationQueryAdapter;

    private static final Long ID = 1L;
    private static final String NAME = "Test Organization";
    private static final Long TENANT_ID = 100L;
    private static final OrganizationStatus STATUS = OrganizationStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT_LOCAL = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @BeforeEach
    void setUp() {
        organizationQueryAdapter =
                new OrganizationQueryAdapter(
                        organizationQueryDslRepository, organizationJpaEntityMapper);
    }

    @Nested
    @DisplayName("findById() 메서드는")
    class FindByIdMethod {

        @Test
        @DisplayName("존재하는 Organization을 조회한다")
        void shouldFindOrganizationById() {
            // Given
            OrganizationId organizationId = OrganizationId.of(ID);
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ID, NAME, TENANT_ID, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);
            Organization organization =
                    Organization.reconstitute(
                            organizationId,
                            OrganizationName.of(NAME),
                            TenantId.of(TENANT_ID),
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            given(organizationQueryDslRepository.findById(ID)).willReturn(Optional.of(entity));
            given(organizationJpaEntityMapper.toDomain(entity)).willReturn(organization);

            // When
            Optional<Organization> result = organizationQueryAdapter.findById(organizationId);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().organizationIdValue()).isEqualTo(ID);
            verify(organizationQueryDslRepository).findById(ID);
            verify(organizationJpaEntityMapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 Organization은 빈 Optional을 반환한다")
        void shouldReturnEmptyForNonExistentOrganization() {
            // Given
            OrganizationId organizationId = OrganizationId.of(999L);
            given(organizationQueryDslRepository.findById(999L)).willReturn(Optional.empty());

            // When
            Optional<Organization> result = organizationQueryAdapter.findById(organizationId);

            // Then
            assertThat(result).isEmpty();
            verify(organizationQueryDslRepository).findById(999L);
        }
    }

    @Nested
    @DisplayName("existsById() 메서드는")
    class ExistsByIdMethod {

        @Test
        @DisplayName("존재하는 ID는 true를 반환한다")
        void shouldReturnTrueForExistingId() {
            // Given
            OrganizationId organizationId = OrganizationId.of(ID);
            given(organizationQueryDslRepository.existsById(ID)).willReturn(true);

            // When
            boolean result = organizationQueryAdapter.existsById(organizationId);

            // Then
            assertThat(result).isTrue();
            verify(organizationQueryDslRepository).existsById(ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환한다")
        void shouldReturnFalseForNonExistentId() {
            // Given
            OrganizationId organizationId = OrganizationId.of(999L);
            given(organizationQueryDslRepository.existsById(999L)).willReturn(false);

            // When
            boolean result = organizationQueryAdapter.existsById(organizationId);

            // Then
            assertThat(result).isFalse();
            verify(organizationQueryDslRepository).existsById(999L);
        }
    }

    @Nested
    @DisplayName("existsByTenantId() 메서드는")
    class ExistsByTenantIdMethod {

        @Test
        @DisplayName("해당 Tenant의 Organization이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenOrganizationExistsForTenant() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            given(organizationQueryDslRepository.existsByTenantId(TENANT_ID)).willReturn(true);

            // When
            boolean result = organizationQueryAdapter.existsByTenantId(tenantId);

            // Then
            assertThat(result).isTrue();
            verify(organizationQueryDslRepository).existsByTenantId(TENANT_ID);
        }

        @Test
        @DisplayName("해당 Tenant의 Organization이 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoOrganizationExistsForTenant() {
            // Given
            TenantId tenantId = TenantId.of(999L);
            given(organizationQueryDslRepository.existsByTenantId(999L)).willReturn(false);

            // When
            boolean result = organizationQueryAdapter.existsByTenantId(tenantId);

            // Then
            assertThat(result).isFalse();
            verify(organizationQueryDslRepository).existsByTenantId(999L);
        }
    }

    @Nested
    @DisplayName("existsActiveByTenantId() 메서드는")
    class ExistsActiveByTenantIdMethod {

        @Test
        @DisplayName("해당 Tenant의 활성 Organization이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenActiveOrganizationExistsForTenant() {
            // Given
            TenantId tenantId = TenantId.of(TENANT_ID);
            given(organizationQueryDslRepository.existsActiveByTenantId(TENANT_ID))
                    .willReturn(true);

            // When
            boolean result = organizationQueryAdapter.existsActiveByTenantId(tenantId);

            // Then
            assertThat(result).isTrue();
            verify(organizationQueryDslRepository).existsActiveByTenantId(TENANT_ID);
        }

        @Test
        @DisplayName("해당 Tenant의 활성 Organization이 없으면 false를 반환한다")
        void shouldReturnFalseWhenNoActiveOrganizationExistsForTenant() {
            // Given
            TenantId tenantId = TenantId.of(999L);
            given(organizationQueryDslRepository.existsActiveByTenantId(999L)).willReturn(false);

            // When
            boolean result = organizationQueryAdapter.existsActiveByTenantId(tenantId);

            // Then
            assertThat(result).isFalse();
            verify(organizationQueryDslRepository).existsActiveByTenantId(999L);
        }
    }
}
