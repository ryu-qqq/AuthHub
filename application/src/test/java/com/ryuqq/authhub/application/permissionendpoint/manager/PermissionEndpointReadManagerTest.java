package com.ryuqq.authhub.application.permissionendpoint.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointQueryPort;
import com.ryuqq.authhub.application.permissionendpoint.port.out.query.PermissionEndpointSpecQueryPort;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.query.criteria.PermissionEndpointSearchCriteria;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
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
 * PermissionEndpointReadManager 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointReadManager 단위 테스트")
class PermissionEndpointReadManagerTest {

    @Mock private PermissionEndpointQueryPort queryPort;

    @Mock private PermissionEndpointSpecQueryPort specQueryPort;

    private PermissionEndpointReadManager sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointReadManager(queryPort, specQueryPort);
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("성공: 엔드포인트가 존재하면 해당 엔드포인트 반환")
        void shouldReturnPermissionEndpoint_WhenExists() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            PermissionEndpoint expected = PermissionEndpointFixture.create();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            PermissionEndpoint result = sut.findById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("실패: 엔드포인트가 존재하지 않으면 PermissionEndpointNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findById(id))
                    .isInstanceOf(PermissionEndpointNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("existsByUrlPatternAndHttpMethod 메서드")
    class ExistsByUrlPatternAndHttpMethod {

        @Test
        @DisplayName("존재하면 true 반환")
        void shouldReturnTrue_WhenExists() {
            // given
            String urlPattern = "/api/v1/users";
            HttpMethod httpMethod = HttpMethod.GET;

            given(queryPort.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod))
                    .willReturn(true);

            // when
            boolean result = sut.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false 반환")
        void shouldReturnFalse_WhenNotExists() {
            // given
            String urlPattern = "/api/v1/nonexistent";
            HttpMethod httpMethod = HttpMethod.GET;

            given(queryPort.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod))
                    .willReturn(false);

            // when
            boolean result = sut.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllBySearchCriteria 메서드")
    class FindAllBySearchCriteria {

        @Test
        @DisplayName("성공: Criteria에 맞는 엔드포인트 목록 반환")
        void shouldReturnPermissionEndpoints_MatchingCriteria() {
            // given
            PermissionEndpointSearchCriteria criteria =
                    PermissionEndpointSearchCriteria.forPermission(
                            PermissionEndpointFixture.defaultPermissionIdValue(), 0, 10);
            List<PermissionEndpoint> expected = List.of(PermissionEndpointFixture.create());

            given(queryPort.findAllBySearchCriteria(criteria)).willReturn(expected);

            // when
            List<PermissionEndpoint> result = sut.findAllBySearchCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findAllBySearchCriteria(criteria);
        }
    }
}
