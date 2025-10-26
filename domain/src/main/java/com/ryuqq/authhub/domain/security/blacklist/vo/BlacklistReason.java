package com.ryuqq.authhub.domain.security.blacklist.vo;

/**
 * 토큰이 블랙리스트에 등록된 사유를 정의하는 Enum.
 *
 * <p>블랙리스트 등록 사유는 보안 감사와 분석에 중요한 정보를 제공합니다.
 * 각 사유는 서로 다른 보안 정책과 대응 방안을 필요로 합니다.</p>
 *
 * <p><strong>사유별 설명:</strong></p>
 * <ul>
 *   <li><strong>LOGOUT</strong>: 정상적인 로그아웃 - 사용자가 명시적으로 로그아웃한 경우</li>
 *   <li><strong>FORCE_LOGOUT</strong>: 강제 로그아웃 - 관리자에 의한 세션 강제 종료</li>
 *   <li><strong>SECURITY_BREACH</strong>: 보안 침해 - 의심스러운 활동 감지 시 토큰 무효화</li>
 *   <li><strong>PASSWORD_CHANGE</strong>: 비밀번호 변경 - 비밀번호 변경 시 기존 토큰 무효화</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Plain Java Enum 사용</li>
 *   <li>✅ 불변성 보장 - Enum의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공</li>
 * </ul>
 *
 * <p><strong>보안 고려사항:</strong></p>
 * <ul>
 *   <li>SECURITY_BREACH의 경우 추가적인 보안 조치 필요 (계정 잠금, 알림 등)</li>
 *   <li>PASSWORD_CHANGE의 경우 해당 사용자의 모든 토큰 무효화 권장</li>
 *   <li>FORCE_LOGOUT의 경우 감사 로그 기록 필수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum BlacklistReason {

    /**
     * 정상적인 로그아웃.
     * 사용자가 명시적으로 로그아웃 요청을 한 경우.
     */
    LOGOUT("정상 로그아웃", "사용자가 명시적으로 로그아웃하여 토큰을 무효화합니다"),

    /**
     * 강제 로그아웃.
     * 관리자가 사용자의 세션을 강제로 종료한 경우.
     * 보안 정책 위반, 계정 정지 등의 상황에서 사용됩니다.
     */
    FORCE_LOGOUT("강제 로그아웃", "관리자에 의해 강제로 세션이 종료되어 토큰을 무효화합니다"),

    /**
     * 보안 침해.
     * 의심스러운 활동이 감지되어 토큰을 즉시 무효화해야 하는 경우.
     * 비정상적인 접근 패턴, IP 변경, 동시 접속 등이 감지된 경우.
     */
    SECURITY_BREACH("보안 침해", "보안 위협이 감지되어 토큰을 즉시 무효화합니다"),

    /**
     * 비밀번호 변경.
     * 사용자가 비밀번호를 변경하여 기존 모든 토큰을 무효화해야 하는 경우.
     * 보안 강화를 위해 비밀번호 변경 시 기존 세션을 모두 종료합니다.
     */
    PASSWORD_CHANGE("비밀번호 변경", "비밀번호가 변경되어 기존 토큰을 무효화합니다");

    private final String displayName;
    private final String description;

    /**
     * BlacklistReason Enum 생성자.
     *
     * @param displayName 사유의 표시 이름
     * @param description 사유의 상세 설명
     */
    BlacklistReason(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 사유의 표시 이름을 반환합니다.
     *
     * @return 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 사유의 상세 설명을 반환합니다.
     *
     * @return 상세 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 정상 로그아웃 사유인지 확인합니다.
     *
     * @return 정상 로그아웃이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isLogout() {
        return this == LOGOUT;
    }

    /**
     * 강제 로그아웃 사유인지 확인합니다.
     *
     * @return 강제 로그아웃이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isForceLogout() {
        return this == FORCE_LOGOUT;
    }

    /**
     * 보안 침해 사유인지 확인합니다.
     *
     * @return 보안 침해면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSecurityBreach() {
        return this == SECURITY_BREACH;
    }

    /**
     * 비밀번호 변경 사유인지 확인합니다.
     *
     * @return 비밀번호 변경이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isPasswordChange() {
        return this == PASSWORD_CHANGE;
    }

    /**
     * 보안 관련 사유인지 확인합니다 (SECURITY_BREACH 또는 FORCE_LOGOUT).
     * 보안 관련 사유의 경우 추가적인 보안 조치가 필요할 수 있습니다.
     *
     * @return 보안 관련 사유면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean requiresSecurityAction() {
        return this == SECURITY_BREACH || this == FORCE_LOGOUT;
    }
}
