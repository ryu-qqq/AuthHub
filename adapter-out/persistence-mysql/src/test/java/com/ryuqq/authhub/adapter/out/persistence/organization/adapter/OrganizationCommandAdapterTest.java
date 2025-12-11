package com.ryuqq.authhub.adapter.out.persistence.organization.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.adapter.out.persistence.organization.entity.OrganizationJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.organization.mapper.OrganizationJpaEntityMapper;
import com.ryuqq.authhub.adapter.out.persistence.organization.repository.OrganizationJpaRepository;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
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
 * OrganizationCommandAdapter 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationCommandAdapter 단위 테스트")
class OrganizationCommandAdapterTest {

    @Mock private OrganizationJpaRepository repository;

    @Mock private OrganizationJpaEntityMapper mapper;

    private OrganizationCommandAdapter adapter;

    private static final UUID ORG_UUID = OrganizationFixture.defaultUUID();
    private static final UUID TENANT_UUID = OrganizationFixture.defaultTenantUUID();
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

    @BeforeEach
    void setUp() {
        adapter = new OrganizationCommandAdapter(repository, mapper);
    }

    @Nested
    @DisplayName("persist 메서드")
    class PersistTest {

        @Test
        @DisplayName("조직을 성공적으로 저장한다")
        void shouldPersistOrganizationSuccessfully() {
            // given
            Organization domainToSave = OrganizationFixture.createNew();
            Organization savedDomain = OrganizationFixture.create();

            OrganizationJpaEntity entityToSave =
                    OrganizationJpaEntity.of(
                            null,
                            ORG_UUID,
                            TENANT_UUID,
                            "New Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            OrganizationJpaEntity savedEntity =
                    OrganizationJpaEntity.of(
                            1L,
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(domainToSave)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedDomain);

            // when
            Organization result = adapter.persist(domainToSave);

            // then
            assertThat(result).isEqualTo(savedDomain);
            verify(mapper).toEntity(domainToSave);
            verify(repository).save(entityToSave);
            verify(mapper).toDomain(savedEntity);
        }

        @Test
        @DisplayName("기존 조직을 수정한다")
        void shouldUpdateExistingOrganization() {
            // given
            Organization existingDomain = OrganizationFixture.create();
            Organization updatedDomain = OrganizationFixture.createWithName("Updated Organization");

            OrganizationJpaEntity entityToUpdate =
                    OrganizationJpaEntity.of(
                            1L,
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            OrganizationJpaEntity updatedEntity =
                    OrganizationJpaEntity.of(
                            1L,
                            ORG_UUID,
                            TENANT_UUID,
                            "Updated Organization",
                            OrganizationStatus.ACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(existingDomain)).willReturn(entityToUpdate);
            given(repository.save(entityToUpdate)).willReturn(updatedEntity);
            given(mapper.toDomain(updatedEntity)).willReturn(updatedDomain);

            // when
            Organization result = adapter.persist(existingDomain);

            // then
            assertThat(result.nameValue()).isEqualTo("Updated Organization");
            verify(repository).save(any(OrganizationJpaEntity.class));
        }

        @Test
        @DisplayName("비활성화된 조직을 저장한다")
        void shouldPersistInactiveOrganization() {
            // given
            Organization inactiveOrg = OrganizationFixture.createInactive();
            Organization savedInactiveOrg = OrganizationFixture.createInactive();

            OrganizationJpaEntity entityToSave =
                    OrganizationJpaEntity.of(
                            null,
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);
            OrganizationJpaEntity savedEntity =
                    OrganizationJpaEntity.of(
                            1L,
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Organization",
                            OrganizationStatus.INACTIVE,
                            FIXED_TIME,
                            FIXED_TIME);

            given(mapper.toEntity(inactiveOrg)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);
            given(mapper.toDomain(savedEntity)).willReturn(savedInactiveOrg);

            // when
            Organization result = adapter.persist(inactiveOrg);

            // then
            assertThat(result.getStatus()).isEqualTo(OrganizationStatus.INACTIVE);
        }
    }
}
