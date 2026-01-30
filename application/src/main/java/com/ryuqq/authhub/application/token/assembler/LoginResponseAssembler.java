package com.ryuqq.authhub.application.token.assembler;

import com.ryuqq.authhub.application.token.dto.response.LoginResponse;
import com.ryuqq.authhub.application.token.dto.response.TokenResponse;
import com.ryuqq.authhub.domain.user.id.UserId;
import org.springframework.stereotype.Component;

/**
 * LoginResponseAssembler - 로그인 응답 조립기
 *
 * <p>User 또는 UserId와 TokenResponse를 조합하여 LoginResponse를 생성합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Domain → DTO 변환만 담당
 *   <li>비즈니스 로직 포함 금지
 *   <li>외부 호출 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class LoginResponseAssembler {

    /**
     * UserId와 TokenResponse로부터 LoginResponse 조립
     *
     * @param userId 로그인한 사용자 ID
     * @param tokenResponse 발급된 토큰 정보
     * @return 조립된 LoginResponse
     */
    public LoginResponse toLoginResponse(UserId userId, TokenResponse tokenResponse) {
        return new LoginResponse(
                userId.value(),
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                tokenResponse.accessTokenExpiresIn(),
                tokenResponse.tokenType());
    }
}
