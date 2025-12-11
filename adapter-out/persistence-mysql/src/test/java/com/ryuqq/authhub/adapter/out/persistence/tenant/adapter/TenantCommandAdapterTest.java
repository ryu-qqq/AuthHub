package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantJpaRepository;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * TenantCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantCommandAdapter 단위 테스트")
class TenantCommandAdapterTest {

    @Mock private TenantJpaRepository repository;

    @Mock private TenantJpaEntityMapper mapper;

    private TenantCommandAdapter adapter;

    private static final UUID TENANT_UUID = TenantFixture.defaultUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new TenantCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("테넌트를 성공적으로 저장한다")
        void shouldPersistTenantSuccessfully() {
            // given
            Tenant domainToSave = TenantFixture.createNew();
            Tenant savedDomain = TenantFixture.create();

            TenantJpaEntity entityToSave =
                    TenantJpaEntity.of(
                            null, null, "New Tenant", TenantStatus.ACTIVE, FIXED_TIME, FIXED_TIME);
            TenantJpaEntity savedEntity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            Tenant result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 테넌트를 수정한다")
        void shouldUpdateExistingTenant() {
            // given
            Tenant existingDomain = TenantFixture.create();
            Tenant updatedDomain = TenantFixture.createWithName("Updated Tenant");

            TenantJpaEntity entityToUpdate =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            TenantJpaEntity updatedEntity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Updated Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            Tenant result = adapter.persist(existingDomain);

            // then
            assertThat(result.nameValue()).isEqualTo("Updated Tenant");
            verify(repository).save(any(TenantJpaEntity.class));
        }
    }
}
