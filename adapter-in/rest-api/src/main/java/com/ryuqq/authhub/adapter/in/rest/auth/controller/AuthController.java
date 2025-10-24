package com.ryuqq.authhub.adapter.in.rest.auth.controller;

import com.ryuqq.authhub.adapter.in.rest.auth.dto.request.LoginApiRequest;
import com.ryuqq.authhub.adapter.in.rest.auth.dto.response.LoginApiResponse;
import com.ryuqq.authhub.adapter.in.rest.auth.mapper.AuthApiMapper;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth Controller - 인증 관련 REST API Controller.
 *
 * <p>클라이언트의 인증 요청을 처리하는 REST API 엔드포인트를 제공합니다.
 * 로그인, 토큰 갱신 등의 인증 관련 작업을 처리합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Mapper 사용 (Assembler 아님) - API DTO ↔ UseCase Command/Response 변환</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 문서화</li>
 *   <li>✅ @Valid 어노테이션으로 요청 DTO 검증</li>
 *   <li>✅ Bounded Context 구조 준수 - com.ryuqq.authhub.adapter.in.rest.auth.*</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>API 요청 DTO 수신 및 Validation</li>
 *   <li>Mapper를 통해 API DTO → UseCase Command 변환</li>
 *   <li>UseCase 호출 (비즈니스 로직 실행)</li>
 *   <li>Mapper를 통해 UseCase Response → API DTO 변환</li>
 *   <li>HTTP 응답 반환</li>
 * </ol>
 *
 * <p><strong>엔드포인트:</strong></p>
 * <ul>
 *   <li>POST /api/v1/auth/login - 로그인 및 JWT 토큰 발급</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final AuthApiMapper authApiMapper;

    /**
     * AuthController 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param loginUseCase 로그인 UseCase
     * @param authApiMapper Auth API Mapper
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuthController(
            final LoginUseCase loginUseCase,
            final AuthApiMapper authApiMapper
    ) {
        this.loginUseCase = loginUseCase;
        this.authApiMapper = authApiMapper;
    }

    /**
     * 로그인을 수행하고 JWT 토큰을 발급합니다.
     *
     * <p>클라이언트로부터 인증 정보를 받아 사용자를 인증하고,
     * Access Token과 Refresh Token을 발급하여 반환합니다.</p>
     *
     * <p><strong>요청 예시:</strong></p>
     * <pre>
     * POST /api/v1/auth/login
     * Content-Type: application/json
     *
     * {
     *   "credentialType": "EMAIL",
     *   "identifier": "user@example.com",
     *   "password": "securePassword123!",
     *   "platform": "WEB"
     * }
     * </pre>
     *
     * <p><strong>응답 예시 (성공):</strong></p>
     * <pre>
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     *
     * {
     *   "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "expiresIn": 900
     * }
     * </pre>
     *
     * <p><strong>에러 응답:</strong></p>
     * <ul>
     *   <li>400 Bad Request: Validation 실패 (필수 필드 누락, 형식 오류)</li>
     *   <li>401 Unauthorized: 인증 실패 (비밀번호 불일치, 존재하지 않는 사용자)</li>
     *   <li>403 Forbidden: 사용자 상태 비활성화 (SUSPENDED, DELETED)</li>
     * </ul>
     *
     * @param request 로그인 요청 DTO (credentialType, identifier, password, platform)
     * @return ResponseEntity&lt;LoginApiResponse&gt; JWT 토큰 정보
     * @throws com.ryuqq.authhub.domain.auth.credential.exception.CredentialNotFoundException 인증 정보가 존재하지 않는 경우
     * @throws com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException 비밀번호가 일치하지 않는 경우
     * @throws com.ryuqq.authhub.domain.auth.user.exception.InvalidUserStatusException 사용자가 비활성화 상태인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @PostMapping("/login")
    public ResponseEntity<LoginApiResponse> login(
            @Valid @RequestBody final LoginApiRequest request
    ) {
        // 1. API DTO → UseCase Command 변환
        final LoginUseCase.Command command = authApiMapper.toCommand(request);

        // 2. UseCase 호출 (비즈니스 로직 실행)
        final LoginUseCase.Response response = loginUseCase.login(command);

        // 3. UseCase Response → API DTO 변환
        final LoginApiResponse apiResponse = authApiMapper.toApiResponse(response);

        // 4. HTTP 200 OK 응답 반환
        return ResponseEntity.ok(apiResponse);
    }
}
