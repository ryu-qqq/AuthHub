package com.ryuqq.authhub.application.user.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.user.dto.query.GetUserQuery;
import com.ryuqq.authhub.application.user.dto.response.UserDetailResponse;
import com.ryuqq.authhub.application.user.port.out.query.UserAdminQueryPort;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.fixture.UserFixture;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import java.time.Instant;
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
 * GetUserDetailService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserDetailService 단위 테스트")
class GetUserDetailServiceTest {

    @Mock private UserAdminQueryPort adminQueryPort;

    private GetUserDetailService service;

    private static final UUID USER_UUID = UserFixture.defaultUUID();
    private static final UUID TENANT_UUID = UserFixture.defaultTenantUUID();
    private static final UUID ORG_UUID = UserFixture.defaultOrganizationUUID();

    @BeforeEach
    void setUp() {
        service = new GetUserDetailService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("사용자 상세 정보를 성공적으로 조회한다")
        void shouldGetUserDetailSuccessfully() {
            // given
            GetUserQuery query = new GetUserQuery(USER_UUID);
            UserDetailResponse expectedResponse =
                    new UserDetailResponse(
                            USER_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            ORG_UUID,
                            "테스트 조직",
                            "user@example.com",
                            "010-1234-5678",
                            "ACTIVE",
                            List.of(),
                            Instant.now(),
                            Instant.now());

            given(adminQueryPort.findUserDetail(UserId.of(USER_UUID)))
                    .willReturn(Optional.of(expectedResponse));

            // when
            UserDetailResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(adminQueryPort).findUserDetail(UserId.of(USER_UUID));
        }

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenUserNotFound() {
            // given
            UUID nonExistingUserId = UUID.randomUUID();
            GetUserQuery query = new GetUserQuery(nonExistingUserId);

            given(adminQueryPort.findUserDetail(UserId.of(nonExistingUserId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
