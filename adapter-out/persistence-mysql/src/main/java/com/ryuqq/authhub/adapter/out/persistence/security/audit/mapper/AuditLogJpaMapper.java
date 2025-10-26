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
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Domain의 AuditLog와 JPA의 AuditLogJpaEntity 간 변환을 담당하는 Mapper.
 *
 * <p>Persistence Adapter에서 Domain Layer와 Persistence Layer 간의 데이터 변환을 수행합니다.
 * Anti-Corruption Layer 역할을 하며, Domain 모델의 순수성을 보호합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain AuditLog → JPA AuditLogJpaEntity 변환 (영속화 시)</li>
 *   <li>JPA AuditLogJpaEntity → Domain AuditLog 변환 (조회 시)</li>
 *   <li>Domain Value Object ↔ JPA 기본 타입 매핑</li>
 *   <li>Domain Enum ↔ JPA Enum 매핑</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Law of Demeter 준수 - Domain 객체의 행위 메서드 활용 (getter chaining 금지)</li>
 *   <li>✅ Null 안전성 - Objects.requireNonNull로 null 체크</li>
 *   <li>✅ 불변성 보장 - Domain 재구성 시 reconstruct() 팩토리 메서드 사용</li>
 * </ul>
 *
 * <p><strong>매핑 전략:</strong></p>
 * <ul>
 *   <li>AuditLogId (UUID) ↔ String</li>
 *   <li>UserId (UUID) ↔ String</li>
 *   <li>ActionType (Domain Enum) ↔ ActionTypeEnum (JPA Enum)</li>
 *   <li>ResourceType (Domain Enum) ↔ ResourceTypeEnum (JPA Enum)</li>
 *   <li>IpAddress (VO) ↔ String</li>
 *   <li>UserAgent (VO) ↔ String</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class AuditLogJpaMapper {

    /**
     * Domain의 AuditLog를 JPA의 AuditLogJpaEntity로 변환합니다.
     *
     * <p>AuditLog Aggregate를 데이터베이스에 영속화하기 위해 JPA Entity로 변환합니다.
     * Law of Demeter를 준수하여 Domain 객체의 행위 메서드를 직접 호출합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>auditLog.getIdAsString() → AuditLogJpaEntity.auditLogId</li>
     *   <li>auditLog.getUserIdAsString() → AuditLogJpaEntity.userId</li>
     *   <li>auditLog.getActionType() → ActionTypeEnum</li>
     *   <li>auditLog.getResourceType() → ResourceTypeEnum</li>
     *   <li>auditLog.getIpAddressAsString() → AuditLogJpaEntity.ipAddress</li>
     *   <li>auditLog.getUserAgentAsString() → AuditLogJpaEntity.userAgent</li>
     * </ul>
     *
     * @param auditLog Domain AuditLog (null 불가)
     * @return AuditLogJpaEntity
     * @throws NullPointerException auditLog이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuditLogJpaEntity toEntity(final AuditLog auditLog) {
        Objects.requireNonNull(auditLog, "AuditLog cannot be null");

        return AuditLogJpaEntity.of(
                auditLog.getIdAsString(),
                auditLog.getUserIdAsString(),
                mapToActionTypeEnum(auditLog.getActionType()),
                mapToResourceTypeEnum(auditLog.getResourceType()),
                auditLog.getResourceId(),
                auditLog.getIpAddressAsString(),
                auditLog.getUserAgentAsString(),
                auditLog.getOccurredAt()
        );
    }

    /**
     * JPA의 AuditLogJpaEntity를 Domain의 AuditLog로 변환합니다.
     *
     * <p>데이터베이스에서 조회한 JPA Entity를 Domain Aggregate로 재구성합니다.
     * AuditLog.reconstruct() 팩토리 메서드를 사용하여 불변 객체를 생성합니다.</p>
     *
     * <p><strong>매핑 규칙:</strong></p>
     * <ul>
     *   <li>AuditLogJpaEntity.auditLogId → AuditLogId</li>
     *   <li>AuditLogJpaEntity.userId → UserId</li>
     *   <li>ActionTypeEnum → ActionType</li>
     *   <li>ResourceTypeEnum → ResourceType</li>
     *   <li>AuditLogJpaEntity.ipAddress → IpAddress</li>
     *   <li>AuditLogJpaEntity.userAgent → UserAgent</li>
     * </ul>
     *
     * @param entity AuditLogJpaEntity (null 불가)
     * @return Domain AuditLog
     * @throws NullPointerException entity가 null인 경우
     * @throws IllegalArgumentException entity의 필드가 유효하지 않은 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuditLog toDomain(final AuditLogJpaEntity entity) {
        Objects.requireNonNull(entity, "AuditLogJpaEntity cannot be null");

        return AuditLog.reconstruct(
                AuditLogId.fromString(entity.getAuditLogId()),
                UserId.fromString(entity.getUserId()),
                mapToActionType(entity.getActionType()),
                mapToResourceType(entity.getResourceType()),
                entity.getResourceId(),
                IpAddress.of(entity.getIpAddress()),
                UserAgent.of(entity.getUserAgent()),
                entity.getOccurredAt()
        );
    }

    /**
     * Domain의 ActionType을 JPA의 ActionTypeEnum으로 변환합니다.
     *
     * @param actionType Domain ActionType (null 불가)
     * @return ActionTypeEnum
     * @throws NullPointerException actionType이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ActionTypeEnum mapToActionTypeEnum(final ActionType actionType) {
        Objects.requireNonNull(actionType, "ActionType cannot be null");

        return switch (actionType) {
            case LOGIN -> ActionTypeEnum.LOGIN;
            case LOGOUT -> ActionTypeEnum.LOGOUT;
            case CREATE -> ActionTypeEnum.CREATE;
            case UPDATE -> ActionTypeEnum.UPDATE;
            case DELETE -> ActionTypeEnum.DELETE;
        };
    }

    /**
     * JPA의 ActionTypeEnum을 Domain의 ActionType으로 변환합니다.
     *
     * @param actionTypeEnum JPA ActionTypeEnum (null 불가)
     * @return Domain ActionType
     * @throws NullPointerException actionTypeEnum이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ActionType mapToActionType(final ActionTypeEnum actionTypeEnum) {
        Objects.requireNonNull(actionTypeEnum, "ActionTypeEnum cannot be null");

        return switch (actionTypeEnum) {
            case LOGIN -> ActionType.LOGIN;
            case LOGOUT -> ActionType.LOGOUT;
            case CREATE -> ActionType.CREATE;
            case UPDATE -> ActionType.UPDATE;
            case DELETE -> ActionType.DELETE;
        };
    }

    /**
     * Domain의 ResourceType을 JPA의 ResourceTypeEnum으로 변환합니다.
     *
     * @param resourceType Domain ResourceType (null 불가)
     * @return ResourceTypeEnum
     * @throws NullPointerException resourceType이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ResourceTypeEnum mapToResourceTypeEnum(final ResourceType resourceType) {
        Objects.requireNonNull(resourceType, "ResourceType cannot be null");

        return switch (resourceType) {
            case USER -> ResourceTypeEnum.USER;
            case ORGANIZATION -> ResourceTypeEnum.ORGANIZATION;
            case COMPANY -> ResourceTypeEnum.COMPANY;
        };
    }

    /**
     * JPA의 ResourceTypeEnum을 Domain의 ResourceType으로 변환합니다.
     *
     * @param resourceTypeEnum JPA ResourceTypeEnum (null 불가)
     * @return Domain ResourceType
     * @throws NullPointerException resourceTypeEnum이 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    private ResourceType mapToResourceType(final ResourceTypeEnum resourceTypeEnum) {
        Objects.requireNonNull(resourceTypeEnum, "ResourceTypeEnum cannot be null");

        return switch (resourceTypeEnum) {
            case USER -> ResourceType.USER;
            case ORGANIZATION -> ResourceType.ORGANIZATION;
            case COMPANY -> ResourceType.COMPANY;
        };
    }
}
