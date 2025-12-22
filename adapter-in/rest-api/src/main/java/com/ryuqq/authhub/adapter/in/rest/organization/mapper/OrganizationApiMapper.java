package com.ryuqq.authhub.adapter.in.rest.organization.mapper;

import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.CreateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.command.UpdateOrganizationStatusApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.query.SearchOrganizationsApiRequest;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.CreateOrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationApiResponse;
import com.ryuqq.authhub.adapter.in.rest.organization.dto.response.OrganizationUserApiResponse;
import com.ryuqq.authhub.application.common.dto.response.PageResponse;
import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.DeleteOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationCommand;
import com.ryuqq.authhub.application.organization.dto.command.UpdateOrganizationStatusCommand;
import com.ryuqq.authhub.application.organization.dto.query.GetOrganizationQuery;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationUsersQuery;
import com.ryuqq.authhub.application.organization.dto.query.SearchOrganizationsQuery;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationResponse;
import com.ryuqq.authhub.application.organization.dto.response.OrganizationUserResponse;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * OrganizationApiMapper - 조직 API DTO 변환기
 *
 * <p>REST API DTO와 Application DTO 간의 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 필수
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지
 *   <li>단순 변환만 수행
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationApiMapper {

    /**
     * CreateOrganizationApiRequest → CreateOrganizationCommand 변환
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public CreateOrganizationCommand toCommand(CreateOrganizationApiRequest request) {
        return new CreateOrganizationCommand(UUID.fromString(request.tenantId()), request.name());
    }

    /**
     * UpdateOrganizationApiRequest → UpdateOrganizationCommand 변환
     *
     * @param organizationId 조직 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateOrganizationCommand toCommand(
            String organizationId, UpdateOrganizationApiRequest request) {
        return new UpdateOrganizationCommand(UUID.fromString(organizationId), request.name());
    }

    /**
     * UpdateOrganizationStatusApiRequest → UpdateOrganizationStatusCommand 변환
     *
     * @param organizationId 조직 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateOrganizationStatusCommand toStatusCommand(
            String organizationId, UpdateOrganizationStatusApiRequest request) {
        return new UpdateOrganizationStatusCommand(
                UUID.fromString(organizationId), request.status());
    }

    /**
     * organizationId → DeleteOrganizationCommand 변환
     *
     * @param organizationId 조직 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteOrganizationCommand toDeleteCommand(String organizationId) {
        return new DeleteOrganizationCommand(UUID.fromString(organizationId));
    }

    /**
     * organizationId → GetOrganizationQuery 변환
     *
     * @param organizationId 조직 ID (PathVariable)
     * @return Application Query DTO
     */
    public GetOrganizationQuery toGetQuery(String organizationId) {
        return new GetOrganizationQuery(UUID.fromString(organizationId));
    }

    /**
     * SearchOrganizationsApiRequest → SearchOrganizationsQuery 변환
     *
     * @param request API 요청 DTO
     * @return Application Query DTO
     */
    public SearchOrganizationsQuery toQuery(SearchOrganizationsApiRequest request) {
        return new SearchOrganizationsQuery(
                UUID.fromString(request.tenantId()),
                request.name(),
                request.status(),
                request.page(),
                request.size());
    }

    /**
     * OrganizationResponse → CreateOrganizationApiResponse 변환
     *
     * @param response 생성된 조직 Response
     * @return API 응답 DTO
     */
    public CreateOrganizationApiResponse toCreateResponse(OrganizationResponse response) {
        return new CreateOrganizationApiResponse(response.organizationId().toString());
    }

    /**
     * OrganizationResponse → OrganizationApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public OrganizationApiResponse toApiResponse(OrganizationResponse response) {
        return new OrganizationApiResponse(
                response.organizationId().toString(),
                response.tenantId().toString(),
                response.name(),
                response.status(),
                response.createdAt(),
                response.updatedAt());
    }

    /**
     * OrganizationResponse 목록 → OrganizationApiResponse 목록 변환
     *
     * @param responses Application Response DTO 목록
     * @return API 응답 DTO 목록
     */
    public List<OrganizationApiResponse> toApiResponseList(List<OrganizationResponse> responses) {
        return responses.stream().map(this::toApiResponse).toList();
    }

    // ===== OrganizationUser 관련 변환 메서드 =====

    /**
     * organizationId + 페이징 정보 → SearchOrganizationUsersQuery 변환
     *
     * @param organizationId 조직 ID (PathVariable)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Application Query DTO
     */
    public SearchOrganizationUsersQuery toOrganizationUsersQuery(
            String organizationId, Integer page, Integer size) {
        OrganizationId id = OrganizationId.of(UUID.fromString(organizationId));
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        return SearchOrganizationUsersQuery.of(id, pageNum, pageSize);
    }

    /**
     * OrganizationUserResponse → OrganizationUserApiResponse 변환
     *
     * @param response Application Response DTO
     * @return API 응답 DTO
     */
    public OrganizationUserApiResponse toOrganizationUserApiResponse(
            OrganizationUserResponse response) {
        return OrganizationUserApiResponse.of(
                response.userId(), response.email(), response.tenantId(), response.createdAt());
    }

    /**
     * PageResponse<OrganizationUserResponse> → PageResponse<OrganizationUserApiResponse> 변환
     *
     * @param pageResponse Application Page Response
     * @return API Page Response
     */
    public PageResponse<OrganizationUserApiResponse> toOrganizationUserApiPageResponse(
            PageResponse<OrganizationUserResponse> pageResponse) {
        List<OrganizationUserApiResponse> content =
                pageResponse.content().stream().map(this::toOrganizationUserApiResponse).toList();
        return PageResponse.of(
                content,
                pageResponse.page(),
                pageResponse.size(),
                pageResponse.totalElements(),
                pageResponse.totalPages(),
                pageResponse.first(),
                pageResponse.last());
    }
}
