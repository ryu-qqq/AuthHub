package com.ryuqq.authhub.application.token.dto.composite;

/**
 * UserContextComposite - 사용자 컨텍스트 Composite
 *
 * <p>User, Organization, Tenant 조인 조회 결과를 담는 Composite DTO입니다.
 *
 * @param userId 사용자 ID (UUIDv7 String)
 * @param email 사용자 이메일 (Identifier)
 * @param name 사용자 이름 (Identifier에서 추출하거나 별도 필드)
 * @param tenantId 테넌트 ID
 * @param tenantName 테넌트 이름
 * @param organizationId 조직 ID
 * @param organizationName 조직 이름
 * @author development-team
 * @since 1.0.0
 */
public record UserContextComposite(
        String userId,
        String email,
        String name,
        String tenantId,
        String tenantName,
        String organizationId,
        String organizationName) {

    /**
     * Builder 패턴으로 UserContextComposite 생성
     *
     * @return UserContextCompositeBuilder
     */
    public static UserContextCompositeBuilder builder() {
        return new UserContextCompositeBuilder();
    }

    /** UserContextComposite Builder */
    public static final class UserContextCompositeBuilder {
        private String userId;
        private String email;
        private String name;
        private String tenantId;
        private String tenantName;
        private String organizationId;
        private String organizationName;

        private UserContextCompositeBuilder() {}

        public UserContextCompositeBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserContextCompositeBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserContextCompositeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserContextCompositeBuilder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public UserContextCompositeBuilder tenantName(String tenantName) {
            this.tenantName = tenantName;
            return this;
        }

        public UserContextCompositeBuilder organizationId(String organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public UserContextCompositeBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public UserContextComposite build() {
            return new UserContextComposite(
                    userId, email, name, tenantId, tenantName, organizationId, organizationName);
        }
    }
}
