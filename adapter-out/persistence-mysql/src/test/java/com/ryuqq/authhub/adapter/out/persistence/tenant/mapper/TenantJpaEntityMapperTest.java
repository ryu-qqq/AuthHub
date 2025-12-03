package com.ryuqq.authhub.adapter.out.persistence.tenant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.tenant.entity.TenantJpaEntity;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TenantJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("TenantJpaEntityMapper 테스트")
class TenantJpaEntityMapperTest {

    private TenantJpaEntityMapper mapper;

    private static final Long ID = 1L;
    private static final String NAME = "Test Tenant";
    private static final TenantStatus STATUS = TenantStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL =
            LocalDateTime.ofInstant(CREATED_AT, ZoneId.of("UTC"));
    private static final LocalDateTime UPDATED_AT_LOCAL =
            LocalDateTime.ofInstant(UPDATED_AT, ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        mapper = new TenantJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 Tenant를 Entity로 변환한다")
        void shouldConvertExistingTenantToEntity() {
            // Given
            Tenant tenant =
                    Tenant.reconstitute(
                            TenantId.of(ID), TenantName.of(NAME), STATUS, CREATED_AT, UPDATED_AT);

            // When
            TenantJpaEntity entity = mapper.toEntity(tenant);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT_LOCAL);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT_LOCAL);
        }

        @Test
        @DisplayName("신규 Tenant는 ID가 null인 Entity로 변환한다")
        void shouldConvertNewTenantToEntityWithNullId() {
            // Given
            Tenant tenant = Tenant.of(null, TenantName.of(NAME), STATUS, CREATED_AT, UPDATED_AT);

            // When
            TenantJpaEntity entity = mapper.toEntity(tenant);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo(NAME);
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            TenantJpaEntity entity =
                    TenantJpaEntity.of(ID, NAME, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            // When
            Tenant tenant = mapper.toDomain(entity);

            // Then
            assertThat(tenant.tenantIdValue()).isEqualTo(ID);
            assertThat(tenant.tenantNameValue()).isEqualTo(NAME);
            assertThat(tenant.getTenantStatus()).isEqualTo(STATUS);
            assertThat(tenant.createdAt()).isEqualTo(CREATED_AT);
            assertThat(tenant.updatedAt()).isEqualTo(UPDATED_AT);
        }
    }
}
