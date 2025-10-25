package com.ryuqq.authhub.application.security.audit.port.out;

import com.ryuqq.authhub.application.security.audit.port.in.SearchAuditLogsUseCase;
import com.ryuqq.authhub.domain.security.audit.AuditLog;

import java.util.List;

/**
 * 감사 로그 조회 Port (Port Out).
 *
 * <p>JPA를 통해 감사 로그를 조회하는 아웃바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Infrastructure Layer와의 경계 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>JPA Specification 패턴으로 동적 쿼리 구성</li>
 *   <li>페이징 처리 (Page 객체 반환)</li>
 *   <li>AuditLogJpaEntity를 AuditLog Domain 객체로 변환</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Infrastructure Layer 경계</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Transaction 경계 - @Transactional(readOnly = true)</li>
 * </ul>
 *
 * <p><strong>구현 계층 (Adapter-Out-Persistence):</strong></p>
 * <ul>
 *   <li>AuditLogPersistenceAdapter (Port 구현체)</li>
 *   <li>AuditLogJpaRepository (Spring Data JPA)</li>
 *   <li>AuditLogSpecifications (동적 쿼리 Specification)</li>
 * </ul>
 *
 * <p><strong>Specification 패턴 예시:</strong></p>
 * <pre>
 * public class AuditLogSpecifications {
 *     public static Specification<AuditLogJpaEntity> hasUserId(String userId) {
 *         return (root, query, cb) -> cb.equal(root.get("userId"), userId);
 *     }
 *
 *     public static Specification<AuditLogJpaEntity> hasActionType(ActionType actionType) {
 *         return (root, query, cb) -> cb.equal(root.get("actionType"), actionType);
 *     }
 *
 *     public static Specification<AuditLogJpaEntity> occurredBetween(Instant start, Instant end) {
 *         return (root, query, cb) -> cb.between(root.get("occurredAt"), start, end);
 *     }
 * }
 * </pre>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // AuditLogQueryService에서 호출
 * @Override
 * @Transactional(readOnly = true)
 * public SearchAuditLogsUseCase.Result searchAuditLogs(SearchAuditLogsUseCase.Query query) {
 *     PageResult result = loadAuditLogsPort.search(query);
 *     return new SearchAuditLogsUseCase.Result(
 *         result.getContent(),
 *         result.getPage(),
 *         result.getSize(),
 *         result.getTotalElements(),
 *         result.getTotalPages()
 *     );
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface LoadAuditLogsPort {

    /**
     * 감사 로그를 검색합니다.
     *
     * <p>JPA Specification 패턴으로 동적 쿼리를 구성하여 검색합니다.
     * Query 객체의 모든 nullable 필드는 null이면 조건에서 제외됩니다.</p>
     *
     * @param query 검색 조건 Query
     * @return 검색 결과 PageResult (페이징 정보 포함)
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    PageResult search(SearchAuditLogsUseCase.Query query);

    /**
     * 특정 사용자의 감사 로그를 조회합니다.
     *
     * <p>사용자 ID로 필터링하여 최근 감사 로그부터 조회합니다.</p>
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색 결과 PageResult
     * @throws IllegalArgumentException userId가 null이거나 빈 문자열이거나, page/size가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    PageResult findByUserId(String userId, int page, int size);

    /**
     * 특정 리소스의 감사 로그를 조회합니다.
     *
     * <p>리소스 타입과 리소스 ID로 필터링하여 최근 감사 로그부터 조회합니다.</p>
     *
     * @param resourceType 리소스 타입 (Enum name)
     * @param resourceId 리소스 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색 결과 PageResult
     * @throws IllegalArgumentException resourceType, resourceId가 null이거나 빈 문자열이거나, page/size가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    PageResult findByResource(String resourceType, String resourceId, int page, int size);

    /**
     * 페이징 결과를 담는 불변 객체.
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class PageResult {

        private final List<AuditLog> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        /**
         * PageResult 생성자.
         *
         * @param content 감사 로그 목록
         * @param page 현재 페이지 번호
         * @param size 페이지 크기
         * @param totalElements 전체 요소 개수
         * @param totalPages 전체 페이지 개수
         * @throws IllegalArgumentException content가 null이거나 page/size/totalElements/totalPages가 음수인 경우
         */
        public PageResult(
                final List<AuditLog> content,
                final int page,
                final int size,
                final long totalElements,
                final int totalPages
        ) {
            if (content == null) {
                throw new IllegalArgumentException("Content cannot be null");
            }
            if (page < 0) {
                throw new IllegalArgumentException("Page cannot be negative");
            }
            if (size < 0) {
                throw new IllegalArgumentException("Size cannot be negative");
            }
            if (totalElements < 0) {
                throw new IllegalArgumentException("Total elements cannot be negative");
            }
            if (totalPages < 0) {
                throw new IllegalArgumentException("Total pages cannot be negative");
            }

            this.content = List.copyOf(content);
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        /**
         * 감사 로그 목록을 반환합니다.
         *
         * @return 감사 로그 목록 (불변 리스트)
         */
        public List<AuditLog> getContent() {
            return this.content;
        }

        /**
         * 현재 페이지 번호를 반환합니다.
         *
         * @return 페이지 번호
         */
        public int getPage() {
            return this.page;
        }

        /**
         * 페이지 크기를 반환합니다.
         *
         * @return 페이지 크기
         */
        public int getSize() {
            return this.size;
        }

        /**
         * 전체 요소 개수를 반환합니다.
         *
         * @return 전체 요소 개수
         */
        public long getTotalElements() {
            return this.totalElements;
        }

        /**
         * 전체 페이지 개수를 반환합니다.
         *
         * @return 전체 페이지 개수
         */
        public int getTotalPages() {
            return this.totalPages;
        }

        /**
         * 마지막 페이지 여부를 반환합니다.
         *
         * @return 마지막 페이지이면 true, 아니면 false
         */
        public boolean isLast() {
            return this.page >= this.totalPages - 1;
        }

        /**
         * 첫 페이지 여부를 반환합니다.
         *
         * @return 첫 페이지이면 true, 아니면 false
         */
        public boolean isFirst() {
            return this.page == 0;
        }

        /**
         * 빈 결과 여부를 반환합니다.
         *
         * @return 결과가 비어있으면 true, 아니면 false
         */
        public boolean isEmpty() {
            return this.content.isEmpty();
        }
    }
}
