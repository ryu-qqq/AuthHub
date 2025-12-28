package com.ryuqq.authhub.application.role.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.ryuqq.authhub.application.role.dto.query.GetRoleQuery;
import com.ryuqq.authhub.application.role.dto.response.RoleDetailResponse;
import com.ryuqq.authhub.application.role.port.out.query.RoleAdminQueryPort;
import com.ryuqq.authhub.domain.role.exception.RoleNotFoundException;
import com.ryuqq.authhub.domain.role.identifier.RoleId;
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
 * GetRoleDetailService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetRoleDetailService 단위 테스트")
class GetRoleDetailServiceTest {

    @Mock private RoleAdminQueryPort adminQueryPort;

    private GetRoleDetailService service;

    private static final UUID ROLE_UUID = UUID.randomUUID();
    private static final UUID TENANT_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new GetRoleDetailService(adminQueryPort);
    }

    @Nested
    @DisplayName("execute 메서드")
    class ExecuteTest {

        @Test
        @DisplayName("역할 상세 정보를 성공적으로 조회한다")
        void shouldGetRoleDetailSuccessfully() {
            // given
            GetRoleQuery query = new GetRoleQuery(ROLE_UUID);
            RoleDetailResponse expectedResponse =
                    new RoleDetailResponse(
                            ROLE_UUID,
                            TENANT_UUID,
                            "테스트 테넌트",
                            "관리자",
                            "관리자 역할",
                            "TENANT",
                            "CUSTOM",
                            List.of(),
                            5,
                            Instant.now(),
                            Instant.now());

            given(adminQueryPort.findRoleDetail(RoleId.of(ROLE_UUID)))
                    .willReturn(Optional.of(expectedResponse));

            // when
            RoleDetailResponse response = service.execute(query);

            // then
            assertThat(response).isEqualTo(expectedResponse);
            verify(adminQueryPort).findRoleDetail(RoleId.of(ROLE_UUID));
        }

        @Test
        @DisplayName("역할이 존재하지 않으면 예외를 발생시킨다")
        void shouldThrowExceptionWhenRoleNotFound() {
            // given
            UUID nonExistingRoleId = UUID.randomUUID();
            GetRoleQuery query = new GetRoleQuery(nonExistingRoleId);

            given(adminQueryPort.findRoleDetail(RoleId.of(nonExistingRoleId)))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> service.execute(query))
                    .isInstanceOf(RoleNotFoundException.class);
        }
    }
}
