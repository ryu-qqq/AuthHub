package com.ryuqq.authhub.domain.security.audit.vo;

/**
 * 감사 로그의 액션 타입을 나타내는 Enum.
 * 시스템에서 발생하는 주요 행위를 분류합니다.
 *
 * <p><strong>액션 타입 정의:</strong></p>
 * <ul>
 *   <li>LOGIN - 사용자 로그인</li>
 *   <li>LOGOUT - 사용자 로그아웃</li>
 *   <li>CREATE - 리소스 생성</li>
 *   <li>UPDATE - 리소스 수정</li>
 *   <li>DELETE - 리소스 삭제</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java Enum</li>
 *   <li>✅ 불변성 보장 - Enum의 본질적 불변성</li>
 *   <li>✅ Law of Demeter 준수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public enum ActionType {

    /**
     * 사용자 로그인 행위.
     */
    LOGIN("로그인", "사용자가 시스템에 로그인했습니다."),

    /**
     * 사용자 로그아웃 행위.
     */
    LOGOUT("로그아웃", "사용자가 시스템에서 로그아웃했습니다."),

    /**
     * 리소스 생성 행위.
     */
    CREATE("생성", "새로운 리소스가 생성되었습니다."),

    /**
     * 리소스 수정 행위.
     */
    UPDATE("수정", "기존 리소스가 수정되었습니다."),

    /**
     * 리소스 삭제 행위.
     */
    DELETE("삭제", "기존 리소스가 삭제되었습니다.");

    private final String displayName;
    private final String description;

    /**
     * ActionType 생성자.
     *
     * @param displayName 표시 이름
     * @param description 설명
     */
    ActionType(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 액션 타입의 표시 이름을 반환합니다.
     *
     * @return 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 액션 타입의 설명을 반환합니다.
     *
     * @return 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 로그인 액션 타입인지 확인합니다.
     *
     * @return 로그인이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isLogin() {
        return this == LOGIN;
    }

    /**
     * 로그아웃 액션 타입인지 확인합니다.
     *
     * @return 로그아웃이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isLogout() {
        return this == LOGOUT;
    }

    /**
     * 생성 액션 타입인지 확인합니다.
     *
     * @return 생성이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isCreate() {
        return this == CREATE;
    }

    /**
     * 수정 액션 타입인지 확인합니다.
     *
     * @return 수정이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUpdate() {
        return this == UPDATE;
    }

    /**
     * 삭제 액션 타입인지 확인합니다.
     *
     * @return 삭제이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDelete() {
        return this == DELETE;
    }

    /**
     * 데이터 변경 액션인지 확인합니다.
     * CREATE, UPDATE, DELETE를 데이터 변경 액션으로 간주합니다.
     *
     * @return 데이터 변경 액션이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDataModification() {
        return this == CREATE || this == UPDATE || this == DELETE;
    }

    /**
     * 인증 관련 액션인지 확인합니다.
     * LOGIN, LOGOUT을 인증 관련 액션으로 간주합니다.
     *
     * @return 인증 관련 액션이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isAuthenticationAction() {
        return this == LOGIN || this == LOGOUT;
    }
}
