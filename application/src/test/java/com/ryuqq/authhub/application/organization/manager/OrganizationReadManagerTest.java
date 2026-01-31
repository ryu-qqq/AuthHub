package com.ryuqq.authhub.application.organization.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ryuqq.authhub.application.organization.port.out.query.OrganizationQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.exception.OrganizationNotFoundException;
import com.ryuqq.authhub.domain.organization.fixture.OrganizationFixture;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.organization.query.criteria.OrganizationSearchCriteria;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;

/**
 * OrganizationReadManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * 
 * <ul>
 *   <li>ReadManager는 QueryPort 위임 + 예외 변환 담당
 * <li>Port 호출이 올바르게 위임되는지 검증
 * <li>조회 실패 시 적절한 DomainException 발생 검증
 * ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OrganizationReadManager 단위 테스트")
class OrganizationReadManagerTest {

    @Mock private OrganizationQueryPort queryPort;

    
    private OrganizationReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new OrganizationReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 조직이 존재하면 해당 조직 반환")
        void shouldReturnOrganization_WhenExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();
            Organization expected = OrganizationFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Organization result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 조직이 존재하지 않으면 OrganizationNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id))
                    .isInstanceOf(OrganizationNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(queryPort.existsById(id)).willReturn(true);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            OrganizationId id = OrganizationFixture.defaultId();

            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByTenantIdAndName 메서드")
    class ExistsByTenantIdAndName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("Test Org");

            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(true);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantId tenantId = OrganizationFixture.defaultTenantId();
            OrganizationName name = OrganizationName.of("NonExistent Org");

            given(queryPort.existsByTenantIdAndName(tenantId, name)).willReturn(false);

            // when
            boolean result = sut.existsByTenantIdAndName(tenantId, name);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 조직 목록 반환")
        void shouldReturnOrganizations_MatchingCriteria() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of         List.of(OrganizationFixture.
                    null, null, DateR
                    0, 10 n> expected =
                    List.of(         OrganizationFixture.createWithName("Org 2"))
                    
            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<Organization> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findAllBySearchCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 조직이 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoMatch() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of         null, "NonExistent", null, D
                    
            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(List.of());

            // when
            List<Organization> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 조직 개수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of         List.of(OrganizationFixture.
                    null, null, DateR
                    0, 10 
            given(queryPort.countBySearchCriteria(criteria)).willReturn(15L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(15L);
            then(queryPort).should().countBySearchCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 조직이 없으면 0 반환")
        void shouldReturnZero_WhenNoMatch() {
            // given
            OrganizationSearchCriteria criteria =
                    OrganizationSearchCriteria.of         null, "NonExistent", null, D
                    
            given(queryPort.countBySearchCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
