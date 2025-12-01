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
    private static final String DEFAULT_NICKNAME = "hong";
    private static final String DEFAULT_PROFILE_IMAGE_URL = "https://example.com/profile.png";

    private UserProfileFixture() {
    }

    public static UserProfile aUserProfile() {
        return UserProfile.of(DEFAULT_NAME, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL);
    }

    public static UserProfile aUserProfileWithName(String name) {
        return UserProfile.of(name, DEFAULT_NICKNAME, DEFAULT_PROFILE_IMAGE_URL);
    }

    public static UserProfile aUserProfileWithNickname(String nickname) {
        return UserProfile.of(DEFAULT_NAME, nickname, DEFAULT_PROFILE_IMAGE_URL);
    }

    public static UserProfile anEmptyUserProfile() {
        return UserProfile.empty();
    }

    public static UserProfile aUserProfileWithoutImage() {
        return UserProfile.of(DEFAULT_NAME, DEFAULT_NICKNAME, null);
    }
}
