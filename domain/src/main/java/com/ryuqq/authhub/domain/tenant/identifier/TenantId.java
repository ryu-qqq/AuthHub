package com.ryuqq.authhub.domain.tenant.identifier;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.Objects;
import java.util.UUID;

/**
 * TenantId - Tenant 식별자 Value Object
 *
 * <p>UUIDv7 기반 식별자를 사용합니다 (RFC 9562 준수).
 *
 * <p>UUIDv7 특징:
 *
 * <ul>
 *   <li>시간 기반 정렬 가능 (Time-ordered)
 *   <li>밀리초 정밀도의 타임스탬프 포함
 *   <li>분산 환경에서 충돌 없는 고유 ID 생성
 *   <li>외부 노출 시 보안 강화 (순차 증가하지 않음)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class TenantId {

    private final UUID value;

    TenantId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        this.value = value;
    }

    /**
     * 새로운 UUIDv7 기반 TenantId 생성
     *
     * @return 새로운 TenantId 인스턴스
     */
    public static TenantId forNew() {
        return new TenantId(UuidCreator.getTimeOrderedEpoch());
    }

    /**
     * 기존 UUID로 TenantId 생성
     *
     * @param value UUID 값
     * @return TenantId 인스턴스
     */
    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    /**
     * 문자열로부터 TenantId 생성
     *
     * @param value UUID 문자열
     * @return TenantId 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 UUID 형식인 경우
     */
    public static TenantId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TenantId 문자열은 null이거나 빈 값일 수 없습니다");
        }
        try {
            return new TenantId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 UUID 형식입니다: " + value, e);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TenantId tenantId = (TenantId) o;
        return Objects.equals(value, tenantId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TenantId{value=" + value + "}";
    }
}
