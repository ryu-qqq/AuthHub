package com.ryuqq.authhub.domain.user.vo;

import java.util.Objects;

/**
 * UserProfile - 사용자 프로필 Value Object
 *
 * <p>사용자의 프로필 정보를 담고 있는 불변 객체입니다.
 *
 * <p><strong>Tenant별 phoneNumber 유니크 제약:</strong> 같은 Tenant 내에서 동일한 phoneNumber는 허용되지 않습니다. 이 제약은
 * Application Layer에서 검증합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class UserProfile {

    private final String name;
    private final PhoneNumber phoneNumber;

    private UserProfile(String name, PhoneNumber phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public static UserProfile of(String name, PhoneNumber phoneNumber) {
        return new UserProfile(name, phoneNumber);
    }

    public static UserProfile of(String name, String phoneNumber) {
        return new UserProfile(name, phoneNumber != null ? PhoneNumber.of(phoneNumber) : null);
    }

    public static UserProfile ofName(String name) {
        return new UserProfile(name, null);
    }

    public static UserProfile empty() {
        return new UserProfile(null, null);
    }

    public String name() {
        return name;
    }

    public PhoneNumber phoneNumber() {
        return phoneNumber;
    }

    public String phoneNumberValue() {
        return phoneNumber != null ? phoneNumber.value() : null;
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null;
    }

    public UserProfile withName(String newName) {
        return new UserProfile(newName, this.phoneNumber);
    }

    public UserProfile withPhoneNumber(PhoneNumber newPhoneNumber) {
        return new UserProfile(this.name, newPhoneNumber);
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
                other.phoneNumber != null ? other.phoneNumber : this.phoneNumber);
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
        return Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }

    @Override
    public String toString() {
        return "UserProfile{name='" + name + "', phoneNumber=" + phoneNumber + "}";
    }
}
