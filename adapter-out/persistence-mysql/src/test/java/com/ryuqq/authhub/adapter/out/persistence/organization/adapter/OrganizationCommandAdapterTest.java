package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OrganizationCommandAdapter 테스트
 *
 * <p>OrganizationPersistencePort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationCommandAdapter 테스트")
class OrganizationCommandAdapterTest {

    @Mock private OrganizationJpaRepository organizationJpaRepository;

    @Mock private OrganizationJpaEntityMapper organizationJpaEntityMapper;

    private OrganizationCommandAdapter organizationCommandAdapter;

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
        organizationCommandAdapter =
                new OrganizationCommandAdapter(
                        organizationJpaRepository, organizationJpaEntityMapper);
    }

    @Nested
    @DisplayName("persist() 메서드는")
    class PersistMethod {

        @Test
        @DisplayName("신규 Organization을 저장하고 생성된 ID를 반환한다")
        void shouldSaveNewOrganizationAndReturnId() {
            // Given
            Organization organization =
                    Organization.of(
                            null,
                            OrganizationName.of(NAME),
                            TenantId.of(TENANT_ID),
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);
            OrganizationJpaEntity entityToSave =
                    OrganizationJpaEntity.of(
                            null, NAME, TENANT_ID, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);
            OrganizationJpaEntity savedEntity =
                    OrganizationJpaEntity.of(
                            ID, NAME, TENANT_ID, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            given(organizationJpaEntityMapper.toEntity(organization)).willReturn(entityToSave);
            given(organizationJpaRepository.save(entityToSave)).willReturn(savedEntity);

            // When
            OrganizationId result = organizationCommandAdapter.persist(organization);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(organizationJpaEntityMapper).toEntity(organization);
            verify(organizationJpaRepository).save(entityToSave);
        }

        @Test
        @DisplayName("기존 Organization을 수정하고 ID를 반환한다")
        void shouldUpdateExistingOrganizationAndReturnId() {
            // Given
            Organization organization =
                    Organization.reconstitute(
                            OrganizationId.of(ID),
                            OrganizationName.of(NAME),
                            TenantId.of(TENANT_ID),
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);
            OrganizationJpaEntity entityToSave =
                    OrganizationJpaEntity.of(
                            ID, NAME, TENANT_ID, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            given(organizationJpaEntityMapper.toEntity(organization)).willReturn(entityToSave);
            given(organizationJpaRepository.save(entityToSave)).willReturn(entityToSave);

            // When
            OrganizationId result = organizationCommandAdapter.persist(organization);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(organizationJpaRepository).save(entityToSave);
        }
    }
}
