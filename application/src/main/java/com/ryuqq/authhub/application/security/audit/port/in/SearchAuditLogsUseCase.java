package com.ryuqq.authhub.application.security.audit.port.in;

import com.ryuqq.authhub.domain.security.audit.AuditLog;
import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;

import java.time.Instant;
import java.util.List;

/**
 * 감사 로그 검색 UseCase (Port In).
 *
 * <p>관리자가 감사 로그를 검색하고 조회하는 인바운드 포트입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴에 따라 Application Layer의 진입점 역할을 합니다.</p>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>다양한 조건으로 감사 로그 검색</li>
 *   <li>페이징 처리</li>
 *   <li>Specification 패턴으로 동적 쿼리 구성</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Port/Adapter 패턴 - Application Layer 진입점</li>
 *   <li>✅ Command/Query 분리 - 조회 전용 (Query)</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ @Transactional(readOnly = true) - 읽기 전용 트랜잭션</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // 특정 사용자의 로그인 이력 검색
 * SearchAuditLogsUseCase.Query query = SearchAuditLogsUseCase.Query.builder()
 *     .userId("user-uuid-123")
 *     .actionType(ActionType.LOGIN)
 *     .startDate(Instant.now().minus(30, ChronoUnit.DAYS))
 *     .endDate(Instant.now())
 *     .page(0)
 *     .size(20)
 *     .build();
 *
 * SearchAuditLogsUseCase.Result result = searchAuditLogsUseCase.searchAuditLogs(query);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SearchAuditLogsUseCase {

    /**
     * 감사 로그를 검색합니다.
     *
     * @param query 검색 Query
     * @return 검색 결과 Result (페이징 정보 포함)
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Result searchAuditLogs(Query query);

    /**
     * 감사 로그 검색 Query.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Query {

        private final String userId;
        private final ActionType actionType;
        private final ResourceType resourceType;
        private final String resourceId;
        private final String ipAddress;
        private final Instant startDate;
        private final Instant endDate;
        private final int page;
        private final int size;

        /**
         * Query 생성자 (private).
         * Builder를 통해서만 생성 가능합니다.
         */
        private Query(
                final String userId,
                final ActionType actionType,
                final ResourceType resourceType,
                final String resourceId,
                final String ipAddress,
                final Instant startDate,
                final Instant endDate,
                final int page,
                final int size
        ) {
            if (page < 0) {
                throw new IllegalArgumentException("Page cannot be negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be positive");
            }
            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }

            this.userId = userId;
            this.actionType = actionType;
            this.resourceType = resourceType;
            this.resourceId = resourceId;
            this.ipAddress = ipAddress;
            this.startDate = startDate;
            this.endDate = endDate;
            this.page = page;
            this.size = size;
        }

        /**
         * Builder를 생성합니다.
         *
         * @return Query.Builder 인스턴스
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * 사용자 ID를 반환합니다.
         *
         * @return 사용자 ID (nullable)
         */
        public String getUserId() {
            return this.userId;
        }

        /**
         * 액션 타입을 반환합니다.
         *
         * @return ActionType (nullable)
         */
        public ActionType getActionType() {
            return this.actionType;
        }

        /**
         * 리소스 타입을 반환합니다.
         *
         * @return ResourceType (nullable)
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
         * @return IP 주소 (nullable)
         */
        public String getIpAddress() {
            return this.ipAddress;
        }

        /**
         * 검색 시작 일자를 반환합니다.
         *
         * @return 시작 일자 (nullable)
         */
        public Instant getStartDate() {
            return this.startDate;
        }

        /**
         * 검색 종료 일자를 반환합니다.
         *
         * @return 종료 일자 (nullable)
         */
        public Instant getEndDate() {
            return this.endDate;
        }

        /**
         * 페이지 번호를 반환합니다 (0부터 시작).
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
         * Query Builder.
         *
         * @author AuthHub Team
         * @since 1.0.0
         */
        public static final class Builder {

            private String userId;
            private ActionType actionType;
            private ResourceType resourceType;
            private String resourceId;
            private String ipAddress;
            private Instant startDate;
            private Instant endDate;
            private int page = 0;
            private int size = 20;

            private Builder() {
            }

            /**
             * 사용자 ID를 설정합니다.
             *
             * @param userId 사용자 ID
             * @return Builder 인스턴스
             */
            public Builder userId(final String userId) {
                this.userId = userId;
                return this;
            }

            /**
             * 액션 타입을 설정합니다.
             *
             * @param actionType 액션 타입
             * @return Builder 인스턴스
             */
            public Builder actionType(final ActionType actionType) {
                this.actionType = actionType;
                return this;
            }

            /**
             * 리소스 타입을 설정합니다.
             *
             * @param resourceType 리소스 타입
             * @return Builder 인스턴스
             */
            public Builder resourceType(final ResourceType resourceType) {
                this.resourceType = resourceType;
                return this;
            }

            /**
             * 리소스 ID를 설정합니다.
             *
             * @param resourceId 리소스 ID
             * @return Builder 인스턴스
             */
            public Builder resourceId(final String resourceId) {
                this.resourceId = resourceId;
                return this;
            }

            /**
             * IP 주소를 설정합니다.
             *
             * @param ipAddress IP 주소
             * @return Builder 인스턴스
             */
            public Builder ipAddress(final String ipAddress) {
                this.ipAddress = ipAddress;
                return this;
            }

            /**
             * 검색 시작 일자를 설정합니다.
             *
             * @param startDate 시작 일자
             * @return Builder 인스턴스
             */
            public Builder startDate(final Instant startDate) {
                this.startDate = startDate;
                return this;
            }

            /**
             * 검색 종료 일자를 설정합니다.
             *
             * @param endDate 종료 일자
             * @return Builder 인스턴스
             */
            public Builder endDate(final Instant endDate) {
                this.endDate = endDate;
                return this;
            }

            /**
             * 페이지 번호를 설정합니다 (0부터 시작).
             *
             * @param page 페이지 번호
             * @return Builder 인스턴스
             */
            public Builder page(final int page) {
                this.page = page;
                return this;
            }

            /**
             * 페이지 크기를 설정합니다.
             *
             * @param size 페이지 크기
             * @return Builder 인스턴스
             */
            public Builder size(final int size) {
                this.size = size;
                return this;
            }

            /**
             * Query 인스턴스를 생성합니다.
             *
             * @return Query 인스턴스
             * @throws IllegalArgumentException 유효하지 않은 파라미터가 있는 경우
             */
            public Query build() {
                return new Query(
                        this.userId,
                        this.actionType,
                        this.resourceType,
                        this.resourceId,
                        this.ipAddress,
                        this.startDate,
                        this.endDate,
                        this.page,
                        this.size
                );
            }
        }
    }

    /**
     * 감사 로그 검색 결과 Result.
     *
     * <p>불변 객체로 설계되어 스레드 안전성을 보장합니다.</p>
     *
     * @author AuthHub Team
     * @since 1.0.0
     */
    final class Result {

        private final List<AuditLog> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;

        /**
         * Result 생성자.
         *
         * @param content 감사 로그 목록
         * @param page 현재 페이지 번호
         * @param size 페이지 크기
         * @param totalElements 전체 요소 개수
         * @param totalPages 전체 페이지 개수
         * @throws IllegalArgumentException content가 null이거나 page/size/totalElements/totalPages가 음수인 경우
         */
        public Result(
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
