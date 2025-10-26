package com.ryuqq.authhub.adapter.out.persistence.security.audit.adapter;

import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ActionTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.AuditLogJpaEntity;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.entity.ResourceTypeEnum;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.mapper.AuditLogJpaMapper;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.repository.AuditLogJpaRepository;
import com.ryuqq.authhub.adapter.out.persistence.security.audit.repository.AuditLogSpecifications;
import com.ryuqq.authhub.application.security.audit.port.in.SearchAuditLogsUseCase;
import com.ryuqq.authhub.application.security.audit.port.out.LoadAuditLogsPort;
import com.ryuqq.authhub.domain.auth.user.UserId;
import com.ryuqq.authhub.domain.security.audit.AuditLog;
import com.ryuqq.authhub.domain.security.audit.vo.ActionType;
import com.ryuqq.authhub.domain.security.audit.vo.AuditLogId;
import com.ryuqq.authhub.domain.security.audit.vo.IpAddress;
import com.ryuqq.authhub.domain.security.audit.vo.ResourceType;
import com.ryuqq.authhub.domain.security.audit.vo.UserAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

/**
 * AuditLogPersistenceAdapter 단위 테스트.
 *
 * <p>Mock 객체를 사용하여 외부 의존성 없이 Adapter의 동작을 검증합니다.
 * Database나 Spring Context 없이 빠르게 실행됩니다.</p>
 *
 * <p><strong>테스트 전략:</strong></p>
 * <ul>
 *   <li>MockitoExtension 사용 - {@code @SpringBootTest} 금지 (Zero-Tolerance)</li>
 *   <li>Repository와 Mapper는 Mock으로 대체</li>
 *   <li>각 Port 메서드의 동작 검증 (SaveAuditLogPort, LoadAuditLogsPort)</li>
 *   <li>Null 안전성 검증</li>
 *   <li>Specification 동적 쿼리 빌드 검증</li>
 *   <li>페이징 및 정렬 검증</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java 구현</li>
 *   <li>✅ {@code @SpringBootTest} 금지 - MockitoExtension 사용</li>
 *   <li>✅ Law of Demeter 준수 - Mock 검증에서 확인</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogPersistenceAdapter 단위 테스트")
class AuditLogPersistenceAdapterTest {

    @Mock
    private AuditLogJpaRepository auditLogJpaRepository;

    @Mock
    private AuditLogJpaMapper auditLogJpaMapper;

    @InjectMocks
    private AuditLogPersistenceAdapter adapter;

    private AuditLogId testAuditLogId;
    private UserId testUserId;
    private AuditLog testDomainAuditLog;
    private AuditLogJpaEntity testJpaEntity;
    private Instant testOccurredAt;

    @BeforeEach
    void setUp() {
        testAuditLogId = AuditLogId.newId();
        testUserId = UserId.newId();
        testOccurredAt = Instant.now();

        testDomainAuditLog = AuditLog.reconstruct(
                testAuditLogId,
                testUserId,
                ActionType.CREATE,
                ResourceType.USER,
                "resource-123",
                IpAddress.of("192.168.1.1"),
                UserAgent.of("Mozilla/5.0"),
                testOccurredAt
        );

        testJpaEntity = AuditLogJpaEntity.of(
                testAuditLogId.asString(),
                testUserId.asString(),
                ActionTypeEnum.CREATE,
                ResourceTypeEnum.USER,
                "resource-123",
                "192.168.1.1",
                "Mozilla/5.0",
                testOccurredAt
        );
    }

    @Test
    @DisplayName("AuditLog를 성공적으로 저장한다")
    void save_Success() {
        // given
        given(auditLogJpaMapper.toEntity(testDomainAuditLog)).willReturn(testJpaEntity);
        given(auditLogJpaRepository.save(testJpaEntity)).willReturn(testJpaEntity);

        // when
        adapter.save(testDomainAuditLog);

        // then
        then(auditLogJpaMapper).should(times(1)).toEntity(testDomainAuditLog);
        then(auditLogJpaRepository).should(times(1)).save(testJpaEntity);
    }

    @Test
    @DisplayName("save() 호출 시 auditLog가 null이면 예외를 던진다")
    void save_WithNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("AuditLog cannot be null");

        then(auditLogJpaMapper).should(times(0)).toEntity(any());
        then(auditLogJpaRepository).should(times(0)).save(any());
    }

    @Test
    @DisplayName("여러 AuditLog를 일괄 저장한다")
    void saveAll_Success() {
        // given
        AuditLog auditLog1 = testDomainAuditLog;
        AuditLog auditLog2 = AuditLog.reconstruct(
                AuditLogId.newId(), testUserId, ActionType.UPDATE, ResourceType.USER,
                "resource-456", IpAddress.of("192.168.1.2"), UserAgent.of("Chrome/91.0"), testOccurredAt
        );

        AuditLogJpaEntity entity1 = testJpaEntity;
        AuditLogJpaEntity entity2 = AuditLogJpaEntity.of(
                AuditLogId.newId().asString(), testUserId.asString(), ActionTypeEnum.UPDATE,
                ResourceTypeEnum.USER, "resource-456", "192.168.1.2", "Chrome/91.0", testOccurredAt
        );

        List<AuditLog> auditLogs = Arrays.asList(auditLog1, auditLog2);
        given(auditLogJpaMapper.toEntity(auditLog1)).willReturn(entity1);
        given(auditLogJpaMapper.toEntity(auditLog2)).willReturn(entity2);

        // when
        adapter.saveAll(auditLogs);

        // then
        then(auditLogJpaMapper).should(times(2)).toEntity(any(AuditLog.class));
        then(auditLogJpaRepository).should(times(1)).saveAll(any());
    }

    @Test
    @DisplayName("saveAll() 호출 시 auditLogs가 null이면 예외를 던진다")
    void saveAll_WithNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.saveAll(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("AuditLogs cannot be null");

        then(auditLogJpaRepository).should(times(0)).saveAll(any());
    }

    @Test
    @DisplayName("Query로 AuditLog를 검색하고 PageResult를 반환한다")
    void search_Success() {
        // given
        SearchAuditLogsUseCase.Query query = SearchAuditLogsUseCase.Query.builder()
                .userId(testUserId.asString())
                .actionType(ActionType.CREATE)
                .resourceType(ResourceType.USER)
                .resourceId("resource-123")
                .ipAddress("192.168.1.1")
                .page(0)
                .size(10)
                .build();

        List<AuditLogJpaEntity> entities = Arrays.asList(testJpaEntity);
        Page<AuditLogJpaEntity> page = new PageImpl<>(entities);

        given(auditLogJpaRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(auditLogJpaMapper.toDomain(testJpaEntity)).willReturn(testDomainAuditLog);

        // when
        LoadAuditLogsPort.PageResult result = adapter.search(query);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testDomainAuditLog);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        then(auditLogJpaRepository).should(times(1)).findAll(any(Specification.class), any(Pageable.class));
        then(auditLogJpaMapper).should(times(1)).toDomain(testJpaEntity);
    }

    @Test
    @DisplayName("search() 호출 시 query가 null이면 예외를 던진다")
    void search_WithNullQuery_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.search(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Query cannot be null");

        then(auditLogJpaRepository).should(times(0)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("UserId로 AuditLog를 조회한다")
    void findByUserId_Success() {
        // given
        String userId = testUserId.asString();
        List<AuditLogJpaEntity> entities = Arrays.asList(testJpaEntity);
        Page<AuditLogJpaEntity> page = new PageImpl<>(entities);

        given(auditLogJpaRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(auditLogJpaMapper.toDomain(testJpaEntity)).willReturn(testDomainAuditLog);

        // when
        LoadAuditLogsPort.PageResult result = adapter.findByUserId(userId, 0, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testDomainAuditLog);

        // Specification 빌드 검증
        ArgumentCaptor<Specification<AuditLogJpaEntity>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        then(auditLogJpaRepository).should(times(1)).findAll(specCaptor.capture(), any(Pageable.class));
    }

    @Test
    @DisplayName("findByUserId() 호출 시 userId가 null이면 예외를 던진다")
    void findByUserId_WithNullUserId_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByUserId(null, 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId cannot be null or blank");
    }

    @Test
    @DisplayName("findByUserId() 호출 시 userId가 빈 문자열이면 예외를 던진다")
    void findByUserId_WithBlankUserId_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByUserId("   ", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId cannot be null or blank");
    }

    @Test
    @DisplayName("findByUserId() 호출 시 page가 음수이면 예외를 던진다")
    void findByUserId_WithNegativePage_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByUserId(testUserId.asString(), -1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page cannot be negative");
    }

    @Test
    @DisplayName("findByUserId() 호출 시 size가 음수이면 예외를 던진다")
    void findByUserId_WithNegativeSize_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByUserId(testUserId.asString(), 0, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Size cannot be negative");
    }

    @Test
    @DisplayName("Resource로 AuditLog를 조회한다")
    void findByResource_Success() {
        // given
        String resourceType = "USER";
        String resourceId = "resource-123";
        List<AuditLogJpaEntity> entities = Arrays.asList(testJpaEntity);
        Page<AuditLogJpaEntity> page = new PageImpl<>(entities);

        given(auditLogJpaRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(auditLogJpaMapper.toDomain(testJpaEntity)).willReturn(testDomainAuditLog);

        // when
        LoadAuditLogsPort.PageResult result = adapter.findByResource(resourceType, resourceId, 0, 10);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testDomainAuditLog);

        then(auditLogJpaRepository).should(times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("findByResource() 호출 시 resourceType이 null이면 예외를 던진다")
    void findByResource_WithNullResourceType_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByResource(null, "resource-123", 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ResourceType cannot be null or blank");
    }

    @Test
    @DisplayName("findByResource() 호출 시 resourceId가 null이면 예외를 던진다")
    void findByResource_WithNullResourceId_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> adapter.findByResource("USER", null, 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ResourceId cannot be null or blank");
    }

    @Test
    @DisplayName("생성자에 null Repository를 주입하면 예외를 던진다")
    void constructor_WithNullRepository_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> new AuditLogPersistenceAdapter(null, auditLogJpaMapper))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("auditLogJpaRepository cannot be null");
    }

    @Test
    @DisplayName("생성자에 null Mapper를 주입하면 예외를 던진다")
    void constructor_WithNullMapper_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> new AuditLogPersistenceAdapter(auditLogJpaRepository, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("auditLogJpaMapper cannot be null");
    }

    @Test
    @DisplayName("검색 시 Pageable이 올바른 정렬 옵션으로 생성된다")
    void search_CreatesPageableWithCorrectSorting() {
        // given
        SearchAuditLogsUseCase.Query query = SearchAuditLogsUseCase.Query.builder()
                .page(0)
                .size(10)
                .build();

        List<AuditLogJpaEntity> entities = Arrays.asList(testJpaEntity);
        Page<AuditLogJpaEntity> page = new PageImpl<>(entities);

        given(auditLogJpaRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(auditLogJpaMapper.toDomain(testJpaEntity)).willReturn(testDomainAuditLog);

        // when
        adapter.search(query);

        // then - Pageable 정렬 확인 (occurredAt DESC)
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        then(auditLogJpaRepository).should(times(1)).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().toString()).contains("occurredAt: DESC");
    }
}
