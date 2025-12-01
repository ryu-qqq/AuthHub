package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;

/**
 * UserProfile - 사용자 프로필 Value Object
 *
 * <p>사용자의 프로필 정보를 담고 있는 불변 객체입니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserProfile {

    private final String name;
    private final String nickname;
    private final String profileImageUrl;

    private UserProfile(String name, String nickname, String profileImageUrl) {
        this.name = name;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static UserProfile of(String name, String nickname, String profileImageUrl) {
        return new UserProfile(name, nickname, profileImageUrl);
    }

    public static UserProfile empty() {
        return new UserProfile(null, null, null);
    }

    public String name() {
        return name;
    }

    public String nickname() {
        return nickname;
    }

    public String profileImageUrl() {
        return profileImageUrl;
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasNickname() {
        return nickname != null && !nickname.isBlank();
    }

    public boolean hasProfileImage() {
        return profileImageUrl != null && !profileImageUrl.isBlank();
    }

    public UserProfile withName(String newName) {
        return new UserProfile(newName, this.nickname, this.profileImageUrl);
    }

    public UserProfile withNickname(String newNickname) {
        return new UserProfile(this.name, newNickname, this.profileImageUrl);
    }

    public UserProfile withProfileImageUrl(String newUrl) {
        return new UserProfile(this.name, this.nickname, newUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProfile that = (UserProfile) o;
        return Objects.equals(name, that.name)
                && Objects.equals(nickname, that.nickname)
                && Objects.equals(profileImageUrl, that.profileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, nickname, profileImageUrl);
    }

    @Override
    public String toString() {
        return "UserProfile{name='" + name + "', nickname='" + nickname + "'}";
    }
}
