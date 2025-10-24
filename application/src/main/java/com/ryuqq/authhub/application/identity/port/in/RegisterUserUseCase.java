package com.ryuqq.authhub.application.identity.port.in;

/**
 * RegisterUser UseCase - 사용자 등록 유스케이스 Port Interface.
 *
 * <p>신규 사용자를 등록하고 User, UserCredential, UserProfile Aggregate를 생성하는 Command UseCase입니다.
 * Command/Response는 UseCase 내부 Record로 정의되며, 외부 클래스로 분리하지 않습니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Command/Response는 UseCase 내부 Record로 정의</li>
 *   <li>✅ Lombok 금지 - Plain Java Record 사용</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드/Record에 @author, @since 포함</li>
 *   <li>✅ 구현체는 Service Layer에서 @Transactional 적용</li>
 *   <li>✅ @Transactional 내 외부 API 호출 금지</li>
 * </ul>
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
 * <p><strong>Race Condition 주의사항:</strong></p>
 * <ul>
 *   <li>중복 확인과 생성 사이에 동시 요청이 발생할 수 있음</li>
 *   <li>Database Unique Constraint로 최종 방어</li>
 *   <li>중복 에러 시 적절한 예외 변환 필요</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface RegisterUserUseCase {

    /**
     * 신규 사용자를 등록합니다.
     *
     * <p>User, UserCredential, UserProfile 3개의 Aggregate를 생성하고 저장합니다.
     * 모든 작업은 하나의 트랜잭션 내에서 수행됩니다.</p>
     *
     * @param command 사용자 등록 요청 정보 (credentialType, identifier, password, nickname)
     * @return Response 생성된 사용자 정보 (userId, credentialId)
     * @throws com.ryuqq.authhub.domain.identity.user.exception.DuplicateIdentifierException identifier가 이미 존재하는 경우
     * @throws com.ryuqq.authhub.domain.identity.user.exception.DuplicateNicknameException nickname이 이미 존재하는 경우
     * @throws IllegalArgumentException 입력값이 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Response register(Command command);

    /**
     * 사용자 등록 Command Record.
     *
     * <p>사용자 등록에 필요한 모든 정보를 담고 있습니다.
     * Record의 Compact Constructor에서 입력값 검증을 수행합니다.</p>
     *
     * @param credentialType 인증 타입 (EMAIL, PHONE, USERNAME 등) - null 불가, 공백 불가
     * @param identifier 식별자 (이메일, 전화번호, 사용자명) - null 불가, 공백 불가, 중복 불가
     * @param password 평문 비밀번호 - null 불가, 공백 불가, 최소 8자 이상
     * @param nickname 닉네임 - null 불가, 공백 불가, 중복 불가, 2~20자
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Command(
            String credentialType,
            String identifier,
            String password,
            String nickname
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 형식이 잘못된 경우
         */
        public Command {
            if (credentialType == null || credentialType.isBlank()) {
                throw new IllegalArgumentException("credentialType cannot be null or blank");
            }

            if (identifier == null || identifier.isBlank()) {
                throw new IllegalArgumentException("identifier cannot be null or blank");
            }

            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("password cannot be null or blank");
            }

            if (password.length() < 8) {
                throw new IllegalArgumentException("password must be at least 8 characters");
            }

            if (nickname == null || nickname.isBlank()) {
                throw new IllegalArgumentException("nickname cannot be null or blank");
            }

            if (nickname.length() < 2 || nickname.length() > 20) {
                throw new IllegalArgumentException("nickname must be between 2 and 20 characters");
            }
        }
    }

    /**
     * 사용자 등록 Response Record.
     *
     * <p>사용자 등록 성공 시 반환되는 정보입니다.
     * User와 UserCredential의 ID를 포함합니다.</p>
     *
     * @param userId 생성된 User의 ID (UUID)
     * @param credentialId 생성된 UserCredential의 ID (UUID)
     * @author AuthHub Team
     * @since 1.0.0
     */
    record Response(
            String userId,
            String credentialId
    ) {
        /**
         * Compact Constructor - 입력값 검증.
         *
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 공백인 경우
         */
        public Response {
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("userId cannot be null or blank");
            }

            if (credentialId == null || credentialId.isBlank()) {
                throw new IllegalArgumentException("credentialId cannot be null or blank");
            }
        }
    }
}
