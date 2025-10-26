package com.ryuqq.authhub.application.security.audit.service;

import com.ryuqq.authhub.application.security.audit.port.in.SearchAuditLogsUseCase;
import com.ryuqq.authhub.application.security.audit.port.out.LoadAuditLogsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * AuditLog Query Service - 감사 로그 검색 UseCase 구현체 (Query Service).
 *
 * <p>감사 로그 조회 및 검색 시스템의 핵심 비즈니스 로직을 담당하는 Query Service입니다.
 * JPA Specification 패턴을 활용하여 동적 쿼리를 구성하고, Port를 통해 데이터를 조회합니다.</p>
 *
 * <p><strong>비즈니스 로직 흐름:</strong></p>
 * <ol>
 *   <li><strong>Search Audit Logs</strong>:
 *     <ul>
 *       <li>Query 유효성 검증</li>
 *       <li>LoadAuditLogsPort를 통한 동적 쿼리 실행</li>
 *       <li>PageResult → SearchAuditLogsUseCase.Result 변환</li>
 *       <li>페이징 정보 포함 결과 반환</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Transaction 경계 - @Transactional(readOnly = true) 사용</li>
 *   <li>✅ Lombok 금지 - Plain Java 사용</li>
 *   <li>✅ Law of Demeter 준수 - Getter chaining 금지</li>
 *   <li>✅ Port/Adapter 패턴 - 의존성 역전 원칙 준수</li>
 *   <li>✅ Command/Query 분리 - Query 전용 (Search)</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>트랜잭션 설계:</strong></p>
 * <ul>
 *   <li>@Transactional(readOnly = true) - 읽기 전용 트랜잭션</li>
 *   <li>성능 최적화 - 불필요한 플러시 방지</li>
 *   <li>일관성 보장 - 트랜잭션 격리 수준 적용</li>
 * </ul>
 *
 * <p><strong>Specification 패턴:</strong></p>
 * <ul>
 *   <li>동적 쿼리 구성 - Query 객체의 nullable 필드 처리</li>
 *   <li>JPA Criteria API 활용 - 타입 안전성 보장</li>
 *   <li>복잡한 검색 조건 조합 가능</li>
 * </ul>
 *
 * <p><strong>의존성:</strong></p>
 * <ul>
 *   <li>LoadAuditLogsPort - JPA 조회</li>
 * </ul>
 *
 * <p><strong>사용 예시:</strong></p>
 * <pre>
 * // Controller에서 호출
 * SearchAuditLogsUseCase.Query query = SearchAuditLogsUseCase.Query.builder()
 *     .userId("user-uuid")
 *     .actionType(ActionType.LOGIN)
 *     .startDate(startDate)
 *     .endDate(endDate)
 *     .page(0)
 *     .size(20)
 *     .build();
 *
 * SearchAuditLogsUseCase.Result result = auditLogQueryService.searchAuditLogs(query);
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
public class AuditLogQueryService implements SearchAuditLogsUseCase {

    private final LoadAuditLogsPort loadAuditLogsPort;

    /**
     * AuditLogQueryService 생성자.
     *
     * @param loadAuditLogsPort JPA 조회 Port
     * @throws NullPointerException 파라미터가 null인 경우
     */
    public AuditLogQueryService(final LoadAuditLogsPort loadAuditLogsPort) {
        this.loadAuditLogsPort = Objects.requireNonNull(loadAuditLogsPort, "LoadAuditLogsPort cannot be null");
    }

    /**
     * 감사 로그를 검색합니다 (Query Operation).
     *
     * <p><strong>실행 흐름:</strong></p>
     * <ol>
     *   <li>Query 유효성 검증</li>
     *   <li>LoadAuditLogsPort를 통한 JPA Specification 쿼리 실행</li>
     *   <li>PageResult → Result 변환</li>
     *   <li>페이징 정보 포함 결과 반환</li>
     * </ol>
     *
     * <p><strong>동적 쿼리 구성:</strong></p>
     * <ul>
     *   <li>Query 객체의 nullable 필드는 조건에서 제외</li>
     *   <li>Specification 패턴으로 유연한 조건 조합</li>
     *   <li>JPA Criteria API로 타입 안전성 보장</li>
     * </ul>
     *
     * <p><strong>페이징 처리:</strong></p>
     * <ul>
     *   <li>Query.page, Query.size 기반 페이징</li>
     *   <li>전체 페이지 개수 및 전체 요소 개수 포함</li>
     *   <li>첫 페이지/마지막 페이지 여부 제공</li>
     * </ul>
     *
     * @param query 검색 Query
     * @return 검색 결과 Result (페이징 정보 포함)
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public Result searchAuditLogs(final Query query) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        // 1. LoadAuditLogsPort를 통한 동적 쿼리 실행
        final LoadAuditLogsPort.PageResult pageResult = this.loadAuditLogsPort.search(query);

        // 2. PageResult → Result 변환
        return toResult(pageResult);
    }

    /**
     * PageResult를 Result로 변환합니다.
     *
     * @param pageResult LoadAuditLogsPort.PageResult
     * @return SearchAuditLogsUseCase.Result
     */
    private Result toResult(final LoadAuditLogsPort.PageResult pageResult) {
        return new Result(
                pageResult.getContent(),
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    /**
     * 특정 사용자의 감사 로그를 조회합니다.
     *
     * <p>사용자 ID로 필터링하여 최근 감사 로그부터 조회합니다.
     * 간편한 사용자별 로그 조회를 위한 헬퍼 메서드입니다.</p>
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색 결과 Result
     * @throws IllegalArgumentException userId가 null이거나 빈 문자열이거나, page/size가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Result findByUserId(final String userId, final int page, final int size) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or blank");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }

        // LoadAuditLogsPort를 통한 사용자별 조회
        final LoadAuditLogsPort.PageResult pageResult = this.loadAuditLogsPort.findByUserId(userId, page, size);

        return toResult(pageResult);
    }

    /**
     * 특정 리소스의 감사 로그를 조회합니다.
     *
     * <p>리소스 타입과 리소스 ID로 필터링하여 최근 감사 로그부터 조회합니다.
     * 간편한 리소스별 로그 조회를 위한 헬퍼 메서드입니다.</p>
     *
     * @param resourceType 리소스 타입 (Enum name, 예: "USER", "ORGANIZATION")
     * @param resourceId 리소스 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색 결과 Result
     * @throws IllegalArgumentException resourceType, resourceId가 null이거나 빈 문자열이거나, page/size가 음수인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public Result findByResource(final String resourceType, final String resourceId, final int page, final int size) {
        if (resourceType == null || resourceType.isBlank()) {
            throw new IllegalArgumentException("ResourceType cannot be null or blank");
        }
        if (resourceId == null || resourceId.isBlank()) {
            throw new IllegalArgumentException("ResourceId cannot be null or blank");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }

        // LoadAuditLogsPort를 통한 리소스별 조회
        final LoadAuditLogsPort.PageResult pageResult = this.loadAuditLogsPort.findByResource(
                resourceType,
                resourceId,
                page,
                size
        );

        return toResult(pageResult);
    }
}
