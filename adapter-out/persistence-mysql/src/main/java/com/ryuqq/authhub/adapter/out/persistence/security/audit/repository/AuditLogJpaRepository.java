package com.ryuqq.authhub.adapter.out.persistence.security.audit.repository;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AuditLogJpaEntity를 위한 Spring Data JPA Repository.
 *
 * <p>AuditLog Aggregate를 위한 CRUD 및 동적 쿼리 메서드를 제공합니다.
 * JpaSpecificationExecutor를 상속하여 Specification 패턴으로 동적 쿼리를 구성할 수 있습니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>AuditLogJpaEntity 저장 및 조회</li>
 *   <li>Specification 패턴으로 동적 쿼리 지원</li>
 *   <li>페이징 및 정렬 지원</li>
 *   <li>사용자별, 리소스별, 액션별 조회 메서드 제공</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Long FK 전략 준수 - 관계 어노테이션 사용 금지</li>
 *   <li>✅ JpaSpecificationExecutor 상속 - 동적 쿼리 지원</li>
 *   <li>✅ Optional 반환 - 단건 조회 시 Optional 사용</li>
 *   <li>✅ 메서드 명명 규칙 - Spring Data JPA 쿼리 메서드 규칙 준수</li>
 * </ul>
 *
 * <p><strong>Specification 패턴 활용 예시:</strong></p>
 * <pre>
 * // 동적 쿼리 구성
 * Specification<AuditLogJpaEntity> spec = Specification.where(null);
 * if (userId != null) {
 *     spec = spec.and(hasUserId(userId));
 * }
 * if (actionType != null) {
 *     spec = spec.and(hasActionType(actionType));
 * }
 * Page<AuditLogJpaEntity> result = repository.findAll(spec, pageable);
 * </pre>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인/로그아웃 시 감사 로그 저장</li>
 *   <li>리소스 생성/수정/삭제 시 감사 로그 저장</li>
 *   <li>관리자 페이지에서 감사 로그 검색 및 조회</li>
 *   <li>사용자별 활동 이력 조회</li>
 *   <li>리소스별 변경 이력 조회</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, Long>, JpaSpecificationExecutor<AuditLogJpaEntity> {

    /**
     * auditLogId (UUID 문자열)로 AuditLogJpaEntity를 조회합니다.
     *
     * <p>Domain의 AuditLogId.asString()에 해당하는 auditLogId로 감사 로그를 찾습니다.
     * 특정 감사 로그 상세 정보를 조회할 때 사용됩니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT a FROM AuditLogJpaEntity a WHERE a.auditLogId = :auditLogId
     * </pre>
     *
     * @param auditLogId 감사 로그 고유 식별자 (UUID 문자열, null 불가)
     * @return Optional로 감싼 AuditLogJpaEntity (존재하지 않으면 Empty)
     * @throws IllegalArgumentException auditLogId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<AuditLogJpaEntity> findByAuditLogId(String auditLogId);

    /**
     * auditLogId로 AuditLogJpaEntity 존재 여부를 확인합니다.
     *
     * <p>감사 로그가 이미 존재하는지 확인할 때 사용합니다.
     * 전체 엔티티를 조회하지 않고 EXISTS 쿼리로 빠르게 확인합니다.</p>
     *
     * <p><strong>쿼리:</strong></p>
     * <pre>
     * SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
     * FROM AuditLogJpaEntity a WHERE a.auditLogId = :auditLogId
     * </pre>
     *
     * @param auditLogId 감사 로그 고유 식별자 (UUID 문자열, null 불가)
     * @return auditLogId가 존재하면 true, 아니면 false
     * @throws IllegalArgumentException auditLogId가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByAuditLogId(String auditLogId);
}
