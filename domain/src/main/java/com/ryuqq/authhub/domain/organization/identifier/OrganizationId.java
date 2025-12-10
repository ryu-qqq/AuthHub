package com.ryuqq.authhub.domain.organization.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * OrganizationId - Organization 식별자 Value Object
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
 * <p><strong>UUID 생성 책임</strong>: Application Layer (UuidHolder 인터페이스 통해 주입)
 *
 * @author development-team
 * @since 1.0.0
 */
public final class OrganizationId {

    private final UUID value;

    OrganizationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
        this.value = value;
    }

    /**
     * 새로운 OrganizationId 생성 (Application Layer에서 UUID 생성 후 전달)
     *
     * @param uuid Application Layer에서 생성된 UUIDv7
     * @return 새로운 OrganizationId 인스턴스
     */
    public static OrganizationId forNew(UUID uuid) {
        return new OrganizationId(uuid);
    }

    /**
     * 기존 UUID로 OrganizationId 생성 (DB 조회 시 사용)
     *
     * @param value UUID 값
     * @return OrganizationId 인스턴스
     */
    public static OrganizationId of(UUID value) {
        return new OrganizationId(value);
    }

    /**
     * 문자열로부터 OrganizationId 생성
     *
     * @param value UUID 문자열
     * @return OrganizationId 인스턴스
     * @throws IllegalArgumentException 유효하지 않은 UUID 형식인 경우
     */
    public static OrganizationId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OrganizationId 문자열은 null이거나 빈 값일 수 없습니다");
        }
        try {
            return new OrganizationId(UUID.fromString(value));
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
        OrganizationId that = (OrganizationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OrganizationId{value=" + value + "}";
    }
}
