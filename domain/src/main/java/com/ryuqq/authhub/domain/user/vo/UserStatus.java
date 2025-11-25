package com.ryuqq.authhub.domain.user.vo;

/** 사용자 상태 */
public enum UserStatus {
    /** 활성 상태 */
    ACTIVE,

    /** 비활성 상태 */
    INACTIVE,

    /** 일시 정지 상태 */
    SUSPENDED,

    /** 삭제된 상태 (Soft Delete) */
    DELETED
}
