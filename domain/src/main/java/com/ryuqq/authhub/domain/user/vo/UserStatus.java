package com.ryuqq.authhub.domain.user.vo;

/**
 * UserStatus - 사용자 상태 Value Object
 *
 * <p>사용자의 현재 상태를 나타내는 enum입니다.
 *
 * <p><strong>상태 전이:</strong>
 *
 * <ul>
 *   <li>ACTIVE: 정상 활성 상태 (로그인 가능)
 *   <li>INACTIVE: 비활성 상태 (이메일 미인증 등)
 *   <li>SUSPENDED: 일시 정지 상태 (관리자에 의한 정지)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum UserStatus {

    /** 정상 활성 상태 */
    ACTIVE,

    /** 비활성 상태 (미인증 등) */
    INACTIVE,

    /** 일시 정지 상태 */
    SUSPENDED;

    /**
     * 활성 상태인지 확인
     *
     * @return ACTIVE면 true
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 로그인 가능 상태인지 확인
     *
     * @return ACTIVE면 true
     */
    public boolean canLogin() {
        return this == ACTIVE;
    }

    /**
     * 정지된 상태인지 확인
     *
     * @return SUSPENDED면 true
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }
}
