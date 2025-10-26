package com.ryuqq.authhub.adapter.out.persistence.security.audit.repository;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ActionTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ResourceTypeEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

/**
 * AuditLogJpaEntity를 위한 JPA Specification 빌더.
 *
 * <p>동적 쿼리 구성을 위한 Specification 패턴을 활용합니다.
 * 각 메서드는 하나의 조건을 나타내며, and/or로 조합하여 복잡한 쿼리를 구성할 수 있습니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>감사 로그 검색 조건별 Specification 생성</li>
 *   <li>사용자 ID, 액션 타입, 리소스 타입, 발생 시각 등 필터링 지원</li>
 *   <li>페이징 및 정렬과 함께 사용 가능</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Null 안전성 - null인 경우 항상 true 반환 (조건 제외)</li>
 *   <li>✅ JPA Criteria API - Type-safe 쿼리 구성</li>
 *   <li>✅ 메서드 체이닝 - Specification.where().and() 패턴</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 동적 쿼리 구성
 * Specification<AuditLogJpaEntity> spec = Specification.where(null);
 *
 * if (userId != null) {
 *     spec = spec.and(AuditLogSpecifications.hasUserId(userId));
 * }
 * if (actionType != null) {
 *     spec = spec.and(AuditLogSpecifications.hasActionType(actionType));
 * }
 * if (startDate != null && endDate != null) {
 *     spec = spec.and(AuditLogSpecifications.occurredBetween(startDate, endDate));
 * }
 *
 * // 페이징과 함께 사용
 * Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "occurredAt"));
 * Page<AuditLogJpaEntity> result = repository.findAll(spec, pageable);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public final class AuditLogSpecifications {

    /**
     * Private 생성자 (유틸리티 클래스).
     */
    private AuditLogSpecifications() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 사용자 ID로 필터링하는 Specification을 생성합니다.
     *
     * <p>userId가 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.userId = :userId
     * </pre>
     *
     * @param userId 사용자 ID (nullable)
     * @return Specification (userId가 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasUserId(final String userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    /**
     * 액션 타입으로 필터링하는 Specification을 생성합니다.
     *
     * <p>actionType이 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.actionType = :actionType
     * </pre>
     *
     * @param actionType 액션 타입 (nullable)
     * @return Specification (actionType이 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasActionType(final ActionTypeEnum actionType) {
        return (root, query, criteriaBuilder) -> {
            if (actionType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("actionType"), actionType);
        };
    }

    /**
     * 리소스 타입으로 필터링하는 Specification을 생성합니다.
     *
     * <p>resourceType이 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.resourceType = :resourceType
     * </pre>
     *
     * @param resourceType 리소스 타입 (nullable)
     * @return Specification (resourceType이 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasResourceType(final ResourceTypeEnum resourceType) {
        return (root, query, criteriaBuilder) -> {
            if (resourceType == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("resourceType"), resourceType);
        };
    }

    /**
     * 리소스 ID로 필터링하는 Specification을 생성합니다.
     *
     * <p>resourceId가 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.resourceId = :resourceId
     * </pre>
     *
     * @param resourceId 리소스 ID (nullable)
     * @return Specification (resourceId가 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasResourceId(final String resourceId) {
        return (root, query, criteriaBuilder) -> {
            if (resourceId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("resourceId"), resourceId);
        };
    }

    /**
     * IP 주소로 필터링하는 Specification을 생성합니다.
     *
     * <p>ipAddress가 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.ipAddress = :ipAddress
     * </pre>
     *
     * @param ipAddress IP 주소 (nullable)
     * @return Specification (ipAddress가 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasIpAddress(final String ipAddress) {
        return (root, query, criteriaBuilder) -> {
            if (ipAddress == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("ipAddress"), ipAddress);
        };
    }

    /**
     * 발생 시각이 특정 시점 이후인 감사 로그를 필터링하는 Specification을 생성합니다.
     *
     * <p>startDate가 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.occurredAt >= :startDate
     * </pre>
     *
     * @param startDate 시작 시각 (nullable, inclusive)
     * @return Specification (startDate가 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> occurredAfter(final Instant startDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("occurredAt"), startDate);
        };
    }

    /**
     * 발생 시각이 특정 시점 이전인 감사 로그를 필터링하는 Specification을 생성합니다.
     *
     * <p>endDate가 null이면 항상 true를 반환하여 조건에서 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.occurredAt <= :endDate
     * </pre>
     *
     * @param endDate 종료 시각 (nullable, inclusive)
     * @return Specification (endDate가 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> occurredBefore(final Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (endDate == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("occurredAt"), endDate);
        };
    }

    /**
     * 발생 시각이 특정 기간 내인 감사 로그를 필터링하는 Specification을 생성합니다.
     *
     * <p>startDate 또는 endDate가 null이면 해당 조건은 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.occurredAt BETWEEN :startDate AND :endDate
     * </pre>
     *
     * @param startDate 시작 시각 (nullable, inclusive)
     * @param endDate 종료 시각 (nullable, inclusive)
     * @return Specification (양쪽 모두 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> occurredBetween(final Instant startDate, final Instant endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("occurredAt"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("occurredAt"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("occurredAt"), endDate);
        };
    }

    /**
     * 리소스 타입과 리소스 ID를 동시에 필터링하는 Specification을 생성합니다.
     *
     * <p>resourceType 또는 resourceId가 null이면 해당 조건은 제외됩니다.</p>
     *
     * <p><strong>생성되는 쿼리:</strong></p>
     * <pre>
     * WHERE a.resourceType = :resourceType AND a.resourceId = :resourceId
     * </pre>
     *
     * @param resourceType 리소스 타입 (nullable)
     * @param resourceId 리소스 ID (nullable)
     * @return Specification (양쪽 모두 null이면 항상 true)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public static Specification<AuditLogJpaEntity> hasResource(final ResourceTypeEnum resourceType, final String resourceId) {
        return (root, query, criteriaBuilder) -> {
            if (resourceType == null && resourceId == null) {
                return criteriaBuilder.conjunction();
            }
            if (resourceType != null && resourceId != null) {
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("resourceType"), resourceType),
                        criteriaBuilder.equal(root.get("resourceId"), resourceId)
                );
            }
            if (resourceType != null) {
                return criteriaBuilder.equal(root.get("resourceType"), resourceType);
            }
            return criteriaBuilder.equal(root.get("resourceId"), resourceId);
        };
    }
}
