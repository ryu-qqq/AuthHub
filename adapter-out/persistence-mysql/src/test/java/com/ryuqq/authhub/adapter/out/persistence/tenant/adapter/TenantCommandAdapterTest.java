package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
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
 * TenantCommandAdapter 테스트
 *
 * <p>TenantPersistencePort 구현체 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandAdapter 테스트")
class TenantCommandAdapterTest {

    @Mock private TenantJpaRepository tenantJpaRepository;

    @Mock private TenantJpaEntityMapper tenantJpaEntityMapper;

    private TenantCommandAdapter tenantCommandAdapter;

    private static final Long ID = 1L;
    private static final String NAME = "Test Tenant";
    private static final TenantStatus STATUS = TenantStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
    private static final LocalDateTime UPDATED_AT_LOCAL = LocalDateTime.of(2025, 1, 2, 15, 30, 0);

    @BeforeEach
    void setUp() {
        tenantCommandAdapter = new TenantCommandAdapter(tenantJpaRepository, tenantJpaEntityMapper);
    }

    @Nested
    @DisplayName("persist() 메서드는")
    class PersistMethod {

        @Test
        @DisplayName("신규 Tenant를 저장하고 생성된 ID를 반환한다")
        void shouldSaveNewTenantAndReturnId() {
            // Given
            Tenant tenant = Tenant.of(null, TenantName.of(NAME), STATUS, CREATED_AT, UPDATED_AT);
            TenantJpaEntity entityToSave =
                    TenantJpaEntity.of(null, NAME, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);
            TenantJpaEntity savedEntity =
                    TenantJpaEntity.of(ID, NAME, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            given(tenantJpaEntityMapper.toEntity(tenant)).willReturn(entityToSave);
            given(tenantJpaRepository.save(entityToSave)).willReturn(savedEntity);

            // When
            TenantId result = tenantCommandAdapter.persist(tenant);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(tenantJpaEntityMapper).toEntity(tenant);
            verify(tenantJpaRepository).save(entityToSave);
        }

        @Test
        @DisplayName("기존 Tenant를 수정하고 ID를 반환한다")
        void shouldUpdateExistingTenantAndReturnId() {
            // Given
            Tenant tenant =
                    Tenant.reconstitute(
                            TenantId.of(ID), TenantName.of(NAME), STATUS, CREATED_AT, UPDATED_AT);
            TenantJpaEntity entityToSave =
                    TenantJpaEntity.of(ID, NAME, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            given(tenantJpaEntityMapper.toEntity(tenant)).willReturn(entityToSave);
            given(tenantJpaRepository.save(entityToSave)).willReturn(entityToSave);

            // When
            TenantId result = tenantCommandAdapter.persist(tenant);

            // Then
            assertThat(result.value()).isEqualTo(ID);
            verify(tenantJpaRepository).save(entityToSave);
        }
    }
}
