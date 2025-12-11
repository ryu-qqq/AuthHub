package com.ryuqq.authhub.adapter.in.rest.endpointpermission.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.CreateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.command.UpdateEndpointPermissionApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.query.SearchEndpointPermissionsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.CreateEndpointPermissionApiResponse;
import com.ryuqq.authhub.adapter.in.rest.endpointpermission.dto.response.EndpointPermissionApiResponse;
import com.ryuqq.authhub.application.endpointpermission.dto.command.CreateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.DeleteEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.command.UpdateEndpointPermissionCommand;
import com.ryuqq.authhub.application.endpointpermission.dto.query.GetEndpointPermissionQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.query.SearchEndpointPermissionsQuery;
import com.ryuqq.authhub.application.endpointpermission.dto.response.EndpointPermissionResponse;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * EndpointPermissionApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("EndpointPermissionApiMapper 테스트")
class EndpointPermissionApiMapperTest {

    private EndpointPermissionApiMapper mapper;

    private static final String ENDPOINT_ID = "auth-service:/api/users:GET";
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        mapper = new EndpointPermissionApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateEndpointPermissionApiRequest) 테스트")
    class ToCreateCommandTest {

        @Test
        @DisplayName("CreateEndpointPermissionApiRequest를 CreateEndpointPermissionCommand로 변환 성공")
        void givenCreateRequest_whenToCommand_thenSuccess() {
            // given
            CreateEndpointPermissionApiRequest request =
                    new CreateEndpointPermissionApiRequest(
                            "auth-service",
                            "/api/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("USER"));

            // when
            CreateEndpointPermissionCommand command = mapper.toCommand(request);

            // then
            assertThat(command.serviceName()).isEqualTo("auth-service");
            assertThat(command.path()).isEqualTo("/api/users");
            assertThat(command.method()).isEqualTo("GET");
            assertThat(command.description()).isEqualTo("사용자 목록 조회");
            assertThat(command.isPublic()).isFalse();
            assertThat(command.requiredPermissions()).containsExactly("user:read");
            assertThat(command.requiredRoles()).containsExactly("USER");
        }

        @Test
        @DisplayName("공개 엔드포인트 생성 요청 변환 성공")
        void givenPublicEndpointRequest_whenToCommand_thenSuccess() {
            // given
            CreateEndpointPermissionApiRequest request =
                    new CreateEndpointPermissionApiRequest(
                            "auth-service", "/api/health", "GET", "헬스체크", true, Set.of(), Set.of());

            // when
            CreateEndpointPermissionCommand command = mapper.toCommand(request);

            // then
            assertThat(command.isPublic()).isTrue();
            assertThat(command.requiredPermissions()).isEmpty();
            assertThat(command.requiredRoles()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdateEndpointPermissionApiRequest) 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateEndpointPermissionApiRequest를 UpdateEndpointPermissionCommand로 변환 성공")
        void givenUpdateRequest_whenToCommand_thenSuccess() {
            // given
            UpdateEndpointPermissionApiRequest request =
                    new UpdateEndpointPermissionApiRequest(
                            "수정된 설명", false, Set.of("user:read", "user:write"), Set.of("ADMIN"));

            // when
            UpdateEndpointPermissionCommand command = mapper.toCommand(ENDPOINT_ID, request);

            // then
            assertThat(command.endpointPermissionId()).isEqualTo(ENDPOINT_ID);
            assertThat(command.description()).isEqualTo("수정된 설명");
            assertThat(command.isPublic()).isFalse();
            assertThat(command.requiredPermissions())
                    .containsExactlyInAnyOrder("user:read", "user:write");
            assertThat(command.requiredRoles()).containsExactly("ADMIN");
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() 테스트")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("endpointPermissionId를 DeleteEndpointPermissionCommand로 변환 성공")
        void givenEndpointPermissionId_whenToDeleteCommand_thenSuccess() {
            // when
            DeleteEndpointPermissionCommand command = mapper.toDeleteCommand(ENDPOINT_ID);

            // then
            assertThat(command.endpointPermissionId()).isEqualTo(ENDPOINT_ID);
        }
    }

    @Nested
    @DisplayName("toGetQuery() 테스트")
    class ToGetQueryTest {

        @Test
        @DisplayName("endpointPermissionId를 GetEndpointPermissionQuery로 변환 성공")
        void givenEndpointPermissionId_whenToGetQuery_thenSuccess() {
            // when
            GetEndpointPermissionQuery query = mapper.toGetQuery(ENDPOINT_ID);

            // then
            assertThat(query.endpointPermissionId()).isEqualTo(ENDPOINT_ID);
        }
    }

    @Nested
    @DisplayName("toQuery(SearchEndpointPermissionsApiRequest) 테스트")
    class ToSearchQueryTest {

        @Test
        @DisplayName("SearchEndpointPermissionsApiRequest를 SearchEndpointPermissionsQuery로 변환 성공")
        void givenSearchRequest_whenToQuery_thenSuccess() {
            // given
            SearchEndpointPermissionsApiRequest request =
                    new SearchEndpointPermissionsApiRequest(
                            "auth-service", "/api/*", "GET", false, 0, 20);

            // when
            SearchEndpointPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.serviceName()).isEqualTo("auth-service");
            assertThat(query.pathPattern()).isEqualTo("/api/*");
            assertThat(query.method()).isEqualTo("GET");
            assertThat(query.isPublic()).isFalse();
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("null 값들도 변환 성공")
        void givenNullValues_whenToQuery_thenSuccess() {
            // given
            SearchEndpointPermissionsApiRequest request =
                    new SearchEndpointPermissionsApiRequest(null, null, null, null, 0, 10);

            // when
            SearchEndpointPermissionsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.serviceName()).isNull();
            assertThat(query.pathPattern()).isNull();
            assertThat(query.method()).isNull();
            assertThat(query.isPublic()).isNull();
        }
    }

    @Nested
    @DisplayName("toCreateResponse() 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("EndpointPermissionResponse를 CreateEndpointPermissionApiResponse로 변환 성공")
        void givenEndpointPermissionResponse_whenToCreateResponse_thenSuccess() {
            // given
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            ENDPOINT_ID,
                            "auth-service",
                            "/api/users",
                            "GET",
                            "설명",
                            false,
                            Set.of("user:read"),
                            Set.of("USER"),
                            1L,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            CreateEndpointPermissionApiResponse apiResponse = mapper.toCreateResponse(response);

            // then
            assertThat(apiResponse.id()).isEqualTo(ENDPOINT_ID);
        }
    }

    @Nested
    @DisplayName("toApiResponse() 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("EndpointPermissionResponse를 EndpointPermissionApiResponse로 변환 성공")
        void givenEndpointPermissionResponse_whenToApiResponse_thenSuccess() {
            // given
            EndpointPermissionResponse response =
                    new EndpointPermissionResponse(
                            ENDPOINT_ID,
                            "auth-service",
                            "/api/users",
                            "GET",
                            "사용자 목록 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("USER"),
                            1L,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            EndpointPermissionApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.id()).isEqualTo(ENDPOINT_ID);
            assertThat(apiResponse.serviceName()).isEqualTo("auth-service");
            assertThat(apiResponse.path()).isEqualTo("/api/users");
            assertThat(apiResponse.method()).isEqualTo("GET");
            assertThat(apiResponse.description()).isEqualTo("사용자 목록 조회");
            assertThat(apiResponse.isPublic()).isFalse();
            assertThat(apiResponse.requiredPermissions()).containsExactly("user:read");
            assertThat(apiResponse.requiredRoles()).containsExactly("USER");
            assertThat(apiResponse.version()).isEqualTo(1L);
            assertThat(apiResponse.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(apiResponse.updatedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("toApiResponseList() 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("EndpointPermissionResponse 목록을 EndpointPermissionApiResponse 목록으로 변환 성공")
        void givenResponseList_whenToApiResponseList_thenSuccess() {
            // given
            EndpointPermissionResponse response1 =
                    new EndpointPermissionResponse(
                            "auth-service:/api/users:GET",
                            "auth-service",
                            "/api/users",
                            "GET",
                            "사용자 조회",
                            false,
                            Set.of("user:read"),
                            Set.of("USER"),
                            1L,
                            FIXED_INSTANT,
                            FIXED_INSTANT);
            EndpointPermissionResponse response2 =
                    new EndpointPermissionResponse(
                            "auth-service:/api/users:POST",
                            "auth-service",
                            "/api/users",
                            "POST",
                            "사용자 생성",
                            false,
                            Set.of("user:write"),
                            Set.of("ADMIN"),
                            1L,
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            List<EndpointPermissionApiResponse> apiResponses =
                    mapper.toApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
            assertThat(apiResponses.get(0).method()).isEqualTo("GET");
            assertThat(apiResponses.get(1).method()).isEqualTo("POST");
        }

        @Test
        @DisplayName("빈 목록도 변환 성공")
        void givenEmptyList_whenToApiResponseList_thenReturnsEmptyList() {
            // when
            List<EndpointPermissionApiResponse> apiResponses = mapper.toApiResponseList(List.of());

            // then
            assertThat(apiResponses).isEmpty();
        }
    }
}
