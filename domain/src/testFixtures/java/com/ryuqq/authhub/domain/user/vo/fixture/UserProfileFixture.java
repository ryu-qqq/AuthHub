package com.ryuqq.authhub.domain.user.vo.fixture;

import com.ryuqq.authhub.domain.user.vo.UserProfile;

/**
 * UserProfileFixture - UserProfile VO 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserProfileFixture {

    private static final String DEFAULT_NAME = "홍길동";
    private static final String DEFAULT_PHONE_NUMBER = "+821012345678";

    private UserProfileFixture() {}

    public static UserProfile aUserProfile() {
        return UserProfile.of(DEFAULT_NAME, DEFAULT_PHONE_NUMBER);
    }

    public static UserProfile aUserProfileWithName(String name) {
        return UserProfile.of(name, DEFAULT_PHONE_NUMBER);
    }

    public static UserProfile aUserProfileWithPhoneNumber(String phoneNumber) {
        return UserProfile.of(DEFAULT_NAME, phoneNumber);
    }

    public static UserProfile aUserProfileWithNameAndPhoneNumber(String name, String phoneNumber) {
        return UserProfile.of(name, phoneNumber);
    }

    public static UserProfile aUserProfileNameOnly() {
        return UserProfile.ofName(DEFAULT_NAME);
    }

    public static UserProfile anEmptyUserProfile() {
        return UserProfile.empty();
    }
}
