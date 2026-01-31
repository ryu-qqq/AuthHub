package com.ryuqq.authhub.application.role.manager;

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
import com.ryuqq.authhub.application.role.port.out.query.RoleQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.fixture.RoleFixture;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.query.criteria.RoleSearchCriteria;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.id.TenantId;

/**
 * RoleReadManager 단위 테스트
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
@DisplayName("RoleReadManager 단위 테스트")
class RoleReadManagerTest {

    @Mock private RoleQueryPort queryPort;

    
    private RoleReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new RoleReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 역할이 존재하면 해당 역할 반환")
        void shouldReturnRole_WhenExists() {
            // given
            RoleId id = RoleFixture.defaultId();
            Role expected = RoleFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Role result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 역할이 존재하지 않으면 RoleNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            RoleId id = RoleFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id)).isInstanceOf(RoleNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            RoleId id = RoleFixture.defaultId();

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
            RoleId id = RoleFixture.defaultId();

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
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("TEST_ROLE");

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
            TenantId tenantId = RoleFixture.defaultTenantId();
            RoleName name = RoleName.of("NONEXISTENT_ROLE");

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
        @DisplayName("성공: Criteria에 맞는 역할 목록 반환")
        void shouldReturnRoles_MatchingCriteria() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);
            List<Role> expected = List.of(RoleFixture.create());

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<Role> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllBySearchCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 역할이 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoMatch() {
            // given
            RoleSearchCriteria criteria =
                    RoleSearchCriteria.ofGlobal(null, null, DateRange.of(null, null), 0, 10);

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(List.of());

            // when
            List<Role> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIds {

        @Test
        @DisplayName("성공: ID 목록에 해당하는 역할 목록 반환")
        void shouldReturnRoles_ForIds() {
            // given
            List<RoleId> ids = List.of(RoleFixture.defaultId());
            List<Role> expected = List.of(RoleFixture.create());

            given(queryPort.findAllByIds(ids)).willReturn(expected);

            // when
            List<Role> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByIds(ids);
        }
    }
}
