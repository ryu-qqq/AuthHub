package com.ryuqq.authhub.domain.security.audit;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.AuditLogId;
import com.ryuqq.authhub.domain.security.audit.vo.IpAddress;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;
import com.ryuqq.authhub.domain.security.audit.vo.UserAgent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/**
 * AuditLog Aggregate Root 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("AuditLog Aggregate Root 단위 테스트")
class AuditLogTest {

    @Test
    @DisplayName("create()로 새로운 AuditLog을 생성할 수 있다")
    void testCreate() {
        // given
        UserId userId = UserId.newId();
        ActionType actionType = ActionType.LOGIN;
        ResourceType resourceType = ResourceType.USER;
        String resourceId = "user-123";
        IpAddress ipAddress = IpAddress.of("192.168.0.1");
        UserAgent userAgent = UserAgent.of("Mozilla/5.0");

        // when
        AuditLog auditLog = AuditLog.create(userId, actionType, resourceType, resourceId, ipAddress, userAgent);

        // then
        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getId()).isNotNull();
        assertThat(auditLog.getUserId()).isEqualTo(userId);
        assertThat(auditLog.getActionType()).isEqualTo(actionType);
        assertThat(auditLog.getResourceType()).isEqualTo(resourceType);
        assertThat(auditLog.getResourceId()).isEqualTo(resourceId);
        assertThat(auditLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(auditLog.getUserAgent()).isEqualTo(userAgent);
        assertThat(auditLog.getOccurredAt()).isNotNull();
    }

    @Test
    @DisplayName("reconstruct()로 기존 데이터로부터 AuditLog을 재구성할 수 있다")
    void testReconstruct() {
        // given
        AuditLogId id = AuditLogId.newId();
        UserId userId = UserId.newId();
        ActionType actionType = ActionType.CREATE;
        ResourceType resourceType = ResourceType.ORGANIZATION;
        String resourceId = "org-456";
        IpAddress ipAddress = IpAddress.of("10.0.0.1");
        UserAgent userAgent = UserAgent.of("Chrome/120.0");
        Instant occurredAt = Instant.now();

        // when
        AuditLog auditLog = AuditLog.reconstruct(id, userId, actionType, resourceType, resourceId, ipAddress, userAgent, occurredAt);

        // then
        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getId()).isEqualTo(id);
        assertThat(auditLog.getUserId()).isEqualTo(userId);
        assertThat(auditLog.getActionType()).isEqualTo(actionType);
        assertThat(auditLog.getResourceType()).isEqualTo(resourceType);
        assertThat(auditLog.getResourceId()).isEqualTo(resourceId);
        assertThat(auditLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(auditLog.getUserAgent()).isEqualTo(userAgent);
        assertThat(auditLog.getOccurredAt()).isEqualTo(occurredAt);
    }

    @Test
    @DisplayName("create()에 null userId를 전달하면 예외가 발생한다")
    void testCreateWithNullUserId() {
        // when & then
        assertThatThrownBy(() -> AuditLog.create(
                null,
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserId cannot be null");
    }

    @Test
    @DisplayName("create()에 null actionType을 전달하면 예외가 발생한다")
    void testCreateWithNullActionType() {
        // when & then
        assertThatThrownBy(() -> AuditLog.create(
                UserId.newId(),
                null,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ActionType cannot be null");
    }

    @Test
    @DisplayName("create()에 null resourceType을 전달하면 예외가 발생한다")
    void testCreateWithNullResourceType() {
        // when & then
        assertThatThrownBy(() -> AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                null,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ResourceType cannot be null");
    }

    @Test
    @DisplayName("create()에 null ipAddress를 전달하면 예외가 발생한다")
    void testCreateWithNullIpAddress() {
        // when & then
        assertThatThrownBy(() -> AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                null,
                UserAgent.of("Mozilla/5.0")
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("IpAddress cannot be null");
    }

    @Test
    @DisplayName("create()에 null userAgent를 전달하면 예외가 발생한다")
    void testCreateWithNullUserAgent() {
        // when & then
        assertThatThrownBy(() -> AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                null
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("UserAgent cannot be null");
    }

    @Test
    @DisplayName("resourceId는 null일 수 있다")
    void testCreateWithNullResourceId() {
        // given
        UserId userId = UserId.newId();
        ActionType actionType = ActionType.LOGOUT;
        ResourceType resourceType = ResourceType.USER;
        IpAddress ipAddress = IpAddress.of("192.168.0.1");
        UserAgent userAgent = UserAgent.of("Mozilla/5.0");

        // when
        AuditLog auditLog = AuditLog.create(userId, actionType, resourceType, null, ipAddress, userAgent);

        // then
        assertThat(auditLog).isNotNull();
        assertThat(auditLog.getResourceId()).isNull();
        assertThat(auditLog.hasResourceId()).isFalse();
    }

    @Test
    @DisplayName("Law of Demeter 준수 - getIdAsString()으로 ID 문자열을 직접 반환한다")
    void testGetIdAsString() {
        // given
        AuditLog auditLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when
        String idString = auditLog.getIdAsString();

        // then
        assertThat(idString).isNotNull();
        assertThat(idString).isEqualTo(auditLog.getId().asString());
    }

    @Test
    @DisplayName("Law of Demeter 준수 - getUserIdAsString()으로 UserId 문자열을 직접 반환한다")
    void testGetUserIdAsString() {
        // given
        UserId userId = UserId.newId();
        AuditLog auditLog = AuditLog.create(
                userId,
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when
        String userIdString = auditLog.getUserIdAsString();

        // then
        assertThat(userIdString).isNotNull();
        assertThat(userIdString).isEqualTo(userId.asString());
    }

    @Test
    @DisplayName("Law of Demeter 준수 - getActionTypeDisplayName()으로 액션 타입 표시 이름을 직접 반환한다")
    void testGetActionTypeDisplayName() {
        // given
        AuditLog auditLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when
        String displayName = auditLog.getActionTypeDisplayName();

        // then
        assertThat(displayName).isEqualTo("로그인");
    }

    @Test
    @DisplayName("Law of Demeter 준수 - getIpAddressAsString()으로 IP 주소 문자열을 직접 반환한다")
    void testGetIpAddressAsString() {
        // given
        AuditLog auditLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when
        String ipString = auditLog.getIpAddressAsString();

        // then
        assertThat(ipString).isEqualTo("192.168.0.1");
    }

    @Test
    @DisplayName("isLoginAction()으로 로그인 액션인지 확인할 수 있다")
    void testIsLoginAction() {
        // given
        AuditLog auditLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "resource-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(auditLog.isLoginAction()).isTrue();
        assertThat(auditLog.isLogoutAction()).isFalse();
    }

    @Test
    @DisplayName("isDataModificationAction()으로 데이터 변경 액션인지 확인할 수 있다")
    void testIsDataModificationAction() {
        // given
        AuditLog createLog = AuditLog.create(
                UserId.newId(),
                ActionType.CREATE,
                ResourceType.ORGANIZATION,
                "org-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        AuditLog loginLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(createLog.isDataModificationAction()).isTrue();
        assertThat(loginLog.isDataModificationAction()).isFalse();
    }

    @Test
    @DisplayName("isAuthenticationAction()으로 인증 관련 액션인지 확인할 수 있다")
    void testIsAuthenticationAction() {
        // given
        AuditLog loginLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        AuditLog createLog = AuditLog.create(
                UserId.newId(),
                ActionType.CREATE,
                ResourceType.ORGANIZATION,
                "org-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(loginLog.isAuthenticationAction()).isTrue();
        assertThat(createLog.isAuthenticationAction()).isFalse();
    }

    @Test
    @DisplayName("isUserResource()로 사용자 리소스 관련 로그인지 확인할 수 있다")
    void testIsUserResource() {
        // given
        AuditLog userLog = AuditLog.create(
                UserId.newId(),
                ActionType.UPDATE,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        AuditLog orgLog = AuditLog.create(
                UserId.newId(),
                ActionType.CREATE,
                ResourceType.ORGANIZATION,
                "org-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(userLog.isUserResource()).isTrue();
        assertThat(orgLog.isUserResource()).isFalse();
    }

    @Test
    @DisplayName("isMobileRequest()로 모바일 요청인지 확인할 수 있다")
    void testIsMobileRequest() {
        // given
        AuditLog mobileLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)")
        );

        AuditLog desktopLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        );

        // when & then
        assertThat(mobileLog.isMobileRequest()).isTrue();
        assertThat(desktopLog.isMobileRequest()).isFalse();
    }

    @Test
    @DisplayName("hasResourceId()로 리소스 ID 존재 여부를 확인할 수 있다")
    void testHasResourceId() {
        // given
        AuditLog withResourceId = AuditLog.create(
                UserId.newId(),
                ActionType.UPDATE,
                ResourceType.USER,
                "user-id",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        AuditLog withoutResourceId = AuditLog.create(
                UserId.newId(),
                ActionType.LOGOUT,
                ResourceType.USER,
                null,
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        AuditLog withBlankResourceId = AuditLog.create(
                UserId.newId(),
                ActionType.UPDATE,
                ResourceType.USER,
                "   ",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(withResourceId.hasResourceId()).isTrue();
        assertThat(withoutResourceId.hasResourceId()).isFalse();
        assertThat(withBlankResourceId.hasResourceId()).isFalse();
    }

    @Test
    @DisplayName("같은 ID를 가진 AuditLog는 동등하다")
    void testEquality() {
        // given
        AuditLogId id = AuditLogId.newId();
        UserId userId = UserId.newId();
        Instant occurredAt = Instant.now();

        AuditLog auditLog1 = AuditLog.reconstruct(
                id, userId, ActionType.LOGIN, ResourceType.USER, "resource-id",
                IpAddress.of("192.168.0.1"), UserAgent.of("Mozilla/5.0"), occurredAt
        );

        AuditLog auditLog2 = AuditLog.reconstruct(
                id, userId, ActionType.LOGIN, ResourceType.USER, "resource-id",
                IpAddress.of("192.168.0.1"), UserAgent.of("Mozilla/5.0"), occurredAt
        );

        // when & then
        assertThat(auditLog1).isEqualTo(auditLog2);
        assertThat(auditLog1.hashCode()).isEqualTo(auditLog2.hashCode());
    }

    @Test
    @DisplayName("다른 ID를 가진 AuditLog는 동등하지 않다")
    void testInequality() {
        // given
        UserId userId = UserId.newId();
        AuditLog auditLog1 = AuditLog.create(
                userId, ActionType.LOGIN, ResourceType.USER, "resource-id",
                IpAddress.of("192.168.0.1"), UserAgent.of("Mozilla/5.0")
        );

        AuditLog auditLog2 = AuditLog.create(
                userId, ActionType.LOGIN, ResourceType.USER, "resource-id",
                IpAddress.of("192.168.0.1"), UserAgent.of("Mozilla/5.0")
        );

        // when & then
        assertThat(auditLog1).isNotEqualTo(auditLog2);
    }

    @Test
    @DisplayName("Immutability - AuditLog은 생성 후 변경할 수 없다")
    void testImmutability() {
        // given
        UserId userId = UserId.newId();
        ActionType actionType = ActionType.LOGIN;
        ResourceType resourceType = ResourceType.USER;
        String resourceId = "user-123";
        IpAddress ipAddress = IpAddress.of("192.168.0.1");
        UserAgent userAgent = UserAgent.of("Mozilla/5.0");

        // when
        AuditLog auditLog = AuditLog.create(userId, actionType, resourceType, resourceId, ipAddress, userAgent);

        // then
        // AuditLog의 모든 필드는 final이므로 변경 메서드가 존재하지 않음
        assertThat(auditLog.getId()).isNotNull();
        assertThat(auditLog.getUserId()).isEqualTo(userId);
        assertThat(auditLog.getActionType()).isEqualTo(actionType);
        assertThat(auditLog.getResourceType()).isEqualTo(resourceType);
        assertThat(auditLog.getResourceId()).isEqualTo(resourceId);
        assertThat(auditLog.getIpAddress()).isEqualTo(ipAddress);
        assertThat(auditLog.getUserAgent()).isEqualTo(userAgent);
        assertThat(auditLog.getOccurredAt()).isNotNull();

        // 다시 조회해도 같은 값
        assertThat(auditLog.getId()).isNotNull();
        assertThat(auditLog.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("toString()은 모든 필드 정보를 포함한다")
    void testToString() {
        // given
        AuditLog auditLog = AuditLog.create(
                UserId.newId(),
                ActionType.LOGIN,
                ResourceType.USER,
                "user-123",
                IpAddress.of("192.168.0.1"),
                UserAgent.of("Mozilla/5.0")
        );

        // when
        String result = auditLog.toString();

        // then
        assertThat(result).contains("AuditLog{");
        assertThat(result).contains("id=");
        assertThat(result).contains("userId=");
        assertThat(result).contains("actionType=LOGIN");
        assertThat(result).contains("resourceType=USER");
        assertThat(result).contains("resourceId='user-123'");
        assertThat(result).contains("ipAddress=");
        assertThat(result).contains("userAgent=");
        assertThat(result).contains("occurredAt=");
    }
}
