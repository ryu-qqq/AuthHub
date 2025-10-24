package com.ryuqq.authhub.adapter.in.rest.auth.mapper;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.request.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import org.springframework.stereotype.Component;

/**
 * Auth API Mapper - API DTO와 UseCase Command/Response 변환 Mapper.
 *
 * <p>REST API Layer의 DTO와 Application Layer의 Command/Response를 변환하는 역할을 수행합니다.
 * Adapter-In-Rest Layer에서는 Assembler가 아닌 Mapper를 사용합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Mapper 사용 (Assembler 아님) - Adapter-In-Rest Layer 규칙</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 문서화</li>
 *   <li>✅ API DTO ↔ UseCase Command/Response 변환만 담당</li>
 *   <li>✅ 비즈니스 로직 포함하지 않음 (단순 매핑만)</li>
 * </ul>
 *
 * <p><strong>변환 로직:</strong></p>
 * <ul>
 *   <li>toCommand(): LoginApiRequest → LoginUseCase.Command</li>
 *   <li>toApiResponse(): LoginUseCase.Response → LoginApiResponse</li>
 * </ul>
 *
 * <p><strong>Mapper vs Assembler 구분:</strong></p>
 * <ul>
 *   <li>Mapper: REST API DTO ↔ UseCase Command/Response (Adapter-In-Rest Layer)</li>
 *   <li>Assembler: UseCase Response ↔ Domain Aggregate (Application Layer)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class AuthApiMapper {

    /**
     * LoginApiRequest를 LoginUseCase.Command로 변환합니다.
     *
     * <p>API Layer의 요청 DTO를 Application Layer의 Command Record로 변환합니다.
     * 필드명이 동일하므로 단순 매핑만 수행합니다.</p>
     *
     * @param request 로그인 API 요청 DTO
     * @return LoginUseCase.Command
     * @throws NullPointerException request가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginUseCase.Command toCommand(final LoginApiRequest request) {
        if (request == null) {
            throw new NullPointerException("LoginApiRequest cannot be null");
        }

        return new LoginUseCase.Command(
                request.getCredentialType(),
                request.getIdentifier(),
                request.getPassword(),
                request.getPlatform()
        );
    }

    /**
     * LoginUseCase.Response를 LoginApiResponse로 변환합니다.
     *
     * <p>Application Layer의 Response Record를 API Layer의 응답 DTO로 변환합니다.
     * 필드명이 동일하므로 단순 매핑만 수행합니다.</p>
     *
     * @param response 로그인 UseCase 응답
     * @return LoginApiResponse
     * @throws NullPointerException response가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginApiResponse toApiResponse(final LoginUseCase.Response response) {
        if (response == null) {
            throw new NullPointerException("LoginUseCase.Response cannot be null");
        }

        return new LoginApiResponse(
                response.accessToken(),
                response.refreshToken(),
                response.tokenType(),
                response.expiresIn()
        );
    }
}
