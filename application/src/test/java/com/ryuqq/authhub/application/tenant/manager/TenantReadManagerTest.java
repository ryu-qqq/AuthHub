package com.ryuqq.authhub.application.tenant.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.fixture.TenantFixture;
import com.ryuqq.authhub.domain.tenant.id.TenantId;
import com.ryuqq.authhub.domain.tenant.query.criteria.TenantSearchCriteria;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
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

/**
 * TenantReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantReadManager 단위 테스트")
class TenantReadManagerTest {

    @Mock private TenantQueryPort queryPort;

    private TenantReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new TenantReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 테넌트가 존재하면 해당 테넌트 반환")
        void shouldReturnTenant_WhenExists() {
            // given
            TenantId id = TenantFixture.defaultId();
            Tenant expected = TenantFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Tenant result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 테넌트가 존재하지 않으면 TenantNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            TenantId id = TenantFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id)).isInstanceOf(TenantNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantId id = TenantFixture.defaultId();

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
            TenantId id = TenantFixture.defaultId();

            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByName 메서드")
    class ExistsByName {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantName name = TenantName.of("Test Tenant");

            given(queryPort.existsByName(name)).willReturn(true);

            // when
            boolean result = sut.existsByName(name);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("findAllByCriteria 메서드")
    class FindAllByCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 테넌트 목록 반환")
        void shouldReturnTenants_MatchingCriteria() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.ofSimple(null, null, DateRange.of(null, null), 0, 10);
            List<Tenant> expected = List.of(TenantFixture.create());

            given(queryPort.findAllByCriteria(criteria)).willReturn(expected);

            // when
            List<Tenant> result = sut.findAllByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria 메서드")
    class CountByCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 개수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            TenantSearchCriteria criteria =
                    TenantSearchCriteria.ofSimple(null, null, DateRange.of(null, null), 0, 10);
            given(queryPort.countByCriteria(criteria)).willReturn(42L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(42L);
            then(queryPort).should().countByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByIdOptional 메서드")
    class FindByIdOptional {

        @Test
        @DisplayName("존재하면 Optional.of(tenant) 반환")
        void shouldReturnOptionalOfTenant_WhenExists() {
            // given
            TenantId id = TenantFixture.defaultId();
            Tenant expected = TenantFixture.create();
            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Optional<Tenant> result = sut.findByIdOptional(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않으면 빈 Optional 반환")
        void shouldReturnEmptyOptional_WhenNotExists() {
            // given
            TenantId id = TenantFixture.defaultId();
            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when
            Optional<Tenant> result = sut.findByIdOptional(id);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("existsByNameAndIdNot 메서드")
    class ExistsByNameAndIdNot {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            TenantName name = TenantName.of("Other Tenant");
            TenantId excludeId = TenantFixture.defaultId();
            given(queryPort.existsByNameAndIdNot(name, excludeId)).willReturn(true);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().existsByNameAndIdNot(name, excludeId);
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            TenantName name = TenantName.of("Unique Name");
            TenantId excludeId = TenantFixture.defaultId();
            given(queryPort.existsByNameAndIdNot(name, excludeId)).willReturn(false);

            // when
            boolean result = sut.existsByNameAndIdNot(name, excludeId);

            // then
            assertThat(result).isFalse();
            then(queryPort).should().existsByNameAndIdNot(name, excludeId);
        }
    }
}
