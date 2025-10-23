package com.ryuqq.authhub.domain.auth.user;

/**
 * User의 계정 상태를 나타내는 Value Object.
 *
 * <p>사용자 계정은 다음 세 가지 상태 중 하나를 가질 수 있습니다:</p>
 * <ul>
 *   <li>{@link #ACTIVE} - 정상 활성 상태</li>
 *   <li>{@link #INACTIVE} - 비활성 상태 (휴면 계정 등)</li>
 *   <li>{@link #SUSPENDED} - 정지 상태 (관리자에 의한 계정 정지)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>Lombok 미사용 - Plain Java enum 사용</li>
 *   <li>불변성 보장 - enum은 본질적으로 불변</li>
 *   <li>Law of Demeter 준수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum UserStatus {

    /**
     * 정상 활성 상태.
     * 사용자가 시스템을 정상적으로 사용할 수 있는 상태입니다.
     */
    ACTIVE("활성", "정상적으로 사용 가능한 계정"),

    /**
     * 비활성 상태.
     * 장기간 미사용으로 휴면 처리된 계정 등이 해당됩니다.
     */
    INACTIVE("비활성", "휴면 또는 비활성화된 계정"),

    /**
     * 정지 상태.
     * 관리자에 의해 일시적 또는 영구적으로 정지된 계정입니다.
     */
    SUSPENDED("정지", "관리자에 의해 정지된 계정");

    private final String displayName;
    private final String description;

    /**
     * UserStatus enum 생성자.
     *
     * @param displayName 화면 표시용 이름
     * @param description 상태에 대한 설명
     */
    UserStatus(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 화면 표시용 이름을 반환합니다.
     *
     * @return 한글 표시명
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 상태에 대한 설명을 반환합니다.
     *
     * @return 상태 설명
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 현재 상태가 활성 상태인지 확인합니다.
     *
     * @return ACTIVE 상태이면 true, 아니면 false
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * 현재 상태가 비활성 상태인지 확인합니다.
     *
     * @return INACTIVE 상태이면 true, 아니면 false
     */
    public boolean isInactive() {
        return this == INACTIVE;
    }

    /**
     * 현재 상태가 정지 상태인지 확인합니다.
     *
     * @return SUSPENDED 상태이면 true, 아니면 false
     */
    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    /**
     * 사용자가 시스템을 사용할 수 있는 상태인지 확인합니다.
     * ACTIVE 상태일 때만 true를 반환합니다.
     *
     * @return 시스템 사용 가능하면 true, 아니면 false
     */
    public boolean canUseSystem() {
        return this.isActive();
    }
}
