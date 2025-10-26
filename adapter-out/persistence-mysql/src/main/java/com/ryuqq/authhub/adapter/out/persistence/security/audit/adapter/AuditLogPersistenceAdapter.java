package com.ryuqq.authhub.adapter.out.persistence.security.audit.adapter;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ActionTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ResourceTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.mapper.AuditLogJpaMapper;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.repository.AuditLogJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.repository.AuditLogSpecifications;
import com.ryuqq.authhub.application.security.audit.port.in.SearchAuditLogsUseCase;
import com.ryuqq.authhub.application.security.audit.port.out.LoadAuditLogsPort;
import com.ryuqq.authhub.application.security.audit.port.out.SaveAuditLogPort;
import com.ryuqq.authhub.domain.security.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * AuditLog Persistence Adapter.
 *
 * <p>AuditLog Aggregate를 위한 영속성 어댑터로, Application Port를 구현하여
 * Domain과 Persistence 계층 간의 Anti-Corruption Layer 역할을 수행합니다.</p>
 *
 * <p><strong>구현 Port:</strong></p>
 * <ul>
 *   <li>{@link SaveAuditLogPort} - AuditLog 저장</li>
 *   <li>{@link LoadAuditLogsPort} - AuditLog 조회</li>
 * </ul>
 *
 * <p><strong>주요 책임:</strong></p>
 * <ul>
 *   <li>Domain AuditLog ↔ JPA AuditLogJpaEntity 변환 (Mapper 위임)</li>
 *   <li>JPA Repository를 통한 데이터베이스 접근</li>
 *   <li>Specification 패턴으로 동적 쿼리 구성</li>
 *   <li>Domain 모델의 순수성 보호 (Anti-Corruption)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ Long FK 전략 - JPA 관계 어노테이션 사용하지 않음</li>
 *   <li>✅ Law of Demeter 준수 - Mapper에 변환 위임</li>
 *   <li>✅ Transaction 경계 준수 - {@code @Transactional}은 Application Service에서 관리</li>
 *   <li>✅ Specification 패턴 - 동적 쿼리 구성</li>
 * </ul>
 *
 * <p><strong>Transaction 관리:</strong></p>
 * <ul>
 *   <li>Adapter는 {@code @Transactional}을 사용하지 않습니다</li>
 *   <li>Transaction 경계는 Application Layer의 UseCase에서 관리</li>
 *   <li>Adapter는 단순히 Repository 호출만 수행</li>
 *   <li>감사 로그 저장은 비동기로 처리하여 성능 영향 최소화</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@Component
public class AuditLogPersistenceAdapter implements SaveAuditLogPort, LoadAuditLogsPort {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuditLogJpaMapper auditLogJpaMapper;

    /**
     * AuditLogPersistenceAdapter 생성자.
     *
     * <p>Spring의 생성자 주입을 통해 의존성을 주입받습니다.
     * {@code @Autowired} 어노테이션은 생성자가 하나일 경우 생략 가능합니다.</p>
     *
     * @param auditLogJpaRepository AuditLogJpaRepository (null 불가)
     * @param auditLogJpaMapper AuditLogJpaMapper (null 불가)
     * @throws NullPointerException 인자 중 하나라도 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    public AuditLogPersistenceAdapter(
            final AuditLogJpaRepository auditLogJpaRepository,
            final AuditLogJpaMapper auditLogJpaMapper
    ) {
        this.auditLogJpaRepository = Objects.requireNonNull(auditLogJpaRepository, "auditLogJpaRepository cannot be null");
        this.auditLogJpaMapper = Objects.requireNonNull(auditLogJpaMapper, "auditLogJpaMapper cannot be null");
    }

    /**
     * 감사 로그를 저장합니다.
     *
     * <p>Domain AuditLog를 AuditLogJpaMapper를 통해 AuditLogJpaEntity로 변환한 후,
     * AuditLogJpaRepository.save()를 호출하여 데이터베이스에 저장합니다.</p>
     *
     * <p><strong>동작 순서:</strong></p>
     * <ol>
     *   <li>Domain AuditLog → JPA AuditLogJpaEntity 변환 (Mapper)</li>
     *   <li>AuditLogJpaRepository.save() 호출</li>
     * </ol>
     *
     * <p><strong>비동기 저장:</strong></p>
     * <ul>
     *   <li>Application Service에서 {@code @Async}로 호출하여 비동기 처리</li>
     *   <li>저장 실패가 요청에 영향을 주지 않음</li>
     * </ul>
     *
     * @param auditLog Domain AuditLog (null 불가)
     * @throws IllegalArgumentException auditLog가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public void save(final AuditLog auditLog) {
        Objects.requireNonNull(auditLog, "AuditLog cannot be null");

        AuditLogJpaEntity entity = auditLogJpaMapper.toEntity(auditLog);
        auditLogJpaRepository.save(entity);
    }

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
    @Override
    public void saveAll(final Iterable<AuditLog> auditLogs) {
        Objects.requireNonNull(auditLogs, "AuditLogs cannot be null");

        List<AuditLogJpaEntity> entities = ((List<AuditLog>) auditLogs).stream()
                .map(auditLogJpaMapper::toEntity)
                .toList();

        auditLogJpaRepository.saveAll(entities);
    }

    /**
     * 감사 로그를 검색합니다.
     *
     * <p>JPA Specification 패턴으로 동적 쿼리를 구성하여 검색합니다.
     * Query 객체의 모든 nullable 필드는 null이면 조건에서 제외됩니다.</p>
     *
     * <p><strong>동작 순서:</strong></p>
     * <ol>
     *   <li>Query 객체에서 Specification 빌드</li>
     *   <li>Pageable 객체 생성 (최신순 정렬)</li>
     *   <li>AuditLogJpaRepository.findAll(spec, pageable) 호출</li>
     *   <li>조회된 Entity → Domain AuditLog 변환</li>
     *   <li>PageResult로 감싸서 반환</li>
     * </ol>
     *
     * @param query 검색 조건 Query
     * @return 검색 결과 PageResult (페이징 정보 포함)
     * @throws IllegalArgumentException query가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    @Override
    public PageResult search(final SearchAuditLogsUseCase.Query query) {
        Objects.requireNonNull(query, "Query cannot be null");

        Specification<AuditLogJpaEntity> spec = buildSpecification(query);
        Pageable pageable = buildPageable(query);

        Page<AuditLogJpaEntity> page = auditLogJpaRepository.findAll(spec, pageable);

        List<AuditLog> content = page.getContent().stream()
                .map(auditLogJpaMapper::toDomain)
                .toList();

        return new PageResult(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

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
    @Override
    public PageResult findByUserId(final String userId, final int page, final int size) {
        validateUserId(userId);
        validatePageParameters(page, size);

        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasUserId(userId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));

        Page<AuditLogJpaEntity> resultPage = auditLogJpaRepository.findAll(spec, pageable);

        return toPageResult(resultPage);
    }

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
    @Override
    public PageResult findByResource(final String resourceType, final String resourceId, final int page, final int size) {
        validateResourceType(resourceType);
        validateResourceId(resourceId);
        validatePageParameters(page, size);

        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.valueOf(resourceType);
        Specification<AuditLogJpaEntity> spec = AuditLogSpecifications.hasResource(resourceTypeEnum, resourceId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt"));

        Page<AuditLogJpaEntity> resultPage = auditLogJpaRepository.findAll(spec, pageable);

        return toPageResult(resultPage);
    }

    /**
     * Query 객체로부터 Specification을 빌드합니다.
     *
     * @param query 검색 조건 Query
     * @return Specification
     */
    private Specification<AuditLogJpaEntity> buildSpecification(final SearchAuditLogsUseCase.Query query) {
        Specification<AuditLogJpaEntity> spec = Specification.where(null);

        if (query.getUserId() != null) {
            spec = spec.and(AuditLogSpecifications.hasUserId(query.getUserId()));
        }
        if (query.getActionType() != null) {
            ActionTypeEnum actionTypeEnum = mapToActionTypeEnum(query.getActionType());
            spec = spec.and(AuditLogSpecifications.hasActionType(actionTypeEnum));
        }
        if (query.getResourceType() != null) {
            ResourceTypeEnum resourceTypeEnum = mapToResourceTypeEnum(query.getResourceType());
            spec = spec.and(AuditLogSpecifications.hasResourceType(resourceTypeEnum));
        }
        if (query.getResourceId() != null) {
            spec = spec.and(AuditLogSpecifications.hasResourceId(query.getResourceId()));
        }
        if (query.getIpAddress() != null) {
            spec = spec.and(AuditLogSpecifications.hasIpAddress(query.getIpAddress()));
        }
        if (query.getStartDate() != null && query.getEndDate() != null) {
            spec = spec.and(AuditLogSpecifications.occurredBetween(query.getStartDate(), query.getEndDate()));
        }

        return spec;
    }

    /**
     * Query 객체로부터 Pageable을 빌드합니다.
     *
     * @param query 검색 조건 Query
     * @return Pageable (최신순 정렬)
     */
    private Pageable buildPageable(final SearchAuditLogsUseCase.Query query) {
        return PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(Sort.Direction.DESC, "occurredAt")
        );
    }

    /**
     * Page<AuditLogJpaEntity>를 PageResult로 변환합니다.
     *
     * @param page JPA Page 객체
     * @return PageResult
     */
    private PageResult toPageResult(final Page<AuditLogJpaEntity> page) {
        List<AuditLog> content = page.getContent().stream()
                .map(auditLogJpaMapper::toDomain)
                .toList();

        return new PageResult(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    /**
     * Domain ActionType을 JPA ActionTypeEnum으로 변환합니다.
     */
    private ActionTypeEnum mapToActionTypeEnum(final com.ryuqq.authhub.domain.security.audit.vo.ActionType actionType) {
        return ActionTypeEnum.valueOf(actionType.name());
    }

    /**
     * Domain ResourceType을 JPA ResourceTypeEnum으로 변환합니다.
     */
    private ResourceTypeEnum mapToResourceTypeEnum(final com.ryuqq.authhub.domain.security.audit.vo.ResourceType resourceType) {
        return ResourceTypeEnum.valueOf(resourceType.name());
    }

    /**
     * userId 검증.
     */
    private void validateUserId(final String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or blank");
        }
    }

    /**
     * resourceType 검증.
     */
    private void validateResourceType(final String resourceType) {
        if (resourceType == null || resourceType.isBlank()) {
            throw new IllegalArgumentException("ResourceType cannot be null or blank");
        }
    }

    /**
     * resourceId 검증.
     */
    private void validateResourceId(final String resourceId) {
        if (resourceId == null || resourceId.isBlank()) {
            throw new IllegalArgumentException("ResourceId cannot be null or blank");
        }
    }

    /**
     * 페이지 파라미터 검증.
     */
    private void validatePageParameters(final int page, final int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
    }
}
