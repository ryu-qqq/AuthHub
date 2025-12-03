package com.ryuqq.authhub.adapter.out.persistence.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * OrganizationJpaEntityMapper 테스트
 *
 * <p>Domain ↔ Entity 변환 테스트
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("OrganizationJpaEntityMapper 테스트")
class OrganizationJpaEntityMapperTest {

    private OrganizationJpaEntityMapper mapper;

    private static final Long ID = 1L;
    private static final String NAME = "Test Organization";
    private static final Long TENANT_ID = 100L;
    private static final OrganizationStatus STATUS = OrganizationStatus.ACTIVE;
    private static final Instant CREATED_AT = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2025-01-02T15:30:00Z");
    private static final LocalDateTime CREATED_AT_LOCAL =
            LocalDateTime.ofInstant(CREATED_AT, ZoneId.of("UTC"));
    private static final LocalDateTime UPDATED_AT_LOCAL =
            LocalDateTime.ofInstant(UPDATED_AT, ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        mapper = new OrganizationJpaEntityMapper();
    }

    @Nested
    @DisplayName("toEntity() 메서드는")
    class ToEntityMethod {

        @Test
        @DisplayName("기존 Organization을 Entity로 변환한다")
        void shouldConvertExistingOrganizationToEntity() {
            // Given
            Organization organization =
                    Organization.reconstitute(
                            OrganizationId.of(ID),
                            OrganizationName.of(NAME),
                            TenantId.of(TENANT_ID),
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            // When
            OrganizationJpaEntity entity = mapper.toEntity(organization);

            // Then
            assertThat(entity.getId()).isEqualTo(ID);
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
            assertThat(entity.getStatus()).isEqualTo(STATUS);
            assertThat(entity.getCreatedAt()).isEqualTo(CREATED_AT_LOCAL);
            assertThat(entity.getUpdatedAt()).isEqualTo(UPDATED_AT_LOCAL);
        }

        @Test
        @DisplayName("신규 Organization은 ID가 null인 Entity로 변환한다")
        void shouldConvertNewOrganizationToEntityWithNullId() {
            // Given
            Organization organization =
                    Organization.of(
                            null,
                            OrganizationName.of(NAME),
                            TenantId.of(TENANT_ID),
                            STATUS,
                            CREATED_AT,
                            UPDATED_AT);

            // When
            OrganizationJpaEntity entity = mapper.toEntity(organization);

            // Then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isEqualTo(NAME);
            assertThat(entity.getTenantId()).isEqualTo(TENANT_ID);
        }
    }

    @Nested
    @DisplayName("toDomain() 메서드는")
    class ToDomainMethod {

        @Test
        @DisplayName("Entity를 Domain으로 변환한다")
        void shouldConvertEntityToDomain() {
            // Given
            OrganizationJpaEntity entity =
                    OrganizationJpaEntity.of(
                            ID, NAME, TENANT_ID, STATUS, CREATED_AT_LOCAL, UPDATED_AT_LOCAL);

            // When
            Organization organization = mapper.toDomain(entity);

            // Then
            assertThat(organization.organizationIdValue()).isEqualTo(ID);
            assertThat(organization.organizationNameValue()).isEqualTo(NAME);
            assertThat(organization.tenantIdValue()).isEqualTo(TENANT_ID);
            assertThat(organization.getOrganizationStatus()).isEqualTo(STATUS);
            assertThat(organization.createdAt()).isEqualTo(CREATED_AT);
            assertThat(organization.updatedAt()).isEqualTo(UPDATED_AT);
        }
    }
}
