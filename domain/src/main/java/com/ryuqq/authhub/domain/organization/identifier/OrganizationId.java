package com.ryuqq.authhub.domain.organization.identifier;

/** OrganizationId Identifier Organization의 식별자 */
public record OrganizationId(Long value) {

    public static OrganizationId of(Long value) {
        validate(value);
        return new OrganizationId(value);
    }

    private static void validate(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("OrganizationId는 null일 수 없습니다");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("OrganizationId는 양수여야 합니다");
        }
    }

    /**
     * 새 ID 생성 (아직 영속화되지 않은 상태)
     *
     * @return null 값을 가진 OrganizationId
     */
    public static OrganizationId forNew() {
        return new OrganizationId(null);
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
