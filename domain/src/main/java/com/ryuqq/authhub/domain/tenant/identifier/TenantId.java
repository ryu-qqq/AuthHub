package com.ryuqq.authhub.domain.tenant.identifier;

/** TenantId Identifier Tenant의 식별자 */
public record TenantId(Long value) {

    public static TenantId of(Long value) {
        validate(value);
        return new TenantId(value);
    }

    private static void validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("TenantId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("TenantId는 양수여야 합니다");
        }
    }

    /**
     * 새 ID 생성 (아직 영속화되지 않은 상태)
     *
     * @return null 값을 가진 TenantId
     */
    public static TenantId forNew() {
        return new TenantId(null);
    }

    /**
     * 새 ID 여부 확인
     *
     * @return value가 null이면 true
     */
    public boolean isNew() {
        return value == null;
    }
}
