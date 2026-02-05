package com.ryuqq.authhub.application.permission.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.port.out.query.PermissionQueryPort;
import com.ryuqq.authhub.domain.common.vo.DateRange;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permission.query.criteria.PermissionSearchCriteria;
import com.ryuqq.authhub.domain.service.id.ServiceId;
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
 * PermissionReadManager 단위 테스트
 *
 * <p><strong>테스트 설계 원칙:</strong>
 *
 * <ul>
 *   <li>ReadManager는 QueryPort 위임 + 예외 변환 담당
 *   <li>Port 호출이 올바르게 위임되는지 검증
 *   <li>조회 실패 시 적절한 DomainException 발생 검증 ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionReadManager 단위 테스트")
class PermissionReadManagerTest {

    @Mock private PermissionQueryPort queryPort;

    private PermissionReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionReadManager(queryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 권한이 존재하면 해당 권한 반환")
        void shouldReturnPermission_WhenExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();
            Permission expected = PermissionFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Permission result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 권한이 존재하지 않으면 PermissionNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id))
                    .isInstanceOf(PermissionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsById 메서드")
    class ExistsById {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();

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
            PermissionId id = PermissionFixture.defaultId();

            given(queryPort.existsById(id)).willReturn(false);

            // when
            boolean result = sut.existsById(id);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByServiceIdAndPermissionKey 메서드")
    class ExistsByServiceIdAndPermissionKey {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "user:read";

            given(queryPort.existsByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(true);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "nonexistent:action";

            given(queryPort.existsByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(false);

            // when
            boolean result = sut.existsByServiceIdAndPermissionKey(serviceId, permissionKey);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 권한 목록 반환")
        void shouldReturnPermissions_MatchingCriteria() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);
            List<Permission> expected = List.of(PermissionFixture.create());

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<Permission> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllBySearchCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 권한이 없으면 빈 목록 반환")
        void shouldReturnEmptyList_WhenNoMatch() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(List.of());

            // when
            List<Permission> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds 메서드")
    class FindAllByIds {

        @Test
        @DisplayName("성공: ID 목록에 해당하는 권한 목록 반환")
        void shouldReturnPermissions_ForIds() {
            // given
            List<PermissionId> ids = List.of(PermissionFixture.defaultId());
            List<Permission> expected = List.of(PermissionFixture.create());

            given(queryPort.findAllByIds(ids)).willReturn(expected);

            // when
            List<Permission> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByIds(ids);
        }
    }

    @Nested
    @DisplayName("countBySearchCriteria 메서드")
    class CountBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 권한 개수 반환")
        void shouldReturnCount_MatchingCriteria() {
            // given
            PermissionSearchCriteria criteria =
                    PermissionSearchCriteria.ofDefault(
                            null, null, null, DateRange.of(null, null), 0, 10);

            given(queryPort.countBySearchCriteria(criteria)).willReturn(42L);

            // when
            long result = sut.countBySearchCriteria(criteria);

            // then
            assertThat(result).isEqualTo(42L);
            then(queryPort).should().countBySearchCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findByServiceIdAndPermissionKeyOptional 메서드")
    class FindByServiceIdAndPermissionKeyOptional {

        @Test
        @DisplayName("존재하면 Optional.of(Permission) 반환")
        void shouldReturnOptionalOf_WhenExists() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "user:read";
            Permission expected = PermissionFixture.create();

            given(queryPort.findByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(Optional.of(expected));

            // when
            Optional<Permission> result =
                    sut.findByServiceIdAndPermissionKeyOptional(serviceId, permissionKey);

            // then
            assertThat(result).isPresent().contains(expected);
            then(queryPort).should().findByServiceIdAndPermissionKey(serviceId, permissionKey);
        }

        @Test
        @DisplayName("존재하지 않으면 Optional.empty 반환")
        void shouldReturnEmpty_WhenNotExists() {
            // given
            ServiceId serviceId = null;
            String permissionKey = "nonexistent:action";

            given(queryPort.findByServiceIdAndPermissionKey(serviceId, permissionKey))
                    .willReturn(Optional.empty());

            // when
            Optional<Permission> result =
                    sut.findByServiceIdAndPermissionKeyOptional(serviceId, permissionKey);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByServiceIdAndPermissionKey(serviceId, permissionKey);
        }
    }

    @Nested
    @DisplayName("findAllByPermissionKeys 메서드")
    class FindAllByPermissionKeys {

        @Test
        @DisplayName("성공: permissionKey 목록에 해당하는 권한 목록 반환")
        void shouldReturnPermissions_ForPermissionKeys() {
            // given
            List<String> permissionKeys = List.of("user:read", "role:create");
            List<Permission> expected = List.of(PermissionFixture.create());

            given(queryPort.findAllByPermissionKeys(permissionKeys)).willReturn(expected);

            // when
            List<Permission> result = sut.findAllByPermissionKeys(permissionKeys);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllByPermissionKeys(permissionKeys);
        }

        @Test
        @DisplayName("null 입력 시 빈 목록 반환, Port 미호출")
        void shouldReturnEmptyList_WhenInputIsNull() {
            // when
            List<Permission> result = sut.findAllByPermissionKeys(null);

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록 반환, Port 미호출")
        void shouldReturnEmptyList_WhenInputIsEmpty() {
            // when
            List<Permission> result = sut.findAllByPermissionKeys(List.of());

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }
    }
}
