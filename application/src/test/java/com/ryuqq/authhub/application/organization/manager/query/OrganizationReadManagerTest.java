package com.ryuqq.authhub.application.organization.manager.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.util.List;
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
 * OrganizationReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationReadManager 단위 테스트")
class OrganizationReadManagerTest {

    @Mock private OrganizationQueryPort queryPort;

    private OrganizationReadManager readManager;

    @BeforeEach
    void setUp() {
        readManager = new OrganizationReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 조직을 조회한다")
        void shouldFindOrganizationById() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();
            Organization expected = OrganizationFixture.create();
            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Organization result = readManager.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("조직이 없으면 OrganizationNotFoundException을 발생시킨다")
        void shouldThrowWhenOrganizationNotFound() {
            // given
            OrganizationId id = OrganizationId.of(UUID.randomUUID());
            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> readManager.findById(id))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsByIdTest {

        @Test
        @DisplayName("ID가 존재하면 true를 반환한다")
        void shouldReturnTrueWhenExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();
            given(queryPort.existsById(id)).willReturn(true);

            // when
            boolean result = readManager.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("ID가 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenNotExists() {
            // given
            OrganizationId id = OrganizationId.of(UUID.randomUUID());
            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = readManager.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndNameTest {

        @Test
        @DisplayName("테넌트 내 이름이 존재하면 true를 반환한다")
        void shouldReturnTrueWhenNameExistsInTenant() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Test Organization");
            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(true);

            // when
            boolean result = readManager.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("테넌트 내 이름이 존재하지 않으면 false를 반환한다")
        void shouldReturnFalseWhenNameNotExistsInTenant() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Nonexistent Org");
            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(false);

            // when
            boolean result = readManager.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByTenantIdAndCriteria 메서드")
    class FindAllByTenantIdAndCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 조직 목록을 반환한다")
        void shouldFindOrganizationsByCriteria() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            List<Organization> expected = List.of(OrganizationFixture.create());
            given(queryPort.findAllByTenantIdAndCriteria(tenantId, null, null, 0, 10))
                    .willReturn(expected);

            // when
            List<Organization> result =
                    readManager.findAllByTenantIdAndCriteria(tenantId, null, null, 0, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void shouldReturnEmptyListWhenNoResults() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            given(queryPort.findAllByTenantIdAndCriteria(tenantId, "nonexistent", null, 0, 10))
                    .willReturn(List.of());

            // when
            List<Organization> result =
                    readManager.findAllByTenantIdAndCriteria(tenantId, "nonexistent", null, 0, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("이름과 상태 필터로 조직을 조회한다")
        void shouldFindOrganizationsWithNameAndStatusFilter() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            List<Organization> expected = List.of(OrganizationFixture.create());
            given(queryPort.findAllByTenantIdAndCriteria(tenantId, "Test", "ACTIVE", 0, 10))
                    .willReturn(expected);

            // when
            List<Organization> result =
                    readManager.findAllByTenantIdAndCriteria(tenantId, "Test", "ACTIVE", 0, 10);

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("countByTenantIdAndCriteria 메서드")
    class CountByTenantIdAndCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 조직 개수를 반환한다")
        void shouldCountOrganizationsByCriteria() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            given(queryPort.countByTenantIdAndCriteria(tenantId, null, null)).willReturn(5L);

            // when
            long result = readManager.countByTenantIdAndCriteria(tenantId, null, null);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환한다")
        void shouldReturnZeroWhenNoResults() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            given(queryPort.countByTenantIdAndCriteria(tenantId, "nonexistent", null))
                    .willReturn(0L);

            // when
            long result = readManager.countByTenantIdAndCriteria(tenantId, "nonexistent", null);

            // then
            assertThat(result).isZero();
        }
    }
}
