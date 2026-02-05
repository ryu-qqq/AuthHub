package com.ryuqq.authhub.application.permissionendpoint.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.authhub.application.permissionendpoint.assembler.PermissionEndpointAssembler;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecListResult;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.EndpointPermissionSpecResult;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * GetEndpointPermissionSpecService 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetEndpointPermissionSpecService 단위 테스트")
class GetEndpointPermissionSpecServiceTest {

    @Mock private PermissionEndpointReadManager readManager;
    @Mock private PermissionEndpointAssembler assembler;

    private GetEndpointPermissionSpecService sut;

    @BeforeEach
    void setUp() {
        sut = new GetEndpointPermissionSpecService(readManager, assembler);
    }

    @Nested
    @DisplayName("getAll 메서드")
    class GetAll {

        @Test
        @DisplayName(
                "성공: ReadManager findAllActiveSpecs·findLatestUpdatedAt → Assembler"
                        + " toSpecListResult")
        void shouldReturnSpecListResult() {
            List<EndpointPermissionSpecResult> specs =
                    List.of(
                            new EndpointPermissionSpecResult(
                                    "authhub",
                                    "/api/v1/users",
                                    "GET",
                                    "user:read",
                                    false,
                                    "User list"));
            Instant latestUpdatedAt = Instant.parse("2025-01-15T10:00:00Z");
            EndpointPermissionSpecListResult expected =
                    EndpointPermissionSpecListResult.of(specs, latestUpdatedAt);

            given(readManager.findAllActiveSpecs()).willReturn(specs);
            given(readManager.findLatestUpdatedAt()).willReturn(latestUpdatedAt);
            given(assembler.toSpecListResult(specs, latestUpdatedAt)).willReturn(expected);

            EndpointPermissionSpecListResult result = sut.getAll();

            assertThat(result).isEqualTo(expected);
            then(readManager).should().findAllActiveSpecs();
            then(readManager).should().findLatestUpdatedAt();
            then(assembler).should().toSpecListResult(specs, latestUpdatedAt);
        }

        @Test
        @DisplayName("성공: 스펙이 없으면 빈 결과 반환")
        void shouldReturnEmpty_WhenNoSpecs() {
            List<EndpointPermissionSpecResult> specs = List.of();
            Instant latestUpdatedAt = null;
            EndpointPermissionSpecListResult expected = EndpointPermissionSpecListResult.empty();

            given(readManager.findAllActiveSpecs()).willReturn(specs);
            given(readManager.findLatestUpdatedAt()).willReturn(latestUpdatedAt);
            given(assembler.toSpecListResult(specs, latestUpdatedAt)).willReturn(expected);

            EndpointPermissionSpecListResult result = sut.getAll();

            assertThat(result).isEqualTo(expected);
            assertThat(result.endpoints()).isEmpty();
        }
    }
}
