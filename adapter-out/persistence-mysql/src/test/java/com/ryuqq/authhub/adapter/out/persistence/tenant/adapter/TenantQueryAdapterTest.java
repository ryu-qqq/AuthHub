package com.ryuqq.authhub.adapter.out.persistence.tenant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.tenant.mapper.TenantJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.tenant.repository.TenantQueryDslRepository;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.LocalDateTime;
import java.util.Optional;
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
 * TenantQueryAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantQueryAdapter 단위 테스트")
class TenantQueryAdapterTest {

    @Mock private TenantQueryDslRepository repository;

    @Mock private TenantJpaEntityMapper mapper;

    private TenantQueryAdapter adapter;

    private static final UUID TENANT_UUID = TenantFixture.defaultUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new TenantQueryAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 테넌트를 조회한다")
        void shouldFindTenantById() {
            // given
            TenantId tenantId = TenantFixture.defaultId();
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Tenant expectedDomain = TenantFixture.create();

            given(repository.findByTenantId(TENANT_UUID)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Tenant> result = adapter.findById(tenantId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
            verify(repository).findByTenantId(TENANT_UUID);
            verify(mapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenTenantNotFound() {
            // given
            TenantId tenantId = TenantId.of(UUID.randomUUID());
            given(repository.findByTenantId(tenantId.value())).willReturn(Optional.empty());

            // when
            Optional<Tenant> result = adapter.findById(tenantId);

            // then
            assertThat(result).isEmpty();
            verify(repository).findByTenantId(tenantId.value());
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID면 true를 반환한다")
        void shouldReturnTrueWhenIdExists() {
            // given
            TenantId tenantId = TenantFixture.defaultId();
            given(repository.existsByTenantId(TENANT_UUID)).willReturn(true);

            // when
            boolean result = adapter.existsById(tenantId);

            // then
            assertThat(result).isTrue();
            verify(repository).existsByTenantId(TENANT_UUID);
        }

        @Test
        @DisplayName("존재하지 않는 ID면 false를 반환한다")
        void shouldReturnFalseWhenIdDoesNotExist() {
            // given
            UUID nonExistingUuid = UUID.randomUUID();
            TenantId tenantId = TenantId.of(nonExistingUuid);
            given(repository.existsByTenantId(nonExistingUuid)).willReturn(false);

            // when
            boolean result = adapter.existsById(tenantId);

            // then
            assertThat(result).isFalse();
            verify(repository).existsByTenantId(nonExistingUuid);
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByNameTest {

        @Test
        @DisplayName("존재하는 이름이면 true를 반환한다")
        void shouldReturnTrueWhenNameExists() {
            // given
            TenantName name = TenantName.of("Existing Tenant");
            given(repository.existsByName("Existing Tenant")).willReturn(true);

            // when
            boolean result = adapter.existsByName(name);

            // then
            assertThat(result).isTrue();
            verify(repository).existsByName("Existing Tenant");
        }

        @Test
        @DisplayName("존재하지 않는 이름이면 false를 반환한다")
        void shouldReturnFalseWhenNameDoesNotExist() {
            // given
            TenantName name = TenantName.of("Non-Existing Tenant");
            given(repository.existsByName("Non-Existing Tenant")).willReturn(false);

            // when
            boolean result = adapter.existsByName(name);

            // then
            assertThat(result).isFalse();
            verify(repository).existsByName("Non-Existing Tenant");
        }
    }

    @Nested
    @DisplayName("findByName 메서드")
    class FindByNameTest {

        @Test
        @DisplayName("이름으로 테넌트를 조회한다")
        void shouldFindTenantByName() {
            // given
            TenantName name = TenantName.of("Test Tenant");
            TenantJpaEntity entity =
                    TenantJpaEntity.of(
                            1L,
                            TENANT_UUID,
                            "Test Tenant",
                            TenantStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            Tenant expectedDomain = TenantFixture.create();

            given(repository.findByName("Test Tenant")).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(expectedDomain);

            // when
            Optional<Tenant> result = adapter.findByName(name);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedDomain);
            verify(repository).findByName("Test Tenant");
            verify(mapper).toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 이름으로 조회 시 빈 Optional을 반환한다")
        void shouldReturnEmptyWhenTenantNotFoundByName() {
            // given
            TenantName name = TenantName.of("Non-Existing Tenant");
            given(repository.findByName("Non-Existing Tenant")).willReturn(Optional.empty());

            // when
            Optional<Tenant> result = adapter.findByName(name);

            // then
            assertThat(result).isEmpty();
            verify(repository).findByName("Non-Existing Tenant");
        }
    }
}
