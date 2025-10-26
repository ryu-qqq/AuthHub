package com.ryuqq.authhub.application.security.audit.assembler;

import com.ryuqq.authhub.application.security.audit.port.in.CreateAuditLogUseCase;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.security.audit.AuditLog;
import com.ryuqq.authhub.domain.security.audit.vo.IpAddress;
import com.ryuqq.authhub.domain.security.audit.vo.UserAgent;
import org.springframework.stereotype.Component;

/**
 * AuditLog Assembler - Command ↔ Domain Aggregate 변환.
 *
 * <p>Application Layer의 Assembler 패턴을 따르며, UseCase Command를
 * Domain Aggregate(AuditLog)로 변환하는 책임을 가집니다.</p>
 *
 * <p><strong>책임 범위:</strong></p>
 * <ul>
 *   <li>Command → Domain 변환 (CreateAuditLogUseCase.Command → AuditLog)</li>
 *   <li>Primitive 타입 → Value Object 변환</li>
 *   <li>Law of Demeter 준수 - Domain 객체의 Factory Method 활용</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ 비즈니스 로직 금지 - 순수 변환 로직만</li>
 *   <li>✅ Port 호출 금지 - 외부 의존성 없음</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // AuditLogService에서 사용
 * CreateAuditLogUseCase.Command command = ...;
 * AuditLog auditLog = auditLogAssembler.toDomain(command);
 * saveAuditLogPort.save(auditLog);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class AuditLogAssembler {

    /**
     * Command의 유효성을 검증합니다.
     *
     * @param command CreateAuditLogUseCase.Command
     * @throws IllegalArgumentException command가 null인 경우
     */
    private void validateCommand(final CreateAuditLogUseCase.Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }
    }

    /**
     * CreateAuditLogUseCase.Command를 AuditLog Domain Aggregate로 변환합니다.
     *
     * <p><strong>변환 과정:</strong></p>
     * <ol>
     *   <li>Command 유효성 검증</li>
     *   <li>Primitive 타입 → Value Object 변환</li>
     *   <li>AuditLog.create() Factory Method 호출</li>
     * </ol>
     *
     * <p><strong>Law of Demeter 준수:</strong></p>
     * <ul>
     *   <li>✅ AuditLog.create() 사용 - Factory Method 패턴</li>
     *   <li>✅ UserId.fromString() 사용 - Value Object 생성</li>
     *   <li>✅ IpAddress.of() 사용 - Value Object 생성</li>
     *   <li>✅ UserAgent.of() 사용 - Value Object 생성</li>
     * </ul>
     *
     * @param command CreateAuditLogUseCase.Command (null 불가)
     * @return AuditLog Domain Aggregate
     * @throws IllegalArgumentException command가 null인 경우
     * @throws IllegalArgumentException userId, ipAddress, userAgent가 잘못된 형식인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuditLog toDomain(final CreateAuditLogUseCase.Command command) {
        validateCommand(command);

        // Primitive 타입 → Value Object 변환
        final UserId userId = UserId.fromString(command.getUserId());
        final IpAddress ipAddress = IpAddress.of(command.getIpAddress());
        final UserAgent userAgent = UserAgent.of(command.getUserAgent());

        // AuditLog.create() Factory Method 호출
        return AuditLog.create(
                userId,
                command.getActionType(),
                command.getResourceType(),
                command.getResourceId(),
                ipAddress,
                userAgent
        );
    }

    /**
     * 여러 Command를 AuditLog 목록으로 변환합니다.
     *
     * <p>대량의 감사 로그를 일괄 생성할 때 사용됩니다.</p>
     *
     * @param commands CreateAuditLogUseCase.Command 목록
     * @return AuditLog Domain Aggregate 목록
     * @throws IllegalArgumentException commands가 null이거나 빈 리스트인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Iterable<AuditLog> toDomainList(final Iterable<CreateAuditLogUseCase.Command> commands) {
        if (commands == null) {
            throw new IllegalArgumentException("Commands cannot be null");
        }

        return () -> {
            return java.util.stream.StreamSupport.stream(commands.spliterator(), false)
                    .map(this::toDomain)
                    .iterator();
        };
    }
}
