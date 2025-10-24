package com.ryuqq.authhub.application.auth.service.command;

import com.ryuqq.authhub.application.auth.assembler.TokenAssembler;
import com.ryuqq.authhub.application.auth.config.JwtProperties;
import com.ryuqq.authhub.application.auth.port.in.LoginUseCase;
import com.ryuqq.authhub.application.auth.port.out.GenerateTokenPort;
import com.ryuqq.authhub.application.auth.port.out.LoadCredentialByIdentifierPort;
import com.ryuqq.authhub.application.auth.port.out.LoadUserPort;
import com.ryuqq.authhub.application.auth.port.out.SaveRefreshTokenPort;
import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;
import com.ryuqq.authhub.domain.auth.credential.PasswordHash;
import com.ryuqq.authhub.domain.auth.credential.UserCredential;
import com.ryuqq.authhub.domain.auth.credential.exception.CredentialNotFoundException;
import com.ryuqq.authhub.domain.auth.credential.exception.InvalidCredentialException;
import com.ryuqq.authhub.domain.auth.token.Token;
import com.ryuqq.authhub.domain.auth.token.TokenType;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.exception.InvalidUserStatusException;
import com.ryuqq.authhub.domain.auth.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Login Service - LoginUseCase 구현체.
 *
 * <p>사용자 인증을 수행하고 JWT 토큰을 발급하는 Command Service입니다.
 * 로그인 메서드에 {@code @Transactional}을 적용하여 트랜잭션 경계를 명확히 정의합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>Credential 조회 (credentialType + identifier)</li>
 *   <li>비밀번호 검증</li>
 *   <li>User 조회 및 상태 확인 (ACTIVE 여부)</li>
 *   <li>Access Token + Refresh Token 생성 (JWT 서명 - 내부 계산)</li>
 *   <li>Refresh Token을 Redis에 저장 (내부 I/O)</li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ @Transactional 내 외부 API 호출 금지 (S3, HTTP, SQS 등)</li>
 *   <li>✅ Redis/MySQL은 내부 시스템으로 간주 (트랜잭션 내 호출 허용)</li>
 *   <li>✅ JWT 생성은 메모리 계산 작업 (네트워크 I/O 없음)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Assembler 패턴 사용 - Domain ↔ Response 변환</li>
 * </ul>
 *
 * <p><strong>트랜잭션 경계:</strong></p>
 * <ul>
 *   <li>DB 조회: LoadCredentialByIdentifierPort, LoadUserPort (MySQL)</li>
 *   <li>Token 생성: GenerateTokenPort (JWT 서명 - 메모리 계산)</li>
 *   <li>Redis 저장: SaveRefreshTokenPort (Redis - 내부 I/O)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class LoginService implements LoginUseCase {

    // 인증 관련 의존성
    private final LoadCredentialByIdentifierPort loadCredentialByIdentifierPort;
    private final PasswordHash.PasswordEncoder passwordEncoder;

    // 사용자 조회 의존성
    private final LoadUserPort loadUserPort;

    // 토큰 관리 의존성
    private final GenerateTokenPort generateTokenPort;
    private final SaveRefreshTokenPort saveRefreshTokenPort;

    // 응답 변환 의존성
    private final TokenAssembler tokenAssembler;

    // ✅ JWT 토큰 유효 기간 설정 (외부 설정 파일에서 주입)
    private final JwtProperties jwtProperties;

    /**
     * LoginService 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param loadCredentialByIdentifierPort Credential 조회 Port
     * @param passwordEncoder 비밀번호 검증 Encoder
     * @param loadUserPort User 조회 Port
     * @param generateTokenPort Token 생성 Port
     * @param saveRefreshTokenPort Refresh Token 저장 Port
     * @param tokenAssembler Token Assembler
     * @param jwtProperties JWT 토큰 유효 기간 설정
     * @author AuthHub Team
     * @since 1.0.0
     */
    public LoginService(
            final LoadCredentialByIdentifierPort loadCredentialByIdentifierPort,
            final PasswordHash.PasswordEncoder passwordEncoder,
            final LoadUserPort loadUserPort,
            final GenerateTokenPort generateTokenPort,
            final SaveRefreshTokenPort saveRefreshTokenPort,
            final TokenAssembler tokenAssembler,
            final JwtProperties jwtProperties
    ) {
        this.loadCredentialByIdentifierPort = loadCredentialByIdentifierPort;
        this.passwordEncoder = passwordEncoder;
        this.loadUserPort = loadUserPort;
        this.generateTokenPort = generateTokenPort;
        this.saveRefreshTokenPort = saveRefreshTokenPort;
        this.tokenAssembler = tokenAssembler;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 로그인을 수행하고 JWT 토큰을 발급합니다.
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>✅ Credential 조회 (MySQL)</li>
     *   <li>✅ User 조회 (MySQL)</li>
     *   <li>✅ Token 생성 (JWT 서명 - 메모리 계산, 외부 API 아님)</li>
     *   <li>✅ Refresh Token 저장 (Redis - 내부 시스템)</li>
     *   <li>❌ 외부 API 호출 없음 (S3, HTTP, SQS 등)</li>
     * </ul>
     *
     * @param command 로그인 요청 정보
     * @return Response JWT 토큰 정보
     * @throws CredentialNotFoundException 인증 정보가 존재하지 않는 경우
     * @throws InvalidCredentialException 비밀번호가 일치하지 않거나 잘못된 CredentialType인 경우
     * @throws UserNotFoundException 사용자가 존재하지 않는 경우
     * @throws InvalidUserStatusException 사용자가 ACTIVE 상태가 아닌 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    @Transactional
    public Response login(final Command command) {
        // ✅ 1. Credential 조회 및 검증
        final UserCredential credential = loadAndValidateCredential(command);

        // ✅ 2. User 조회 및 상태 확인
        final User user = loadAndValidateUser(credential);

        // ✅ 3. Access Token 생성 (JWT 서명 - 내부 계산 작업)
        final Token accessToken = generateTokenPort.generate(
                user.getId(),
                TokenType.ACCESS,
                jwtProperties.accessTokenValidity()
        );

        // ✅ 4. Refresh Token 생성 (JWT 서명 - 내부 계산 작업)
        final Token refreshToken = generateTokenPort.generate(
                user.getId(),
                TokenType.REFRESH,
                jwtProperties.refreshTokenValidity()
        );

        // ✅ 5. Refresh Token을 Redis에 저장 (내부 I/O)
        saveRefreshTokenPort.save(refreshToken);

        // ✅ 6. Assembler로 Domain → Response 변환
        return tokenAssembler.toLoginResponse(accessToken, refreshToken);
    }

    /**
     * Credential을 조회하고 비밀번호를 검증합니다.
     *
     * @param command 로그인 Command
     * @return 검증된 UserCredential
     * @throws CredentialNotFoundException 인증 정보가 존재하지 않는 경우
     * @throws InvalidCredentialException 비밀번호가 일치하지 않거나 credentialType이 유효하지 않은 경우
     */
    private UserCredential loadAndValidateCredential(final Command command) {
        // Command → Domain Value Object 변환
        final CredentialType credentialType;
        try {
            credentialType = CredentialType.valueOf(command.credentialType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidCredentialException("Invalid credentialType: " + command.credentialType());
        }
        final Identifier identifier = Identifier.of(credentialType, command.identifier());

        // Credential 조회
        final UserCredential credential = loadCredentialByIdentifierPort
                .loadByIdentifier(credentialType, identifier)
                .orElseThrow(() -> new CredentialNotFoundException(
                        "Credential not found for type: " + command.credentialType() +
                                ", identifier: " + command.identifier()
                ));

        if (!credential.matchesPassword(command.password(), passwordEncoder)) {
            throw new InvalidCredentialException("Invalid password for identifier: " + command.identifier());
        }

        return credential;
    }

    /**
     * User를 조회하고 상태를 검증합니다.
     *
     * @param credential 검증된 UserCredential
     * @return 검증된 User
     * @throws UserNotFoundException User가 존재하지 않는 경우
     * @throws InvalidUserStatusException User가 ACTIVE 상태가 아닌 경우
     */
    private User loadAndValidateUser(final UserCredential credential) {
        final User user = loadUserPort
                .load(credential.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found for userId: " + credential.getUserId(),
                        credential.getUserId()
                ));

        if (!user.canUseSystem()) {
            throw new InvalidUserStatusException(
                    "User is not in ACTIVE status. Current status: " + user.getStatus(),
                    user.getId(),
                    user.getStatus()
            );
        }

        return user;
    }
}
