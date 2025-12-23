package com.ryuqq.authhub.domain.permission.identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * PermissionUsageId - 권한 사용 이력 식별자 Value Object
 *
 * <p>권한 사용 이력의 고유 식별자입니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Lombok 금지 - Plain Java 사용
 *   <li>불변 객체 (Immutable)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class PermissionUsageId {

    private final UUID value;

    private PermissionUsageId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("PermissionUsageId는 null일 수 없습니다");
        }
        this.value = value;
    }

    /**
     * UUID로 PermissionUsageId 생성
     *
     * @param value UUID 값
     * @return PermissionUsageId 인스턴스
     */
    public static PermissionUsageId of(UUID value) {
        return new PermissionUsageId(value);
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
        PermissionUsageId that = (PermissionUsageId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
