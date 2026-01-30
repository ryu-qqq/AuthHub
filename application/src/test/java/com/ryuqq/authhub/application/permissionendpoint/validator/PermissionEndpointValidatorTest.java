package com.ryuqq.authhub.application.permissionendpoint.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.fixture.PermissionFixture;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException;
import com.ryuqq.authhub.domain.permissionendpoint.fixture.PermissionEndpointFixture;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionEndpointValidator 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("PermissionEndpointValidator 단위 테스트")
class PermissionEndpointValidatorTest {

    @Mock private PermissionReadManager permissionReadManager;

    @Mock private PermissionEndpointReadManager permissionEndpointReadManager;

    private PermissionEndpointValidator sut;

    @BeforeEach
    void setUp() {
        sut = new PermissionEndpointValidator(permissionReadManager, permissionEndpointReadManager);
    }

    @Nested
    @DisplayName("validatePermissionExists 메서드")
    class ValidatePermissionExists {

        @Test
        @DisplayName("성공: 권한이 존재하면 Permission 반환")
        void shouldReturnPermission_WhenExists() {
            // given
            PermissionId id = PermissionFixture.defaultId();
            Permission expected = PermissionFixture.create();

            given(permissionReadManager.findById(id)).willReturn(expected);

            // when
            Permission result = sut.validatePermissionExists(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(permissionReadManager).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findExistingOrThrow 메서드")
    class FindExistingOrThrow {

        @Test
        @DisplayName("성공: 엔드포인트가 존재하면 해당 엔드포인트 반환")
        void shouldReturnPermissionEndpoint_WhenExists() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();
            PermissionEndpoint expected = PermissionEndpointFixture.create();

            given(permissionEndpointReadManager.findById(id)).willReturn(expected);

            // when
            PermissionEndpoint result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(permissionEndpointReadManager).should().findById(id);
        }

        @Test
        @DisplayName("실패: 엔드포인트가 존재하지 않으면 PermissionEndpointNotFoundException 발생")
        void shouldThrowException_WhenNotExists() {
            // given
            PermissionEndpointId id = PermissionEndpointFixture.defaultId();

            given(permissionEndpointReadManager.findById(id))
                    .willThrow(new PermissionEndpointNotFoundException(id));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(PermissionEndpointNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateNoDuplicate 메서드")
    class ValidateNoDuplicate {

        @Test
        @DisplayName("성공: URL 패턴+HTTP 메서드가 중복되지 않으면 예외 없음")
        void shouldNotThrow_WhenNotDuplicated() {
            // given
            String urlPattern = "/api/v1/users";
            HttpMethod httpMethod = HttpMethod.GET;

            given(
                            permissionEndpointReadManager.existsByUrlPatternAndHttpMethod(
                                    urlPattern, httpMethod))
                    .willReturn(false);

            // when & then
            assertThatCode(() -> sut.validateNoDuplicate(urlPattern, httpMethod))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: URL 패턴+HTTP 메서드가 중복되면 DuplicatePermissionEndpointException 발생")
        void shouldThrowException_WhenDuplicated() {
            // given
            String urlPattern = "/api/v1/users";
            HttpMethod httpMethod = HttpMethod.GET;

            given(
                            permissionEndpointReadManager.existsByUrlPatternAndHttpMethod(
                                    urlPattern, httpMethod))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> sut.validateNoDuplicate(urlPattern, httpMethod))
                    .isInstanceOf(DuplicatePermissionEndpointException.class);
        }
    }
}
