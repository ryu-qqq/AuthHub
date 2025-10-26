package com.ryuqq.authhub.application.security.audit.service;

import com.ryuqq.authhub.application.security.audit.assembler.AuditLogAssembler;
import com.ryuqq.authhub.application.security.audit.port.in.CreateAuditLogUseCase;
import com.ryuqq.authhub.application.security.audit.port.out.SaveAuditLogPort;
import com.ryuqq.authhub.domain.security.audit.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * AuditLog Service - 감사 로그 생성 UseCase 구현체 (Command Service).
 *
 * <p>비동기 감사 로그 저장 시스템의 핵심 비즈니스 로직을 담당하는 Command Service입니다.
 * AuditLog Aggregate를 생성하고, JPA Port를 통해 영속화합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li><strong>Create Audit Log</strong>:
 *     <ul>
 *       <li>Command 유효성 검증</li>
 *       <li>Assembler를 통해 AuditLog Domain 객체 생성</li>
 *       <li>JPA를 통한 비동기 저장 (SaveAuditLogPort)</li>
 *       <li>저장 실패 시 예외 처리 (요청에 영향 없음)</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Transaction 경계 - 저장 연산은 트랜잭션 밖에서 비동기 처리</li>
 *   <li>✅ @Async 사용 - 저장 실패가 요청에 영향 없음</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - 의존성 역전 원칙 준수</li>
 *   <li>✅ Command/Query 분리 - Command 전용 (Create)</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>비동기 처리 설계:</strong></p>
 * <ul>
 *   <li>@Async 사용 - Spring의 비동기 처리 활용</li>
 *   <li>저장 실패 시 요청에 영향 없음 - 예외 무시</li>
 *   <li>성능 최적화 - 메인 요청 흐름과 분리</li>
 *   <li>별도 Thread Pool 사용 - 리소스 격리</li>
 * </ul>
 *
 * <p><strong>트랜잭션 설계:</strong></p>
 * <ul>
 *   <li>@Transactional 없음 - 비동기 메서드는 별도 트랜잭션 관리 필요</li>
 *   <li>SaveAuditLogPort 구현체에서 @Transactional 처리</li>
 *   <li>비동기 + 트랜잭션 분리 - Spring 프록시 제약 회피</li>
 * </ul>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>SaveAuditLogPort - JPA 저장</li>
 *   <li>AuditLogAssembler - Command → Domain 변환</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Filter 또는 Interceptor에서 호출
 * CreateAuditLogUseCase.Command command = new CreateAuditLogUseCase.Command(
 *     userId, ActionType.LOGIN, ResourceType.USER, resourceId, ipAddress, userAgent
 * );
 * auditLogService.createAuditLog(command); // 비동기 저장
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
public class AuditLogService implements CreateAuditLogUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final SaveAuditLogPort saveAuditLogPort;
    private final AuditLogAssembler auditLogAssembler;

    /**
     * AuditLogService 생성자.
     *
     * @param saveAuditLogPort JPA 저장 Port
     * @param auditLogAssembler Command → Domain 변환 Assembler
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public AuditLogService(
            final SaveAuditLogPort saveAuditLogPort,
            final AuditLogAssembler auditLogAssembler
    ) {
        this.saveAuditLogPort = Objects.requireNonNull(saveAuditLogPort, "SaveAuditLogPort cannot be null");
        this.auditLogAssembler = Objects.requireNonNull(auditLogAssembler, "AuditLogAssembler cannot be null");
    }

    /**
     * 감사 로그를 비동기로 생성하고 저장합니다 (Command Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Command 유효성 검증</li>
     *   <li>Assembler를 통해 AuditLog Domain 객체 생성</li>
     *   <li>SaveAuditLogPort를 통해 JPA 저장</li>
     *   <li>저장 실패 시 예외 로깅 (요청에 영향 없음)</li>
     * </ol>
     *
     * <p><strong>비동기 처리:</strong></p>
     * <ul>
     *   <li>@Async 어노테이션으로 비동기 실행</li>
     *   <li>별도 Thread Pool에서 실행</li>
     *   <li>저장 실패 시 메인 요청에 영향 없음</li>
     * </ul>
     *
     * <p><strong>예외 처리:</strong></p>
     * <ul>
     *   <li>IllegalArgumentException: command가 null인 경우</li>
     *   <li>기타 예외: 로그 기록 후 무시 (비동기이므로 요청에 영향 없음)</li>
     * </ul>
     *
     * @param command 감사 로그 생성 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Async
    @Override
    public void createAuditLog(final Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        try {
            // 1. Command → Domain 변환
            final AuditLog auditLog = this.auditLogAssembler.toDomain(command);

            // 2. 비동기 저장
            this.saveAuditLogPort.save(auditLog);

            log.debug("AuditLog saved successfully: userId={}, actionType={}, resourceType={}",
                    command.getUserId(), command.getActionType(), command.getResourceType());
        } catch (Exception ex) {
            // 3. 저장 실패 시 예외 로깅 (비동기이므로 요청에 영향 없음)
            log.error("Failed to save AuditLog: userId={}, actionType={}, resourceType={}, error={}",
                    command.getUserId(), command.getActionType(), command.getResourceType(), ex.getMessage(), ex);
        }
    }

    /**
     * 여러 감사 로그를 비동기로 일괄 생성하고 저장합니다.
     *
     * <p>대량의 감사 로그를 효율적으로 저장할 때 사용됩니다.
     * Spring Data JPA의 saveAll()을 활용하여 배치 처리합니다.</p>
     *
     * @param commands 감사 로그 생성 Command 목록
     * @throws IllegalArgumentException commands가 null이거나 빈 리스트인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Async
    public void createAuditLogs(final Iterable<Command> commands) {
        if (commands == null) {
            throw new IllegalArgumentException("Commands cannot be null");
        }

        try {
            // 1. Commands → Domains 변환
            final Iterable<AuditLog> auditLogs = this.auditLogAssembler.toDomainList(commands);

            // 2. 일괄 비동기 저장
            this.saveAuditLogPort.saveAll(auditLogs);

            log.debug("Batch AuditLogs saved successfully");
        } catch (Exception ex) {
            // 3. 저장 실패 시 예외 로깅 (비동기이므로 요청에 영향 없음)
            log.error("Failed to save batch AuditLogs: error={}", ex.getMessage(), ex);
        }
    }
}
