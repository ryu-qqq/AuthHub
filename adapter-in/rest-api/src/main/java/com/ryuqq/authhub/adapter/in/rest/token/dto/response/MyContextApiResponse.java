package com.ryuqq.authhub.adapter.in.rest.token.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 내 정보 조회 API 응답 DTO
 *
 * <p>현재 로그인한 사용자의 전체 컨텍스트 정보를 반환합니다.
 *
 * @param userId 사용자 ID (UUIDv7 문자열)
 * @param email 이메일
 * @param name 사용자 이름
 * @param tenant 테넌트 정보
 * @param organization 조직 정보
 * @param roles 역할 목록
 * @param permissions 권한 키 목록
 * @author development-team
 * @since 1.0.0
 */
@Schema(description = "내 정보 조회 응답")
public record MyContextApiResponse(
        @Schema(description = "사용자 ID") String userId,
        @Schema(description = "이메일") String email,
        @Schema(description = "사용자 이름") String name,
        @Schema(description = "테넌트 정보") TenantInfo tenant,
        @Schema(description = "조직 정보") OrganizationInfo organization,
        @Schema(description = "역할 목록") List<RoleInfo> roles,
        @Schema(description = "권한 키 목록") List<String> permissions) {

    /**
     * 테넌트 정보
     *
     * @param id 테넌트 ID
     * @param name 테넌트 이름
     */
    @Schema(description = "테넌트 정보")
    public record TenantInfo(
            @Schema(description = "테넌트 ID") String id,
            @Schema(description = "테넌트 이름") String name) {}

    /**
     * 조직 정보
     *
     * @param id 조직 ID
     * @param name 조직 이름
     */
    @Schema(description = "조직 정보")
    public record OrganizationInfo(
            @Schema(description = "조직 ID") String id, @Schema(description = "조직 이름") String name) {}

    /**
     * 역할 정보
     *
     * @param id 역할 ID
     * @param name 역할 이름
     */
    @Schema(description = "역할 정보")
    public record RoleInfo(
            @Schema(description = "역할 ID") String id, @Schema(description = "역할 이름") String name) {}
}
