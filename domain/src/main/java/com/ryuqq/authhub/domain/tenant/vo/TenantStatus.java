package com.ryuqq.authhub.domain.tenant.vo;

/**
 * TenantStatus - 테넌트 상태 Value Object
 *
 * <p>상태 전이 규칙:
 *
 * <ul>
 *   <li>ACTIVE ↔ INACTIVE (양방향 전이 가능)
 *   <li>ACTIVE → DELETED (활성 상태에서 삭제)
 *   <li>INACTIVE → DELETED (비활성 상태에서 삭제)
 *   <li>DELETED → X (삭제 상태에서 다른 상태로 전이 불가)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum TenantStatus {
    ACTIVE {
        @Override
        public boolean canTransitionTo(TenantStatus target) {
            return target == INACTIVE || target == DELETED;
        }
    },
    INACTIVE {
        @Override
        public boolean canTransitionTo(TenantStatus target) {
            return target == ACTIVE || target == DELETED;
        }
    },
    DELETED {
        @Override
        public boolean canTransitionTo(TenantStatus target) {
            return false;
        }
    };

    /**
     * 대상 상태로 전이 가능 여부 확인
     *
     * @param target 전이 대상 상태
     * @return 전이 가능하면 true
     */
    public abstract boolean canTransitionTo(TenantStatus target);
}
