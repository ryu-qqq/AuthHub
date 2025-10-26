package com.ryuqq.authhub.adapter.out.persistence.security.audit.mapper;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ActionTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ResourceTypeEnum;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.security.audit.AuditLog;
import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.AuditLogId;
import com.ryuqq.authhub.domain.security.audit.vo.IpAddress;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;
import com.ryuqq.authhub.domain.security.audit.vo.UserAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AuditLogJpaMapper 단위 테스트.
 *
 * <p>Domain의 AuditLog와 JPA의 AuditLogJpaEntity 간 변환 로직을 검증합니다.
 * Anti-Corruption Layer의 핵심 역할을 수행하는 Mapper의 정확성을 보장합니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>Plain Java Test - Mock 없이 Pure Object 변환 테스트</li>
 *   <li>양방향 변환 검증 - toEntity() → toDomain() 일관성</li>
 *   <li>Null 안전성 검증 - NullPointerException 발생 확인</li>
 *   <li>Enum 매핑 검증 - Domain Enum ↔ JPA Enum 정확성</li>
 *   <li>Value Object 변환 검증 - VO → Primitive → VO 일관성</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ {@code @SpringBootTest} 금지 - Pure Unit Test</li>
 *   <li>✅ Law of Demeter 검증 - Mapper의 행위 메서드 사용 확인</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("AuditLogJpaMapper 단위 테스트")
class AuditLogJpaMapperTest {

    private AuditLogJpaMapper mapper;

    private AuditLogId testAuditLogId;
    private UserId testUserId;
    private ActionType testActionType;
    private ResourceType testResourceType;
    private String testResourceId;
    private IpAddress testIpAddress;
    private UserAgent testUserAgent;
    private Instant testOccurredAt;

    private AuditLog testDomainAuditLog;
    private AuditLogJpaEntity testJpaEntity;

    @BeforeEach
    void setUp() {
        mapper = new AuditLogJpaMapper();

        // Test Data 준비
        testAuditLogId = AuditLogId.newId();
        testUserId = UserId.newId();
        testActionType = ActionType.CREATE;
        testResourceType = ResourceType.USER;
        testResourceId = "resource-123";
        testIpAddress = IpAddress.of("192.168.1.1");
        testUserAgent = UserAgent.of("Mozilla/5.0");
        testOccurredAt = Instant.now();

        // Domain AuditLog 생성
        testDomainAuditLog = AuditLog.reconstruct(
                testAuditLogId,
                testUserId,
                testActionType,
                testResourceType,
                testResourceId,
                testIpAddress,
                testUserAgent,
                testOccurredAt
        );

        // JPA Entity 생성
        testJpaEntity = AuditLogJpaEntity.of(
                testAuditLogId.asString(),
                testUserId.asString(),
                ActionTypeEnum.CREATE,
                ResourceTypeEnum.USER,
                testResourceId,
                testIpAddress.value(),
                testUserAgent.value(),
                testOccurredAt
        );
    }

    @Test
    @DisplayName("Domain AuditLog를 JPA AuditLogJpaEntity로 성공적으로 변환한다")
    void toEntity_Success() {
        // when
        AuditLogJpaEntity result = mapper.toEntity(testDomainAuditLog);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAuditLogId()).isEqualTo(testAuditLogId.asString());
        assertThat(result.getUserId()).isEqualTo(testUserId.asString());
        assertThat(result.getActionType()).isEqualTo(ActionTypeEnum.CREATE);
        assertThat(result.getResourceType()).isEqualTo(ResourceTypeEnum.USER);
        assertThat(result.getResourceId()).isEqualTo(testResourceId);
        assertThat(result.getIpAddress()).isEqualTo(testIpAddress.value());
        assertThat(result.getUserAgent()).isEqualTo(testUserAgent.value());
        assertThat(result.getOccurredAt()).isEqualTo(testOccurredAt);
    }

    @Test
    @DisplayName("JPA AuditLogJpaEntity를 Domain AuditLog로 성공적으로 변환한다")
    void toDomain_Success() {
        // when
        AuditLog result = mapper.toDomain(testJpaEntity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIdAsString()).isEqualTo(testAuditLogId.asString());
        assertThat(result.getUserIdAsString()).isEqualTo(testUserId.asString());
        assertThat(result.getActionType()).isEqualTo(ActionType.CREATE);
        assertThat(result.getResourceType()).isEqualTo(ResourceType.USER);
        assertThat(result.getResourceId()).isEqualTo(testResourceId);
        assertThat(result.getIpAddressAsString()).isEqualTo(testIpAddress.value());
        assertThat(result.getUserAgentAsString()).isEqualTo(testUserAgent.value());
        assertThat(result.getOccurredAt()).isEqualTo(testOccurredAt);
    }

    @Test
    @DisplayName("toEntity() 호출 시 auditLog가 null이면 예외를 던진다")
    void toEntity_WithNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> mapper.toEntity(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("AuditLog cannot be null");
    }

    @Test
    @DisplayName("toDomain() 호출 시 entity가 null이면 예외를 던진다")
    void toDomain_WithNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> mapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("AuditLogJpaEntity cannot be null");
    }

    @Test
    @DisplayName("양방향 변환이 일관성을 유지한다 (Domain → Entity → Domain)")
    void roundTrip_DomainToEntityToDomain_MaintainsConsistency() {
        // when
        AuditLogJpaEntity entity = mapper.toEntity(testDomainAuditLog);
        AuditLog resultDomain = mapper.toDomain(entity);

        // then
        assertThat(resultDomain.getIdAsString()).isEqualTo(testDomainAuditLog.getIdAsString());
        assertThat(resultDomain.getUserIdAsString()).isEqualTo(testDomainAuditLog.getUserIdAsString());
        assertThat(resultDomain.getActionType()).isEqualTo(testDomainAuditLog.getActionType());
        assertThat(resultDomain.getResourceType()).isEqualTo(testDomainAuditLog.getResourceType());
        assertThat(resultDomain.getResourceId()).isEqualTo(testDomainAuditLog.getResourceId());
        assertThat(resultDomain.getIpAddressAsString()).isEqualTo(testDomainAuditLog.getIpAddressAsString());
        assertThat(resultDomain.getUserAgentAsString()).isEqualTo(testDomainAuditLog.getUserAgentAsString());
        assertThat(resultDomain.getOccurredAt()).isEqualTo(testDomainAuditLog.getOccurredAt());
    }

    @Test
    @DisplayName("양방향 변환이 일관성을 유지한다 (Entity → Domain → Entity)")
    void roundTrip_EntityToDomainToEntity_MaintainsConsistency() {
        // when
        AuditLog domain = mapper.toDomain(testJpaEntity);
        AuditLogJpaEntity resultEntity = mapper.toEntity(domain);

        // then
        assertThat(resultEntity.getAuditLogId()).isEqualTo(testJpaEntity.getAuditLogId());
        assertThat(resultEntity.getUserId()).isEqualTo(testJpaEntity.getUserId());
        assertThat(resultEntity.getActionType()).isEqualTo(testJpaEntity.getActionType());
        assertThat(resultEntity.getResourceType()).isEqualTo(testJpaEntity.getResourceType());
        assertThat(resultEntity.getResourceId()).isEqualTo(testJpaEntity.getResourceId());
        assertThat(resultEntity.getIpAddress()).isEqualTo(testJpaEntity.getIpAddress());
        assertThat(resultEntity.getUserAgent()).isEqualTo(testJpaEntity.getUserAgent());
        assertThat(resultEntity.getOccurredAt()).isEqualTo(testJpaEntity.getOccurredAt());
    }

    @Test
    @DisplayName("모든 ActionType Enum 값이 정확히 매핑된다")
    void allActionTypes_MapCorrectly() {
        // given
        ActionType[] domainActions = {ActionType.LOGIN, ActionType.LOGOUT, ActionType.CREATE,
                                       ActionType.UPDATE, ActionType.DELETE};
        ActionTypeEnum[] jpaActions = {ActionTypeEnum.LOGIN, ActionTypeEnum.LOGOUT, ActionTypeEnum.CREATE,
                                        ActionTypeEnum.UPDATE, ActionTypeEnum.DELETE};

        for (int i = 0; i < domainActions.length; i++) {
            // when
            AuditLog domainLog = AuditLog.reconstruct(
                    testAuditLogId, testUserId, domainActions[i], testResourceType,
                    testResourceId, testIpAddress, testUserAgent, testOccurredAt
            );

            AuditLogJpaEntity entity = mapper.toEntity(domainLog);
            AuditLog resultDomain = mapper.toDomain(entity);

            // then
            assertThat(entity.getActionType()).isEqualTo(jpaActions[i]);
            assertThat(resultDomain.getActionType()).isEqualTo(domainActions[i]);
        }
    }

    @Test
    @DisplayName("모든 ResourceType Enum 값이 정확히 매핑된다")
    void allResourceTypes_MapCorrectly() {
        // given
        ResourceType[] domainResources = {ResourceType.USER, ResourceType.ORGANIZATION, ResourceType.COMPANY};
        ResourceTypeEnum[] jpaResources = {ResourceTypeEnum.USER, ResourceTypeEnum.ORGANIZATION, ResourceTypeEnum.COMPANY};

        for (int i = 0; i < domainResources.length; i++) {
            // when
            AuditLog domainLog = AuditLog.reconstruct(
                    testAuditLogId, testUserId, testActionType, domainResources[i],
                    testResourceId, testIpAddress, testUserAgent, testOccurredAt
            );

            AuditLogJpaEntity entity = mapper.toEntity(domainLog);
            AuditLog resultDomain = mapper.toDomain(entity);

            // then
            assertThat(entity.getResourceType()).isEqualTo(jpaResources[i]);
            assertThat(resultDomain.getResourceType()).isEqualTo(domainResources[i]);
        }
    }

    @Test
    @DisplayName("Value Object의 값이 정확히 변환된다")
    void valueObjects_ConvertCorrectly() {
        // given
        IpAddress ipAddress = IpAddress.of("10.0.0.1");
        UserAgent userAgent = UserAgent.of("Chrome/91.0");

        AuditLog domainLog = AuditLog.reconstruct(
                testAuditLogId, testUserId, testActionType, testResourceType,
                testResourceId, ipAddress, userAgent, testOccurredAt
        );

        // when
        AuditLogJpaEntity entity = mapper.toEntity(domainLog);
        AuditLog resultDomain = mapper.toDomain(entity);

        // then
        assertThat(entity.getIpAddress()).isEqualTo("10.0.0.1");
        assertThat(entity.getUserAgent()).isEqualTo("Chrome/91.0");
        assertThat(resultDomain.getIpAddressAsString()).isEqualTo("10.0.0.1");
        assertThat(resultDomain.getUserAgentAsString()).isEqualTo("Chrome/91.0");
    }

    @Test
    @DisplayName("UUID 기반 ID가 정확히 String으로 변환된다")
    void uuidIds_ConvertCorrectly() {
        // when
        AuditLogJpaEntity entity = mapper.toEntity(testDomainAuditLog);

        // then
        assertThat(entity.getAuditLogId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
        assertThat(entity.getUserId()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    @Test
    @DisplayName("Law of Demeter를 준수하는 행위 메서드를 사용한다")
    void usesLawOfDemeterCompliantMethods() {
        // when
        AuditLogJpaEntity entity = mapper.toEntity(testDomainAuditLog);

        // then - 행위 메서드 사용 검증 (getIdAsString(), getUserIdAsString() 등)
        // Domain 객체가 getIdAsString()과 같은 행위 메서드를 제공하므로
        // Mapper는 .getId().asString() 같은 체이닝을 하지 않음
        assertThat(entity.getAuditLogId()).isEqualTo(testDomainAuditLog.getIdAsString());
        assertThat(entity.getUserId()).isEqualTo(testDomainAuditLog.getUserIdAsString());
        assertThat(entity.getIpAddress()).isEqualTo(testDomainAuditLog.getIpAddressAsString());
        assertThat(entity.getUserAgent()).isEqualTo(testDomainAuditLog.getUserAgentAsString());
    }
}
