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

    /**
     * 새로운 프로필과 병합 (null인 필드는 기존 값 유지)
     *
     * @param other 병합할 프로필 정보
     * @return 병합된 새로운 UserProfile 인스턴스
     */
    public UserProfile mergeWith(UserProfile other) {
        if (other == null) {
            return this;
        }
        return new UserProfile(
                other.name != null ? other.name : this.name,
                other.nickname != null ? other.nickname : this.nickname,
                other.profileImageUrl != null ? other.profileImageUrl : this.profileImageUrl
        );
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
