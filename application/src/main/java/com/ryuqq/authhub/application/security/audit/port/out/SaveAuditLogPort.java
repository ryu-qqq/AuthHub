package com.ryuqq.authhub.application.security.audit.port.out;

import com.ryuqq.authhub.domain.security.audit.AuditLog;

/**
 * 감사 로그 저장 Port (Port Out).
 *
 * <p>JPA를 통해 감사 로그를 영속화하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>AuditLog Domain 객체를 JPA Entity로 변환하여 저장</li>
 *   <li>비동기 저장 지원 (@Async 호출 가능)</li>
 *   <li>저장 실패 시 예외 전파 (하지만 비동기로 호출되어 요청에 영향 없음)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 명령 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - 저장 연산은 트랜잭션 내에서 처리</li>
 *   <li>✅ 비동기 처리 - @Async로 저장 실패가 요청에 영향 없음</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>AuditLogPersistenceAdapter (Port 구현체)</li>
 *   <li>AuditLogJpaRepository (Spring Data JPA)</li>
 *   <li>AuditLogJpaEntity (JPA 엔티티)</li>
 * </ul>
 *
 * <p><strong>JPA Entity 구조:</strong></p>
 * <pre>
 * @Entity
 * @Table(name = "audit_logs", indexes = {
 *     @Index(name = "idx_user_id", columnList = "user_id"),
 *     @Index(name = "idx_occurred_at", columnList = "occurred_at"),
 *     @Index(name = "idx_action_type", columnList = "action_type")
 * })
 * public class AuditLogJpaEntity { ... }
 * </pre>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // AuditLogService에서 비동기 호출
 * @Async
 * public void createAuditLog(CreateAuditLogUseCase.Command command) {
 *     AuditLog auditLog = AuditLog.create(...);
 *     saveAuditLogPort.save(auditLog); // 비동기로 저장
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SaveAuditLogPort {

    /**
     * 감사 로그를 저장합니다.
     *
     * <p>AuditLog Domain 객체를 AuditLogJpaEntity로 변환하여 JPA를 통해 저장합니다.
     * 비동기로 호출되어 저장 실패가 요청에 영향을 주지 않습니다.</p>
     *
     * @param auditLog 저장할 감사 로그
     * @throws IllegalArgumentException auditLog가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void save(AuditLog auditLog);

    /**
     * 여러 감사 로그를 일괄 저장합니다.
     *
     * <p>대량의 감사 로그를 효율적으로 저장할 때 사용됩니다.
     * Spring Data JPA의 saveAll()을 활용하여 배치 처리합니다.</p>
     *
     * @param auditLogs 저장할 감사 로그 목록
     * @throws IllegalArgumentException auditLogs가 null이거나 빈 리스트인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void saveAll(Iterable<AuditLog> auditLogs);
}
