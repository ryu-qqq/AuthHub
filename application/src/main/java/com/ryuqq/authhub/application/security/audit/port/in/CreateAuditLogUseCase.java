package com.ryuqq.authhub.application.security.audit.port.in;

import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;

/**
 * 감사 로그 생성 UseCase (Port In).
 *
 * <p>시스템에서 발생하는 중요 행위를 감사 로그로 기록하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>사용자 행위를 감사 로그로 변환</li>
 *   <li>AuditLog Aggregate 생성</li>
 *   <li>비동기 저장 처리 (@Async)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 명령 전용 (Command)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ 비동기 처리 - @Async로 저장 실패가 요청에 영향 없음</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 로그인 감사 로그 생성
 * CreateAuditLogUseCase.Command command = new CreateAuditLogUseCase.Command(
 *     "user-uuid-123",
 *     ActionType.LOGIN,
 *     ResourceType.USER,
 *     "user-resource-id",
 *     "192.168.0.1",
 *     "Mozilla/5.0..."
 * );
 *
 * createAuditLogUseCase.createAuditLog(command);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CreateAuditLogUseCase {

    /**
     * 감사 로그를 생성합니다.
     * 비동기로 처리되어 저장 실패가 요청에 영향을 주지 않습니다.
     *
     * @param command 감사 로그 생성 Command
     * @throws IllegalArgumentException command가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    void createAuditLog(Command command);

    /**
     * 감사 로그 생성 요청 Command.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Command {

        private final String userId;
        private final ActionType actionType;
        private final ResourceType resourceType;
        private final String resourceId;
        private final String ipAddress;
        private final String userAgent;

        /**
         * Command 생성자.
         *
         * @param userId 사용자 ID (UUID 문자열)
         * @param actionType 액션 타입
         * @param resourceType 리소스 타입
         * @param resourceId 리소스 ID (nullable)
         * @param ipAddress IP 주소
         * @param userAgent User-Agent 문자열
         * @throws IllegalArgumentException 필수 파라미터가 null이거나 빈 문자열인 경우
         */
        public Command(
                final String userId,
                final ActionType actionType,
                final ResourceType resourceType,
                final String resourceId,
                final String ipAddress,
                final String userAgent
        ) {
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("UserId cannot be null or blank");
            }
            if (actionType == null) {
                throw new IllegalArgumentException("ActionType cannot be null");
            }
            if (resourceType == null) {
                throw new IllegalArgumentException("ResourceType cannot be null");
            }
            if (ipAddress == null || ipAddress.isBlank()) {
                throw new IllegalArgumentException("IpAddress cannot be null or blank");
            }
            if (userAgent == null || userAgent.isBlank()) {
                throw new IllegalArgumentException("UserAgent cannot be null or blank");
            }

            this.userId = userId;
            this.actionType = actionType;
            this.resourceType = resourceType;
            this.resourceId = resourceId;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
        }

        /**
         * 사용자 ID를 반환합니다.
         *
         * @return 사용자 ID (UUID 문자열)
         */
        public String getUserId() {
            return this.userId;
        }

        /**
         * 액션 타입을 반환합니다.
         *
         * @return ActionType
         */
        public ActionType getActionType() {
            return this.actionType;
        }

        /**
         * 리소스 타입을 반환합니다.
         *
         * @return ResourceType
         */
        public ResourceType getResourceType() {
            return this.resourceType;
        }

        /**
         * 리소스 ID를 반환합니다.
         *
         * @return 리소스 ID (nullable)
         */
        public String getResourceId() {
            return this.resourceId;
        }

        /**
         * IP 주소를 반환합니다.
         *
         * @return IP 주소 문자열
         */
        public String getIpAddress() {
            return this.ipAddress;
        }

        /**
         * User-Agent를 반환합니다.
         *
         * @return User-Agent 문자열
         */
        public String getUserAgent() {
            return this.userAgent;
        }
    }
}
