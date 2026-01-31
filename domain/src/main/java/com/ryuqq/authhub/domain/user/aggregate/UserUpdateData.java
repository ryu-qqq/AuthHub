package com.ryuqq.authhub.domain.user.aggregate;

import com.ryuqq.authhub.domain.user.vo.PhoneNumber;

/**
 * UserUpdateData - 사용자 수정 데이터 Value Object
 *
 * <p>User 수정 시 변경 가능한 필드들을 담는 VO입니다.
 *
 * <p><strong>변경 가능한 필드:</strong>
 *
 * <ul>
 *   <li>phoneNumber: 전화번호
 * </ul>
 *
 * <p><strong>변경 불가 필드:</strong>
 *
 * <ul>
 *   <li>identifier: 로그인 식별자 (변경 불가)
 *   <li>password: 별도 비밀번호 변경 메서드 사용
 * </ul>
 *
 * @param phoneNumber 전화번호 (null 허용 - null이면 변경하지 않음)
 * @author development-team
 * @since 1.0.0
 */
public record UserUpdateData(PhoneNumber phoneNumber) {

    /**
     * UserUpdateData 생성
     *
     * @param phoneNumber 전화번호
     * @return UserUpdateData 인스턴스
     */
    public static UserUpdateData of(PhoneNumber phoneNumber) {
        return new UserUpdateData(phoneNumber);
    }

    /**
     * 전화번호가 있는지 확인
     *
     * @return phoneNumber가 null이 아니면 true
     */
    public boolean hasPhoneNumber() {
        return phoneNumber != null;
    }

    /**
     * 변경할 내용이 있는지 확인
     *
     * @return 하나라도 변경할 내용이 있으면 true
     */
    public boolean hasAnyUpdate() {
        return hasPhoneNumber();
    }
}
