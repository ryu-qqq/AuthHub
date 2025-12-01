package com.ryuqq.authhub.domain.user.vo.fixture;

import com.ryuqq.authhub.domain.user.vo.Credential;
import com.ryuqq.authhub.domain.user.vo.CredentialType;
import com.ryuqq.authhub.domain.user.vo.Email;
import com.ryuqq.authhub.domain.user.vo.Password;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;

/**
 * CredentialFixture - Credential VO 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class CredentialFixture {

    private static final String DEFAULT_EMAIL = "test@example.com";
    private static final String DEFAULT_PHONE = "+821012345678";
    private static final String DEFAULT_HASHED_PASSWORD = "$2a$10$hashedPasswordValue123456";

    private CredentialFixture() {
    }

    public static Credential aPhoneCredential() {
        return Credential.ofPhone(
                PhoneNumber.of(DEFAULT_PHONE),
                Password.ofHashed(DEFAULT_HASHED_PASSWORD)
        );
    }

    public static Credential anEmailCredential() {
        return Credential.ofEmail(
                Email.of(DEFAULT_EMAIL),
                Password.ofHashed(DEFAULT_HASHED_PASSWORD)
        );
    }

    public static Credential aCredentialWithEmail(String email) {
        return Credential.ofEmail(
                Email.of(email),
                Password.ofHashed(DEFAULT_HASHED_PASSWORD)
        );
    }

    public static Credential aCredentialWithPhone(String phone) {
        return Credential.ofPhone(
                PhoneNumber.of(phone),
                Password.ofHashed(DEFAULT_HASHED_PASSWORD)
        );
    }

    public static Credential aCredentialWithoutPassword() {
        return Credential.of(
                CredentialType.EMAIL,
                DEFAULT_EMAIL,
                null
        );
    }
}
