package com.ryuqq.authhub.adapter.in.rest.identity.user.controller;

import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.request.RegisterUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.response.RegisterUserApiResponse;
import com.ryuqq.authhub.adapter.in.rest.identity.user.mapper.UserApiMapper;
import com.ryuqq.authhub.application.identity.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller - 사용자 관련 REST API Controller.
 *
 * <p>클라이언트의 사용자 관련 요청을 처리하는 REST API 엔드포인트를 제공합니다.
 * 사용자 등록, 프로필 조회, 비밀번호 변경 등의 작업을 처리합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Mapper 사용 (Assembler 아님) - API DTO ↔ UseCase Command/Response 변환</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 문서화</li>
 *   <li>✅ @Valid 어노테이션으로 요청 DTO 검증</li>
 *   <li>✅ Bounded Context 구조 준수 - com.ryuqq.authhub.adapter.in.rest.identity.user.*</li>
 * </ul>
 *
 * <p><strong>처리 흐름:</strong></p>
 * <ol>
 *   <li>API 요청 DTO 수신 및 Validation (Jakarta Validation)</li>
 *   <li>Mapper를 통해 API DTO → UseCase Command 변환</li>
 *   <li>UseCase 호출 (비즈니스 로직 실행)</li>
 *   <li>Mapper를 통해 UseCase Response → API DTO 변환</li>
 *   <li>HTTP 응답 반환 (상태 코드, 헤더, Body)</li>
 * </ol>
 *
 * <p><strong>엔드포인트:</strong></p>
 * <ul>
 *   <li>POST /api/v1/users/register - 사용자 등록 및 User/UserCredential/UserProfile 생성</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final UserApiMapper userApiMapper;

    /**
     * UserController 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param registerUserUseCase 사용자 등록 UseCase
     * @param userApiMapper User API Mapper
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserController(
            final RegisterUserUseCase registerUserUseCase,
            final UserApiMapper userApiMapper
    ) {
        this.registerUserUseCase = registerUserUseCase;
        this.userApiMapper = userApiMapper;
    }

    /**
     * 신규 사용자를 등록합니다.
     *
     * <p>클라이언트로부터 사용자 등록 정보를 받아 User, UserCredential, UserProfile을 생성합니다.
     * 등록 성공 시 생성된 userId와 credentialId를 반환합니다.</p>
     *
     * <p><strong>요청 예시:</strong></p>
     * <pre>
     * POST /api/v1/users/register
     * Content-Type: application/json
     *
     * {
     *   "credentialType": "EMAIL",
     *   "identifier": "user@example.com",
     *   "password": "securePassword123!",
     *   "nickname": "cooluser"
     * }
     * </pre>
     *
     * <p><strong>응답 예시 (성공):</strong></p>
     * <pre>
     * HTTP/1.1 201 Created
     * Content-Type: application/json
     *
     * {
     *   "userId": "550e8400-e29b-41d4-a716-446655440000",
     *   "credentialId": "660e8400-e29b-41d4-a716-446655440111"
     * }
     * </pre>
     *
     * <p><strong>에러 응답:</strong></p>
     * <ul>
     *   <li>400 Bad Request: Validation 실패 (필수 필드 누락, 형식 오류, 비밀번호 길이 부족)</li>
     *   <li>409 Conflict: 중복 에러 (identifier 또는 nickname이 이미 존재)</li>
     * </ul>
     *
     * <p><strong>비즈니스 로직:</strong></p>
     * <ol>
     *   <li>Identifier 중복 확인 (credentialType + identifier)</li>
     *   <li>Nickname 중복 확인</li>
     *   <li>User Aggregate 생성 및 저장</li>
     *   <li>UserCredential Aggregate 생성 및 저장 (비밀번호 BCrypt 암호화)</li>
     *   <li>UserProfile Aggregate 생성 및 저장</li>
     * </ol>
     *
     * @param request 사용자 등록 요청 DTO (credentialType, identifier, password, nickname)
     * @return ResponseEntity&lt;RegisterUserApiResponse&gt; 생성된 사용자 정보 (userId, credentialId)
     * @throws com.ryuqq.authhub.application.identity.exception.DuplicateIdentifierException identifier가 이미 존재하는 경우
     * @throws com.ryuqq.authhub.application.identity.exception.DuplicateNicknameException nickname이 이미 존재하는 경우
     * @throws IllegalArgumentException 입력값이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserApiResponse> register(
            @Valid @RequestBody final RegisterUserApiRequest request
    ) {
        // 1. API DTO → UseCase Command 변환
        final RegisterUserUseCase.Command command = userApiMapper.toCommand(request);

        // 2. UseCase 호출 (비즈니스 로직 실행)
        final RegisterUserUseCase.Response response = registerUserUseCase.register(command);

        // 3. UseCase Response → API DTO 변환
        final RegisterUserApiResponse apiResponse = userApiMapper.toApiResponse(response);

        // 4. HTTP 201 Created 응답 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }
}
