package com.ryuqq.authhub.domain.security.audit.vo;

/**
 * 감사 로그의 리소스 타입을 나타내는 Enum.
 * 시스템에서 관리하는 주요 리소스를 분류합니다.
 *
 * <p><strong>리소스 타입 정의:</strong></p>
 * <ul>
 *   <li>USER - 사용자</li>
 *   <li>ORGANIZATION - 조직</li>
 *   <li>COMPANY - 회사</li>
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
public enum ResourceType {

    /**
     * 사용자 리소스.
     */
    USER("사용자", "시스템 사용자 계정"),

    /**
     * 조직 리소스.
     */
    ORGANIZATION("조직", "사용자가 소속된 조직"),

    /**
     * 회사 리소스.
     */
    COMPANY("회사", "조직이 소속된 회사");

    private final String displayName;
    private final String description;

    /**
     * ResourceType 생성자.
     *
     * @param displayName 표시 이름
     * @param description 설명
     */
    ResourceType(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 리소스 타입의 표시 이름을 반환합니다.
     *
     * @return 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * 리소스 타입의 설명을 반환합니다.
     *
     * @return 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 사용자 리소스 타입인지 확인합니다.
     *
     * @return 사용자이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 조직 리소스 타입인지 확인합니다.
     *
     * @return 조직이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isOrganization() {
        return this == ORGANIZATION;
    }

    /**
     * 회사 리소스 타입인지 확인합니다.
     *
     * @return 회사이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isCompany() {
        return this == COMPANY;
    }
}
