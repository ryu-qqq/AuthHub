package com.ryuqq.authhub.application.identity.service.command;

import com.ryuqq.authhub.application.auth.port.out.SaveUserPort;
import com.ryuqq.authhub.application.identity.port.in.RegisterUserUseCase;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateIdentifierPort;
import com.ryuqq.authhub.application.identity.port.out.CheckDuplicateNicknamePort;
import com.ryuqq.authhub.application.identity.port.out.SaveUserCredentialPort;
import com.ryuqq.authhub.application.identity.port.out.SaveUserProfilePort;
import com.ryuqq.authhub.domain.auth.credential.CredentialId;
import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;
import com.ryuqq.authhub.domain.auth.credential.PasswordHash;
import com.ryuqq.authhub.domain.auth.credential.UserCredential;
import com.ryuqq.authhub.domain.auth.user.User;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.profile.UserProfile;
import com.ryuqq.authhub.domain.identity.profile.vo.Nickname;
import com.ryuqq.authhub.domain.identity.profile.vo.UserProfileId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * RegisterUser Service - RegisterUserUseCase 구현체.
 *
 * <p>신규 사용자를 등록하고 User, UserCredential, UserProfile Aggregate를 생성하는 Command Service입니다.
 * 등록 메서드에 {@code @Transactional}을 적용하여 트랜잭션 경계를 명확히 정의합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li>중복 확인 (identifier, nickname)</li>
 *   <li>User Aggregate 생성</li>
 *   <li>UserCredential Aggregate 생성 (비밀번호 암호화 포함)</li>
 *   <li>UserProfile Aggregate 생성</li>
 *   <li>3개 Aggregate를 각 Repository에 저장</li>
 *   <li>생성된 userId와 credentialId 반환</li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ @Transactional 내 외부 API 호출 금지</li>
 *   <li>✅ MySQL/Redis는 내부 시스템으로 간주 (트랜잭션 내 호출 허용)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Race Condition 주의 - 중복 확인 후 생성 사이 동시 요청 가능</li>
 *   <li>✅ Database Unique Constraint로 최종 방어</li>
 * </ul>
 *
 * <p><strong>트랜잭션 경계:</strong></p>
 * <ul>
 *   <li>중복 확인: CheckDuplicateIdentifierPort, CheckDuplicateNicknamePort (MySQL)</li>
 *   <li>비밀번호 암호화: PasswordHash.encode() (메모리 계산)</li>
 *   <li>저장: SaveUserPort, SaveUserCredentialPort, SaveUserProfilePort (MySQL)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class RegisterUserService implements RegisterUserUseCase {

    // 중복 확인 의존성
    private final CheckDuplicateIdentifierPort checkDuplicateIdentifierPort;
    private final CheckDuplicateNicknamePort checkDuplicateNicknamePort;

    // 저장 Port 의존성
    private final SaveUserPort saveUserPort;
    private final SaveUserCredentialPort saveUserCredentialPort;
    private final SaveUserProfilePort saveUserProfilePort;

    // 비밀번호 암호화 의존성
    private final PasswordHash.PasswordEncoder passwordEncoder;

    /**
     * RegisterUserService 생성자.
     * Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     *
     * @param checkDuplicateIdentifierPort Identifier 중복 확인 Port
     * @param checkDuplicateNicknamePort Nickname 중복 확인 Port
     * @param saveUserPort User 저장 Port
     * @param saveUserCredentialPort UserCredential 저장 Port
     * @param saveUserProfilePort UserProfile 저장 Port
     * @param passwordEncoder 비밀번호 암호화 Encoder
     * @author AuthHub Team
     * @since 1.0.0
     */
    public RegisterUserService(
            final CheckDuplicateIdentifierPort checkDuplicateIdentifierPort,
            final CheckDuplicateNicknamePort checkDuplicateNicknamePort,
            final SaveUserPort saveUserPort,
            final SaveUserCredentialPort saveUserCredentialPort,
            final SaveUserProfilePort saveUserProfilePort,
            final PasswordHash.PasswordEncoder passwordEncoder
    ) {
        this.checkDuplicateIdentifierPort = checkDuplicateIdentifierPort;
        this.checkDuplicateNicknamePort = checkDuplicateNicknamePort;
        this.saveUserPort = saveUserPort;
        this.saveUserCredentialPort = saveUserCredentialPort;
        this.saveUserProfilePort = saveUserProfilePort;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 신규 사용자를 등록합니다.
     *
     * <p><strong>트랜잭션 범위:</strong></p>
     * <ul>
     *   <li>✅ Identifier 중복 확인 (MySQL)</li>
     *   <li>✅ Nickname 중복 확인 (MySQL)</li>
     *   <li>✅ User Aggregate 생성 및 저장 (MySQL)</li>
     *   <li>✅ UserCredential Aggregate 생성 및 저장 (MySQL)</li>
     *   <li>✅ UserProfile Aggregate 생성 및 저장 (MySQL)</li>
     *   <li>❌ 외부 API 호출 없음 (S3, HTTP, SQS 등)</li>
     * </ul>
     *
     * @param command 사용자 등록 요청 정보
     * @return Response 생성된 사용자 정보 (userId, credentialId)
     * @throws DuplicateIdentifierException identifier가 이미 존재하는 경우
     * @throws DuplicateNicknameException nickname이 이미 존재하는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    @Transactional
    public Response register(final Command command) {
        // ✅ 1. 중복 확인 (identifier, nickname)
        validateNoDuplicates(command);

        // ✅ 2. User Aggregate 생성 및 저장
        final UserId userId = UserId.newId();
        final User user = User.create(userId);
        saveUserPort.save(user);

        // ✅ 3. UserCredential Aggregate 생성 및 저장
        final UserCredential credential = createCredential(command, userId);
        final UserCredential savedCredential = saveUserCredentialPort.save(credential);

        // ✅ 4. UserProfile Aggregate 생성 및 저장
        final UserProfile profile = createProfile(command, userId);
        saveUserProfilePort.save(profile);

        // ✅ 5. Response 생성 및 반환
        return new Response(
                userId.value().toString(),
                savedCredential.getId().value().toString()
        );
    }

    /**
     * Identifier와 Nickname 중복 여부를 확인합니다.
     *
     * @param command 사용자 등록 Command
     * @throws DuplicateIdentifierException identifier가 이미 존재하는 경우
     * @throws DuplicateNicknameException nickname이 이미 존재하는 경우
     */
    private void validateNoDuplicates(final Command command) {
        // Identifier 중복 확인
        if (checkDuplicateIdentifierPort.existsByIdentifier(
                command.credentialType(),
                command.identifier()
        )) {
            throw new DuplicateIdentifierException(
                    "Identifier already exists: " + command.identifier()
            );
        }

        // Nickname 중복 확인
        if (checkDuplicateNicknamePort.existsByNickname(command.nickname())) {
            throw new DuplicateNicknameException(
                    "Nickname already exists: " + command.nickname()
            );
        }
    }

    /**
     * UserCredential Aggregate를 생성합니다.
     *
     * @param command 사용자 등록 Command
     * @param userId 생성된 User의 ID
     * @return 생성된 UserCredential
     */
    private UserCredential createCredential(final Command command, final UserId userId) {
        final CredentialType credentialType = CredentialType.fromString(command.credentialType());
        final Identifier identifier = Identifier.of(credentialType, command.identifier());

        // 평문 비밀번호를 BCrypt로 해싱 (Infrastructure Layer의 PasswordEncoder 사용)
        final String encodedPassword = passwordEncoder.encode(command.password());
        final PasswordHash passwordHash = PasswordHash.from(encodedPassword);

        return UserCredential.create(
                CredentialId.newId(),
                userId,
                credentialType,
                identifier,
                passwordHash
        );
    }

    /**
     * UserProfile Aggregate를 생성합니다.
     *
     * @param command 사용자 등록 Command
     * @param userId 생성된 User의 ID
     * @return 생성된 UserProfile
     */
    private UserProfile createProfile(final Command command, final UserId userId) {
        return UserProfile.create(
                userId,
                new Nickname(command.nickname())
        );
    }

    /**
     * Identifier 중복 예외.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static class DuplicateIdentifierException extends RuntimeException {
        public DuplicateIdentifierException(final String message) {
            super(message);
        }
    }

    /**
     * Nickname 중복 예외.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static class DuplicateNicknameException extends RuntimeException {
        public DuplicateNicknameException(final String message) {
            super(message);
        }
    }
}
