package com.ryuqq.authhub.domain.identity.organization.event;

import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationId;
import com.ryuqq.authhub.domain.identity.organization.vo.OrganizationType;

import java.time.Instant;
import java.util.Objects;

/**
 * 조직 생성 도메인 이벤트.
 *
 * <p>Organization Aggregate의 생성 시 발행되는 도메인 이벤트입니다.
 * 조직 생성에 따른 후속 처리(기본 권한 설정, 알림 발송, 감사 로그 등)를 비동기로 처리하기 위해 사용됩니다.</p>
 *
 * <p><strong>이벤트 발행 시점:</strong></p>
 * <ul>
 *   <li>Organization.createSeller() 호출 시 (SELLER 타입 조직 생성)</li>
 *   <li>Organization.createCompany() 호출 시 (COMPANY 타입 조직 생성)</li>
 * </ul>
 *
 * <p><strong>이벤트 처리 흐름:</strong></p>
 * <pre>
 * 1. Organization.createXXX() 호출
 * 2. registerEvent(OrganizationCreatedEvent) 호출
 * 3. Repository.save() 호출
 * 4. 트랜잭션 커밋 시 Spring Data가 Event 자동 발행
 * 5. @EventListener 또는 @TransactionalEventListener가 이벤트 수신
 * 6. 후속 처리 실행:
 *    - 조직 소유자에게 OWNER 권한 부여
 *    - 환영 이메일 발송
 *    - 감사 로그 기록
 *    - 통계 업데이트
 * </pre>
 *
 * <p><strong>Enterprise Pattern 준수:</strong></p>
 * <ul>
 *   <li>✅ AbstractAggregateRoot.registerEvent() 사용</li>
 *   <li>✅ 트랜잭션 커밋 시 자동 발행 (즉시 발행 아님)</li>
 *   <li>✅ Repository.save() 호출 시 Spring Data가 이벤트 발행</li>
 *   <li>✅ 비동기 처리 가능 (@Async 결합)</li>
 *   <li>✅ 도메인 로직과 후속 처리 분리 (SRP)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 미사용 - Java 21 Record 사용</li>
 *   <li>✅ 불변성 보장 - Record의 본질적 불변성</li>
 *   <li>✅ Javadoc 완비</li>
 * </ul>
 *
 * @param organizationId 생성된 조직의 식별자 (null 불가)
 * @param ownerId 조직 소유자의 사용자 식별자 (null 불가)
 * @param organizationType 조직 타입 (SELLER 또는 COMPANY, null 불가)
 * @param occurredAt 이벤트 발생 시각 (null 불가)
 * @author AuthHub Team
 * @since 1.0.0
 */
public record OrganizationCreatedEvent(
        OrganizationId organizationId,
        UserId ownerId,
        OrganizationType organizationType,
        Instant occurredAt
) {

    /**
     * Compact constructor - 필드 유효성 검증을 수행합니다.
     *
     * @throws NullPointerException 필드 중 하나라도 null인 경우
     */
    public OrganizationCreatedEvent {
        Objects.requireNonNull(organizationId, "OrganizationId cannot be null");
        Objects.requireNonNull(ownerId, "OwnerId cannot be null");
        Objects.requireNonNull(organizationType, "OrganizationType cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    /**
     * OrganizationCreatedEvent를 생성합니다.
     * 이벤트 발생 시각은 현재 시각으로 자동 설정됩니다.
     *
     * @param organizationId 생성된 조직의 식별자 (null 불가)
     * @param ownerId 조직 소유자의 사용자 식별자 (null 불가)
     * @param organizationType 조직 타입 (null 불가)
     * @return OrganizationCreatedEvent 인스턴스
     * @throws NullPointerException 파라미터 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public OrganizationCreatedEvent(
            final OrganizationId organizationId,
            final UserId ownerId,
            final OrganizationType organizationType
    ) {
        this(organizationId, ownerId, organizationType, Instant.now());
    }

    /**
     * 조직 식별자를 문자열로 반환합니다.
     *
     * @return 조직 식별자 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getOrganizationIdAsString() {
        return this.organizationId.asString();
    }

    /**
     * 소유자 식별자를 문자열로 반환합니다.
     *
     * @return 소유자 식별자 문자열
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getOwnerIdAsString() {
        return this.ownerId.asString();
    }

    /**
     * 조직 타입이 SELLER인지 확인합니다.
     *
     * @return SELLER 타입이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isSeller() {
        return this.organizationType == OrganizationType.SELLER;
    }

    /**
     * 조직 타입이 COMPANY인지 확인합니다.
     *
     * @return COMPANY 타입이면 true, 그렇지 않으면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    public boolean isCompany() {
        return this.organizationType == OrganizationType.COMPANY;
    }

    /**
     * 이벤트 발생 시각을 문자열로 반환합니다.
     *
     * @return 이벤트 발생 시각 문자열 (ISO-8601 형식)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public String getOccurredAtAsString() {
        return this.occurredAt.toString();
    }
}
