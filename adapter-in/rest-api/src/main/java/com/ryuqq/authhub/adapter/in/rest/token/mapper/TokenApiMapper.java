package com.ryuqq.authhub.adapter.in.rest.token.mapper;

import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.LogoutApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.command.RefreshTokenApiRequest;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.MyContextApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeyApiResponse;
import com.ryuqq.authhub.adapter.in.rest.token.dto.response.PublicKeysApiResponse;
import com.ryuqq.authhub.application.token.dto.command.LoginCommand;
import com.ryuqq.authhub.application.token.dto.command.LogoutCommand;
import com.ryuqq.authhub.application.token.dto.command.RefreshTokenCommand;
import com.ryuqq.authhub.application.token.dto.response.MyContextResponse;
import com.ryuqq.authhub.application.token.dto.response.PublicKeysResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Token REST API ↔ Application Layer 변환 Mapper
 *
 * <p>REST API Layer와 Application Layer 간의 DTO 변환을 담당합니다.
 *
 * <p><strong>변환 방향:</strong>
 *
 * <ul>
 *   <li>API Request → Command (Controller → Application)
 * </ul>
 *
 * <p><strong>Response 변환:</strong>
 *
 * <ul>
 *   <li>Application Response → API Response 변환은 각 Response DTO의 from() 메서드 사용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TokenApiMapper {

    /**
     * LoginApiRequest → LoginCommand 변환
     *
     * @param request REST API 로그인 요청
     * @return Application Layer 로그인 명령
     */
    public LoginCommand toLoginCommand(LoginApiRequest request) {
        return new LoginCommand(request.identifier(), request.password());
    }

    /**
     * RefreshTokenApiRequest → RefreshTokenCommand 변환
     *
     * @param request REST API 토큰 갱신 요청
     * @return Application Layer 토큰 갱신 명령
     */
    public RefreshTokenCommand toRefreshTokenCommand(RefreshTokenApiRequest request) {
        return new RefreshTokenCommand(request.refreshToken());
    }

    /**
     * LogoutApiRequest → LogoutCommand 변환
     *
     * @param request REST API 로그아웃 요청
     * @return Application Layer 로그아웃 명령
     */
    public LogoutCommand toLogoutCommand(LogoutApiRequest request) {
        return new LogoutCommand(request.userId());
    }

    /**
     * PublicKeysResponse → PublicKeysApiResponse 변환
     *
     * @param response Application Layer 공개키 목록 응답
     * @return REST API 공개키 목록 응답 (RFC 7517 JWKS 형식)
     */
    public PublicKeysApiResponse toPublicKeysApiResponse(PublicKeysResponse response) {
        List<PublicKeyApiResponse> keys =
                response.keys().stream()
                        .map(
                                jwk ->
                                        new PublicKeyApiResponse(
                                                jwk.kid(), jwk.kty(), jwk.use(), jwk.alg(), jwk.n(),
                                                jwk.e()))
                        .toList();
        return new PublicKeysApiResponse(keys);
    }

    /**
     * MyContextResponse → MyContextApiResponse 변환
     *
     * @param response Application Layer 내 컨텍스트 응답
     * @return REST API 내 컨텍스트 응답
     */
    public MyContextApiResponse toMyContextApiResponse(MyContextResponse response) {
        MyContextApiResponse.TenantInfo tenant =
                new MyContextApiResponse.TenantInfo(response.tenantId(), response.tenantName());

        MyContextApiResponse.OrganizationInfo organization =
                new MyContextApiResponse.OrganizationInfo(
                        response.organizationId(), response.organizationName());

        List<MyContextApiResponse.RoleInfo> roles =
                response.roles().stream()
                        .map(role -> new MyContextApiResponse.RoleInfo(role.id(), role.name()))
                        .toList();

        return new MyContextApiResponse(
                response.userId(),
                response.email(),
                response.name(),
                tenant,
                organization,
                roles,
                response.permissions());
    }
}
