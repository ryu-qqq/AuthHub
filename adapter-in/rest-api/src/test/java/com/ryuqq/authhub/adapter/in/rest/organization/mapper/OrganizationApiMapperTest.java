package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OrganizationApiMapper 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("mapper")
@DisplayName("OrganizationApiMapper 테스트")
class OrganizationApiMapperTest {

    private OrganizationApiMapper mapper;

    private static final UUID TENANT_UUID = UUID.fromString("01941234-5678-7000-8000-123456789abc");
    private static final UUID ORG_UUID = UUID.fromString("01941234-5678-7000-8000-123456789def");
    private static final Instant FIXED_INSTANT = Instant.parse("2025-01-01T00:00:00Z");
    private static final Instant NOW = Instant.now();
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
        mapper = new OrganizationApiMapper();
    }

    @Nested
    @DisplayName("toCommand(CreateOrganizationApiRequest) 테스트")
    class ToCreateCommandTest {

        @Test
        @DisplayName("CreateOrganizationApiRequest를 CreateOrganizationCommand로 변환 성공")
        void givenCreateRequest_whenToCommand_thenSuccess() {
            // given
            CreateOrganizationApiRequest request =
                    new CreateOrganizationApiRequest(TENANT_UUID.toString(), "Test Organization");

            // when
            CreateOrganizationCommand command = mapper.toCommand(request);

            // then
            assertThat(command.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(command.name()).isEqualTo("Test Organization");
        }
    }

    @Nested
    @DisplayName("toCommand(String, UpdateOrganizationApiRequest) 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateOrganizationApiRequest를 UpdateOrganizationCommand로 변환 성공")
        void givenUpdateRequest_whenToCommand_thenSuccess() {
            // given
            UpdateOrganizationApiRequest request = new UpdateOrganizationApiRequest("Updated Name");

            // when
            UpdateOrganizationCommand command = mapper.toCommand(ORG_UUID.toString(), request);

            // then
            assertThat(command.organizationId()).isEqualTo(ORG_UUID);
            assertThat(command.name()).isEqualTo("Updated Name");
        }
    }

    @Nested
    @DisplayName("toStatusCommand() 테스트")
    class ToStatusCommandTest {

        @Test
        @DisplayName("UpdateOrganizationStatusApiRequest를 UpdateOrganizationStatusCommand로 변환 성공")
        void givenStatusRequest_whenToStatusCommand_thenSuccess() {
            // given
            UpdateOrganizationStatusApiRequest request =
                    new UpdateOrganizationStatusApiRequest("INACTIVE");

            // when
            UpdateOrganizationStatusCommand command =
                    mapper.toStatusCommand(ORG_UUID.toString(), request);

            // then
            assertThat(command.organizationId()).isEqualTo(ORG_UUID);
            assertThat(command.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() 테스트")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("organizationId를 DeleteOrganizationCommand로 변환 성공")
        void givenOrganizationId_whenToDeleteCommand_thenSuccess() {
            // when
            DeleteOrganizationCommand command = mapper.toDeleteCommand(ORG_UUID.toString());

            // then
            assertThat(command.organizationId()).isEqualTo(ORG_UUID);
        }
    }

    @Nested
    @DisplayName("toGetQuery() 테스트")
    class ToGetQueryTest {

        @Test
        @DisplayName("organizationId를 GetOrganizationQuery로 변환 성공")
        void givenOrganizationId_whenToGetQuery_thenSuccess() {
            // when
            GetOrganizationQuery query = mapper.toGetQuery(ORG_UUID.toString());

            // then
            assertThat(query.organizationId()).isEqualTo(ORG_UUID);
        }
    }

    @Nested
    @DisplayName("toQuery(SearchOrganizationsApiRequest) 테스트")
    class ToSearchQueryTest {

        @Test
        @DisplayName("SearchOrganizationsApiRequest를 SearchOrganizationsQuery로 변환 성공")
        void givenSearchRequest_whenToQuery_thenSuccess() {
            // given
            SearchOrganizationsApiRequest request =
                    new SearchOrganizationsApiRequest(
                            TENANT_UUID.toString(),
                            "Engineering",
                            List.of("ACTIVE"),
                            ONE_MONTH_AGO,
                            NOW,
                            0,
                            20);

            // when
            SearchOrganizationsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(query.name()).isEqualTo("Engineering");
            assertThat(query.statuses()).containsExactly("ACTIVE");
            assertThat(query.createdFrom()).isEqualTo(ONE_MONTH_AGO);
            assertThat(query.createdTo()).isEqualTo(NOW);
            assertThat(query.page()).isZero();
            assertThat(query.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("선택적 필드가 null인 경우 기본값 사용")
        void givenSearchRequestWithNulls_whenToQuery_thenUsesDefaults() {
            // given
            SearchOrganizationsApiRequest request =
                    new SearchOrganizationsApiRequest(
                            TENANT_UUID.toString(), null, null, ONE_MONTH_AGO, NOW, null, null);

            // when
            SearchOrganizationsQuery query = mapper.toQuery(request);

            // then
            assertThat(query.tenantId()).isEqualTo(TENANT_UUID);
            assertThat(query.name()).isNull();
            assertThat(query.statuses()).isNull();
            assertThat(query.page()).isEqualTo(SearchOrganizationsApiRequest.DEFAULT_PAGE);
            assertThat(query.size()).isEqualTo(SearchOrganizationsApiRequest.DEFAULT_PAGE_SIZE);
        }
    }

    @Nested
    @DisplayName("toCreateResponse() 테스트")
    class ToCreateResponseTest {

        @Test
        @DisplayName("OrganizationResponse를 CreateOrganizationApiResponse로 변환 성공")
        void givenOrganizationResponse_whenToCreateResponse_thenSuccess() {
            // given
            OrganizationResponse response =
                    new OrganizationResponse(
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Org",
                            "ACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            CreateOrganizationApiResponse apiResponse = mapper.toCreateResponse(response);

            // then
            assertThat(apiResponse.organizationId()).isEqualTo(ORG_UUID.toString());
        }
    }

    @Nested
    @DisplayName("toApiResponse() 테스트")
    class ToApiResponseTest {

        @Test
        @DisplayName("OrganizationResponse를 OrganizationApiResponse로 변환 성공")
        void givenOrganizationResponse_whenToApiResponse_thenSuccess() {
            // given
            OrganizationResponse response =
                    new OrganizationResponse(
                            ORG_UUID,
                            TENANT_UUID,
                            "Test Org",
                            "ACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            OrganizationApiResponse apiResponse = mapper.toApiResponse(response);

            // then
            assertThat(apiResponse.organizationId()).isEqualTo(ORG_UUID.toString());
            assertThat(apiResponse.tenantId()).isEqualTo(TENANT_UUID.toString());
            assertThat(apiResponse.name()).isEqualTo("Test Org");
            assertThat(apiResponse.status()).isEqualTo("ACTIVE");
            assertThat(apiResponse.createdAt()).isEqualTo(FIXED_INSTANT);
            assertThat(apiResponse.updatedAt()).isEqualTo(FIXED_INSTANT);
        }
    }

    @Nested
    @DisplayName("toApiResponseList() 테스트")
    class ToApiResponseListTest {

        @Test
        @DisplayName("OrganizationResponse 목록을 OrganizationApiResponse 목록으로 변환 성공")
        void givenResponseList_whenToApiResponseList_thenSuccess() {
            // given
            OrganizationResponse response1 =
                    new OrganizationResponse(
                            ORG_UUID, TENANT_UUID, "Org1", "ACTIVE", FIXED_INSTANT, FIXED_INSTANT);
            OrganizationResponse response2 =
                    new OrganizationResponse(
                            UUID.randomUUID(),
                            TENANT_UUID,
                            "Org2",
                            "INACTIVE",
                            FIXED_INSTANT,
                            FIXED_INSTANT);

            // when
            List<OrganizationApiResponse> apiResponses =
                    mapper.toApiResponseList(List.of(response1, response2));

            // then
            assertThat(apiResponses).hasSize(2);
            assertThat(apiResponses.get(0).name()).isEqualTo("Org1");
            assertThat(apiResponses.get(1).name()).isEqualTo("Org2");
        }

        @Test
        @DisplayName("빈 목록도 변환 성공")
        void givenEmptyList_whenToApiResponseList_thenReturnsEmptyList() {
            // when
            List<OrganizationApiResponse> apiResponses = mapper.toApiResponseList(List.of());

            // then
            assertThat(apiResponses).isEmpty();
        }
    }
}
