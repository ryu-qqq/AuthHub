package com.ryuqq.authhub.domain.user.vo.fixture;

import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.Email;
import com.ryuqq.authhub.domain.user.vo.Password;

/**
 * CredentialFixture - Credential VO 테스트 픽스처
 *
 * <p>Credential은 이메일 또는 아이디 형식의 identifier와 Password로 구성됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CredentialFixture {

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_IDENTIFIER = "testuser123";
    private static final String DEFAULT_HASHED_PASSWORD = "$2a$10$hashedPasswordValue123456";

    private CredentialFixture() {}

    public static Credential aCredential() {
        return Credential.of(DEFAULT_EMAIL, Password.ofHashed(DEFAULT_HASHED_PASSWORD));
    }

    public static Credential anEmailCredential() {
        return Credential.ofEmail(
                Email.of(DEFAULT_EMAIL), Password.ofHashed(DEFAULT_HASHED_PASSWORD));
    }

    public static Credential aCredentialWithEmail(String email) {
        return Credential.ofEmail(Email.of(email), Password.ofHashed(DEFAULT_HASHED_PASSWORD));
    }

    public static Credential aCredentialWithIdentifier(String identifier) {
        return Credential.of(identifier, Password.ofHashed(DEFAULT_HASHED_PASSWORD));
    }

    public static Credential aCredentialWithoutPassword() {
        return Credential.of(DEFAULT_EMAIL, null);
    }
}
