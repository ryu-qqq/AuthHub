package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.common.model.Identifier;

import java.util.UUID;

/**
 * UserId Value Object
 * UUID 기반 사용자 식별자
 */
public record UserId(UUID value) implements Identifier<UUID> {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("UserId는 null일 수 없습니다");
        }
    }

    /**
     * 정적 팩토리 메서드
     * @param value UUID 값
     * @return UserId 인스턴스
     */
    public static UserId of(UUID value) {
        return new UserId(value);
    }

    /**
     * 새 ID 생성 (UUID 자동 생성)
     * @return 새로운 UserId 인스턴스
     */
    public static UserId forNew() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * 새 ID 여부 확인 (항상 false, UUID는 null이 될 수 없음)
     * @return false
     */
    public boolean isNew() {
        return false;
    }
}
