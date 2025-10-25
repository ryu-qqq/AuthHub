package com.ryuqq.authhub.adapter.in.rest.identity.user.mapper;

import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.request.RegisterUserApiRequest;
import com.ryuqq.authhub.adapter.in.rest.identity.user.dto.response.RegisterUserApiResponse;
import com.ryuqq.authhub.application.identity.port.in.RegisterUserUseCase;
import org.springframework.stereotype.Component;

/**
 * User API Mapper - API DTO와 UseCase Command/Response 변환 Mapper.
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
 *   <li>toCommand(): RegisterUserApiRequest → RegisterUserUseCase.Command</li>
 *   <li>toApiResponse(): RegisterUserUseCase.Response → RegisterUserApiResponse</li>
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
public class UserApiMapper {

    /**
     * RegisterUserApiRequest를 RegisterUserUseCase.Command로 변환합니다.
     *
     * <p>API Layer의 요청 DTO를 Application Layer의 Command Record로 변환합니다.
     * 필드명이 동일하므로 단순 매핑만 수행합니다.</p>
     *
     * <p><strong>Record 패턴:</strong></p>
     * <ul>
     *   <li>Record는 자동으로 null-safe 생성자를 제공하므로 명시적 null 체크 불필요</li>
     *   <li>Getter 메서드명이 필드명과 동일 (getXxx() → xxx())</li>
     * </ul>
     *
     * @param request 사용자 등록 API 요청 DTO (Record)
     * @return RegisterUserUseCase.Command
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RegisterUserUseCase.Command toCommand(final RegisterUserApiRequest request) {
        return new RegisterUserUseCase.Command(
                request.credentialType(),
                request.identifier(),
                request.password(),
                request.nickname()
        );
    }

    /**
     * RegisterUserUseCase.Response를 RegisterUserApiResponse로 변환합니다.
     *
     * <p>Application Layer의 Response Record를 API Layer의 응답 DTO로 변환합니다.
     * 필드명이 동일하므로 단순 매핑만 수행합니다.</p>
     *
     * <p><strong>Record 패턴:</strong></p>
     * <ul>
     *   <li>Record는 자동으로 null-safe 생성자를 제공하므로 명시적 null 체크 불필요</li>
     *   <li>Record끼리 변환 시 필드명과 타입이 동일하면 직접 매핑</li>
     * </ul>
     *
     * @param response 사용자 등록 UseCase 응답 (Record)
     * @return RegisterUserApiResponse (Record)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RegisterUserApiResponse toApiResponse(final RegisterUserUseCase.Response response) {
        return new RegisterUserApiResponse(
                response.userId(),
                response.credentialId()
        );
    }
}
