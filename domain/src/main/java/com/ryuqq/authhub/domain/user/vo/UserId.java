package com.ryuqq.authhub.domain.user.vo;

import com.ryuqq.authhub.domain.common.model.Identifier;

import java.util.Objects;
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
}
