package com.ryuqq.authhub.domain.security.audit;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.AuditLogId;
import com.ryuqq.authhub.domain.security.audit.vo.IpAddress;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;
import com.ryuqq.authhub.domain.security.audit.vo.UserAgent;

import java.time.Instant;
import java.util.Objects;

/**
 * AuditLog Aggregate Root.
 *
 * <p>감사 로그를 나타내는 Aggregate Root로서, 시스템에서 발생하는 모든 중요 행위를 기록합니다.
 * DDD(Domain-Driven Design) 원칙에 따라 설계되었으며, 불변성과 도메인 규칙을 엄격히 준수합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>감사 로그 식별자(AuditLogId) 관리</li>
 *   <li>사용자 식별자(UserId) 관리</li>
 *   <li>액션 타입(ActionType) 관리 - LOGIN, LOGOUT, CREATE, UPDATE, DELETE</li>
 *   <li>리소스 타입(ResourceType) 관리 - USER, ORGANIZATION, COMPANY</li>
 *   <li>리소스 ID 관리 - 대상 리소스의 식별자</li>
 *   <li>IP 주소(IpAddress) 관리 - 요청 출처 IP</li>
 *   <li>User-Agent(UserAgent) 관리 - 클라이언트 정보</li>
 *   <li>발생 시각(occurredAt) 관리 - 감사 로그 생성 시각</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java getter/메서드 직접 구현</li>
 *   <li>✅ Law of Demeter 준수 - 직접적인 행위 메서드 제공, getter chaining 금지</li>
 *   <li>✅ 불변성 보장 - 모든 필드 final, 생성 후 변경 불가</li>
 *   <li>✅ Javadoc 완비 - 모든 public 메서드에 @author, @since 포함</li>
 *   <li>✅ Factory Method 패턴 - create() 및 reconstruct() 메서드 제공</li>
 * </ul>
 *
 * <p><strong>도메인 규칙:</strong></p>
 * <ul>
 *   <li>감사 로그는 반드시 고유한 AuditLogId를 가져야 함</li>
 *   <li>모든 필드는 생성 시 결정되며 변경 불가 (Immutable)</li>
 *   <li>userId는 null일 수 없음 (인증되지 않은 사용자는 별도 처리)</li>
 *   <li>resourceId는 null 가능 (일부 액션은 특정 리소스 없이 발생)</li>
 *   <li>occurredAt은 생성 시각을 기록하며 과거 시각일 수 없음</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 로그인 감사 로그 생성
 * AuditLog loginLog = AuditLog.create(
 *     UserId.fromString("user-uuid"),
 *     ActionType.LOGIN,
 *     ResourceType.USER,
 *     "user-resource-id",
 *     IpAddress.of("192.168.0.1"),
 *     UserAgent.of("Mozilla/5.0...")
 * );
 *
 * // 리소스 생성 감사 로그
 * AuditLog createLog = AuditLog.create(
 *     UserId.fromString("user-uuid"),
 *     ActionType.CREATE,
 *     ResourceType.ORGANIZATION,
 *     "org-resource-id",
 *     IpAddress.of("10.0.0.1"),
 *     UserAgent.of("Chrome/120.0")
 * );
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class AuditLog {

    private final AuditLogId id;
    private final UserId userId;
    private final ActionType actionType;
    private final ResourceType resourceType;
    private final String resourceId;
    private final IpAddress ipAddress;
    private final UserAgent userAgent;
    private final Instant occurredAt;

    /**
     * AuditLog 생성자 (private).
     * 외부에서는 팩토리 메서드를 통해서만 생성 가능합니다.
     *
     * @param id 감사 로그 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param actionType 액션 타입 (null 불가)
     * @param resourceType 리소스 타입 (null 불가)
     * @param resourceId 리소스 ID (nullable)
     * @param ipAddress IP 주소 (null 불가)
     * @param userAgent User-Agent (null 불가)
     * @param occurredAt 발생 시각 (null 불가)
     */
    private AuditLog(
            final AuditLogId id,
            final UserId userId,
            final ActionType actionType,
            final ResourceType resourceType,
            final String resourceId,
            final IpAddress ipAddress,
            final UserAgent userAgent,
            final Instant occurredAt
    ) {
        this.id = Objects.requireNonNull(id, "AuditLogId cannot be null");
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.actionType = Objects.requireNonNull(actionType, "ActionType cannot be null");
        this.resourceType = Objects.requireNonNull(resourceType, "ResourceType cannot be null");
        this.resourceId = resourceId;
        this.ipAddress = Objects.requireNonNull(ipAddress, "IpAddress cannot be null");
        this.userAgent = Objects.requireNonNull(userAgent, "UserAgent cannot be null");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    /**
     * 새로운 감사 로그를 생성합니다 (Factory Method).
     *
     * @param userId 사용자 식별자 (null 불가)
     * @param actionType 액션 타입 (null 불가)
     * @param resourceType 리소스 타입 (null 불가)
     * @param resourceId 리소스 ID (nullable)
     * @param ipAddress IP 주소 (null 불가)
     * @param userAgent User-Agent (null 불가)
     * @return 새로 생성된 AuditLog 인스턴스
     * @throws NullPointerException 필수 파라미터가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static AuditLog create(
            final UserId userId,
            final ActionType actionType,
            final ResourceType resourceType,
            final String resourceId,
            final IpAddress ipAddress,
            final UserAgent userAgent
    ) {
        return new AuditLog(
                AuditLogId.newId(),
                userId,
                actionType,
                resourceType,
                resourceId,
                ipAddress,
                userAgent,
                Instant.now()
        );
    }

    /**
     * 기존 데이터로부터 AuditLog을 재구성합니다.
     * 주로 영속성 계층에서 데이터를 로드할 때 사용됩니다.
     *
     * @param id 감사 로그 식별자 (null 불가)
     * @param userId 사용자 식별자 (null 불가)
     * @param actionType 액션 타입 (null 불가)
     * @param resourceType 리소스 타입 (null 불가)
     * @param resourceId 리소스 ID (nullable)
     * @param ipAddress IP 주소 (null 불가)
     * @param userAgent User-Agent (null 불가)
     * @param occurredAt 발생 시각 (null 불가)
     * @return 재구성된 AuditLog 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static AuditLog reconstruct(
            final AuditLogId id,
            final UserId userId,
            final ActionType actionType,
            final ResourceType resourceType,
            final String resourceId,
            final IpAddress ipAddress,
            final UserAgent userAgent,
            final Instant occurredAt
    ) {
        return new AuditLog(id, userId, actionType, resourceType, resourceId, ipAddress, userAgent, occurredAt);
    }

    /**
     * 감사 로그 식별자를 반환합니다.
     *
     * @return AuditLogId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuditLogId getId() {
        return this.id;
    }

    /**
     * 사용자 식별자를 반환합니다.
     *
     * @return UserId 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserId getUserId() {
        return this.userId;
    }

    /**
     * 액션 타입을 반환합니다.
     *
     * @return ActionType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ActionType getActionType() {
        return this.actionType;
    }

    /**
     * 리소스 타입을 반환합니다.
     *
     * @return ResourceType enum
     * @author AuthHub Team
     * @since 1.0.0
     */
    public ResourceType getResourceType() {
        return this.resourceType;
    }

    /**
     * 리소스 ID를 반환합니다.
     *
     * @return 리소스 ID 문자열 (nullable)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getResourceId() {
        return this.resourceId;
    }

    /**
     * IP 주소를 반환합니다.
     *
     * @return IpAddress 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public IpAddress getIpAddress() {
        return this.ipAddress;
    }

    /**
     * User-Agent를 반환합니다.
     *
     * @return UserAgent 인스턴스
     * @author AuthHub Team
     * @since 1.0.0
     */
    public UserAgent getUserAgent() {
        return this.userAgent;
    }

    /**
     * 발생 시각을 반환합니다.
     *
     * @return 발생 시각 Instant
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Instant getOccurredAt() {
        return this.occurredAt;
    }

    /**
     * 감사 로그 ID를 문자열로 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - id.asString() 대신 사용.
     *
     * @return 감사 로그 ID 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getIdAsString() {
        return this.id.asString();
    }

    /**
     * 사용자 ID를 문자열로 반환합니다 (Law of Demeter 준수).
     * getter chaining 방지 - userId.asString() 대신 사용.
     *
     * @return 사용자 ID 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserIdAsString() {
        return this.userId.asString();
    }

    /**
     * 액션 타입 표시 이름을 반환합니다 (Law of Demeter 준수).
     *
     * @return 액션 타입 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getActionTypeDisplayName() {
        return this.actionType.getDisplayName();
    }

    /**
     * 액션 타입 설명을 반환합니다 (Law of Demeter 준수).
     *
     * @return 액션 타입 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getActionTypeDescription() {
        return this.actionType.getDescription();
    }

    /**
     * 리소스 타입 표시 이름을 반환합니다 (Law of Demeter 준수).
     *
     * @return 리소스 타입 표시 이름
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getResourceTypeDisplayName() {
        return this.resourceType.getDisplayName();
    }

    /**
     * 리소스 타입 설명을 반환합니다 (Law of Demeter 준수).
     *
     * @return 리소스 타입 설명
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getResourceTypeDescription() {
        return this.resourceType.getDescription();
    }

    /**
     * IP 주소를 문자열로 반환합니다 (Law of Demeter 준수).
     *
     * @return IP 주소 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getIpAddressAsString() {
        return this.ipAddress.asString();
    }

    /**
     * User-Agent를 문자열로 반환합니다 (Law of Demeter 준수).
     *
     * @return User-Agent 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getUserAgentAsString() {
        return this.userAgent.asString();
    }

    /**
     * 로그인 액션인지 확인합니다.
     *
     * @return 로그인 액션이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isLoginAction() {
        return this.actionType.isLogin();
    }

    /**
     * 로그아웃 액션인지 확인합니다.
     *
     * @return 로그아웃 액션이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isLogoutAction() {
        return this.actionType.isLogout();
    }

    /**
     * 데이터 변경 액션인지 확인합니다.
     * CREATE, UPDATE, DELETE를 데이터 변경 액션으로 간주합니다.
     *
     * @return 데이터 변경 액션이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDataModificationAction() {
        return this.actionType.isDataModification();
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
        return this.actionType.isAuthenticationAction();
    }

    /**
     * 사용자 리소스 관련 감사 로그인지 확인합니다.
     *
     * @return 사용자 리소스이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isUserResource() {
        return this.resourceType.isUser();
    }

    /**
     * 조직 리소스 관련 감사 로그인지 확인합니다.
     *
     * @return 조직 리소스이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isOrganizationResource() {
        return this.resourceType.isOrganization();
    }

    /**
     * 회사 리소스 관련 감사 로그인지 확인합니다.
     *
     * @return 회사 리소스이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isCompanyResource() {
        return this.resourceType.isCompany();
    }

    /**
     * 모바일에서 발생한 감사 로그인지 확인합니다.
     *
     * @return 모바일에서 발생했으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isMobileRequest() {
        return this.userAgent.isMobile();
    }

    /**
     * 데스크톱에서 발생한 감사 로그인지 확인합니다.
     *
     * @return 데스크톱에서 발생했으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isDesktopRequest() {
        return this.userAgent.isDesktop();
    }

    /**
     * 리소스 ID가 있는지 확인합니다.
     *
     * @return 리소스 ID가 있으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean hasResourceId() {
        return this.resourceId != null && !this.resourceId.trim().isEmpty();
    }

    /**
     * 두 AuditLog 객체의 동등성을 비교합니다.
     * AuditLogId가 같으면 같은 감사 로그로 간주합니다.
     *
     * @param obj 비교 대상 객체
     * @return AuditLogId가 같으면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AuditLog other = (AuditLog) obj;
        return Objects.equals(this.id, other.id);
    }

    /**
     * 해시 코드를 반환합니다.
     * AuditLogId를 기준으로 계산됩니다.
     *
     * @return 해시 코드
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    /**
     * AuditLog의 문자열 표현을 반환합니다.
     * String.format()을 사용하여 가독성과 성능을 개선합니다.
     *
     * @return "AuditLog{id=..., userId=..., ...}" 형식의 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return String.format("AuditLog{id=%s, userId=%s, actionType=%s, resourceType=%s, resourceId='%s', ipAddress=%s, userAgent=%s, occurredAt=%s}",
                this.id,
                this.userId,
                this.actionType,
                this.resourceType,
                this.resourceId,
                this.ipAddress,
                this.userAgent,
                this.occurredAt
        );
    }
}
