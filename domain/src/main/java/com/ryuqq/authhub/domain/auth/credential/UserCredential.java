package com.ryuqq.authhub.domain.auth.credential;

import com.ryuqq.authhub.domain.auth.user.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * UserCredential Aggregate Root.
 *
 * <p>사용자 인증 정보 도메인의 Aggregate Root로서, 인증에 필요한 핵심 정보와 행위를 캡슐화합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>인증 정보 식별자(CredentialId) 관리</li>
 *   <li>사용자 참조(UserId Value Object) 관리</li>
 *   <li>인증 타입(CredentialType) 및 식별자(Identifier) 관리</li>
 *   <li>비밀번호(PasswordHash) 관리 및 검증</li>
 *   <li>검증 상태(VerifiedAt) 추적</li>
 *   <li>생성/수정 시각 관리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ Long FK 전략 - UserId를 Value Object로 참조 (JPA 관계 어노테이션 없음)</li>
 *   <li>✅ 불변성 보장 - 모든 필드 final, 상태 변경 시 새 인스턴스 반환</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ Framework 의존성 금지 - Pure Java만 사용</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>인증 정보는 반드시 고유한 CredentialId를 가져야 함</li>
 *   <li>UserId는 Value Object로만 참조 (Long FK 금지)</li>
 *   <li>Identifier는 CredentialType에 맞는 형식이어야 함</li>
 *   <li>비밀번호는 항상 해시된 상태로만 저장</li>
 *   <li>검증되지 않은 인증 정보는 로그인에 사용할 수 없음 (선택적 정책)</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class UserCredential {

    private final CredentialId id;
    private final UserId userId;
    private final CredentialType type;
    private final Identifier identifier;
    private final PasswordHash passwordHash;
    private final VerifiedAt verifiedAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * UserCredential 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 인증 정보 식별자 (null 불가)
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @param type 인증 타입 (null 불가)
     * @param identifier 식별자 값 (null 불가)
     * @param passwordHash 비밀번호 해시 (null 불가)
     * @param verifiedAt 검증 완료 시각 (null 불가, 미검증 시 {@code VerifiedAt.notVerified()} 사용)
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     */
    private UserCredential(
            final CredentialId id,
            final UserId userId,
            final CredentialType type,
            final Identifier identifier,
            final PasswordHash passwordHash,
            final VerifiedAt verifiedAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "CredentialId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.type = Objects.requireNonNull(type, "CredentialType cannot be null");
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "PasswordHash cannot be null");
        this.verifiedAt = Objects.requireNonNull(verifiedAt, "VerifiedAt cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt cannot be null");

        // 도메인 불변식: Identifier는 CredentialType에 맞는 형식이어야 함
        if (!identifier.isValidFor(type)) {
            throw new IllegalArgumentException(
                    "Identifier is not valid for credential type: " + type
            );
        }
    }

    /**
     * 새로운 UserCredential을 생성합니다.
     * 기본적으로 미검증 상태로 생성됩니다.
     *
     * @param id 인증 정보 식별자 (null 불가)
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @param type 인증 타입 (null 불가)
     * @param identifier 식별자 값 (null 불가)
     * @param passwordHash 비밀번호 해시 (null 불가)
     * @return 새로 생성된 UserCredential 인스턴스 (미검증 상태)
     * @throws NullPointerException 필수 파라미터가 null인 경우
     * @throws IllegalArgumentException Identifier가 CredentialType에 맞지 않는 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserCredential create(
            final CredentialId id,
            final UserId userId,
            final CredentialType type,
            final Identifier identifier,
            final PasswordHash passwordHash
    ) {
        Instant now = Instant.now();
        return new UserCredential(
                id,
                userId,
                type,
                identifier,
                passwordHash,
                VerifiedAt.notVerified(),
                now,
                now
        );
    }

    /**
     * 기존 데이터로부터 UserCredential을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 인증 정보 식별자 (null 불가)
     * @param userId 사용자 식별자 Value Object (null 불가)
     * @param type 인증 타입 (null 불가)
     * @param identifier 식별자 값 (null 불가)
     * @param passwordHash 비밀번호 해시 (null 불가)
     * @param verifiedAt 검증 완료 시각
     * @param createdAt 생성 시각 (null 불가)
     * @param updatedAt 수정 시각 (null 불가)
     * @return 재구성된 UserCredential 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static UserCredential reconstruct(
            final CredentialId id,
            final UserId userId,
            final CredentialType type,
            final Identifier identifier,
            final PasswordHash passwordHash,
            final VerifiedAt verifiedAt,
            final Instant createdAt,
            final Instant updatedAt
    ) {
        return new UserCredential(
                id,
                userId,
                type,
                identifier,
                passwordHash,
                verifiedAt,
                createdAt,
                updatedAt
        );
    }

    /**
     * 인증 정보 식별자를 반환합니다.
     *
     * @return CredentialId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialId getId() {
        return this.id;
    }

    /**
     * 사용자 식별자를 반환합니다.
     *
     * @return UserId Value Object
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getUserId() {
        return this.userId;
    }

    /**
     * 인증 타입을 반환합니다.
     *
     * @return CredentialType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public CredentialType getType() {
        return this.type;
    }

    /**
     * 식별자 값을 반환합니다.
     *
     * @return Identifier 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    /**
     * 비밀번호 해시를 반환합니다.
     *
     * @return PasswordHash 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public PasswordHash getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * 검증 완료 시각을 반환합니다.
     *
     * @return VerifiedAt 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public VerifiedAt getVerifiedAt() {
        return this.verifiedAt;
    }

    /**
     * 생성 시각을 반환합니다.
     *
     * @return 생성 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 수정 시각을 반환합니다.
     *
     * @return 수정 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * 이메일 기반 인증인지 확인합니다.
     * Law of Demeter 준수 - type.isEmail()을 직접 호출하지 않고 UserCredential에서 제공
     *
     * @return EMAIL 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isEmailCredential() {
        return this.type.isEmail();
    }

    /**
     * 전화번호 기반 인증인지 확인합니다.
     *
     * @return PHONE 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isPhoneCredential() {
        return this.type.isPhone();
    }

    /**
     * 사용자명 기반 인증인지 확인합니다.
     *
     * @return USERNAME 타입이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUsernameCredential() {
        return this.type.isUsername();
    }

    /**
     * 인증 정보가 검증되었는지 확인합니다.
     * Law of Demeter 준수 - verifiedAt.isVerified()를 직접 호출하지 않고 UserCredential에서 제공
     *
     * @return 검증 완료되었으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isVerified() {
        return this.verifiedAt.isVerified();
    }

    /**
     * 인증 정보가 아직 검증되지 않았는지 확인합니다.
     *
     * @return 미검증 상태이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isNotVerified() {
        return this.verifiedAt.isNotVerified();
    }

    /**
     * 비밀번호가 일치하는지 검증합니다.
     * Infrastructure Layer의 PasswordEncoder에 검증을 위임합니다.
     *
     * @param rawPassword 평문 비밀번호 (null 불가)
     * @param passwordEncoder PasswordEncoder 인터페이스 구현체 (null 불가)
     * @return 비밀번호가 일치하면 true, 아니면 false
     * @throws IllegalArgumentException rawPassword 또는 passwordEncoder가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean matchesPassword(
            final String rawPassword,
            final PasswordHash.PasswordEncoder passwordEncoder
    ) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            return false;
        }
        return this.passwordHash.matches(rawPassword, passwordEncoder);
    }

    /**
     * 비밀번호를 변경합니다.
     * 불변성 원칙에 따라 새로운 UserCredential 인스턴스를 반환합니다.
     *
     * @param newPasswordHash 새로운 비밀번호 해시 (null 불가)
     * @return 비밀번호가 변경된 새로운 UserCredential 인스턴스
     * @throws NullPointerException newPasswordHash가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserCredential changePassword(final PasswordHash newPasswordHash) {
        Objects.requireNonNull(newPasswordHash, "New PasswordHash cannot be null");
        return new UserCredential(
                this.id,
                this.userId,
                this.type,
                this.identifier,
                newPasswordHash,
                this.verifiedAt,
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 인증 정보를 검증 완료 상태로 변경합니다.
     * 이미 검증된 경우 현재 인스턴스를 반환합니다.
     *
     * @return 검증 완료된 새로운 UserCredential 인스턴스 또는 이미 검증된 경우 현재 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserCredential verify() {
        if (this.verifiedAt.isVerified()) {
            return this; // 이미 검증 완료 상태이면 현재 인스턴스 반환
        }
        return new UserCredential(
                this.id,
                this.userId,
                this.type,
                this.identifier,
                this.passwordHash,
                VerifiedAt.now(),
                this.createdAt,
                Instant.now()
        );
    }

    /**
     * 두 UserCredential 객체의 동등성을 비교합니다.
     * CredentialId가 같으면 같은 인증 정보로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return CredentialId가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserCredential other = (UserCredential) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * CredentialId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * UserCredential의 문자열 표현을 반환합니다.
     * 보안상 passwordHash는 마스킹 처리합니다.
     *
     * @return "UserCredential{id=..., type=..., identifier=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "UserCredential{" +
                "id=" + this.id +
                ", userId=" + this.userId +
                ", type=" + this.type +
                ", identifier=" + this.identifier +
                ", passwordHash=***MASKED***" +
                ", verifiedAt=" + this.verifiedAt +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
