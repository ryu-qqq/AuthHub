package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.mapper;

import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import com.ryuqq.authhub.domain.endpointpermission.aggregate.EndpointPermission;
import com.ryuqq.authhub.domain.endpointpermission.identifier.EndpointPermissionId;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointDescription;
import com.ryuqq.authhub.domain.endpointpermission.vo.EndpointPath;
import com.ryuqq.authhub.domain.endpointpermission.vo.HttpMethod;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredPermissions;
import com.ryuqq.authhub.domain.endpointpermission.vo.RequiredRoles;
import com.ryuqq.authhub.domain.endpointpermission.vo.ServiceName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * EndpointPermissionJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 EndpointPermission 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>EndpointPermission → EndpointPermissionJpaEntity (저장용)
 *   <li>EndpointPermissionJpaEntity → EndpointPermission (조회용)
 * </ul>
 *
 * <p><strong>Set ↔ 콤마 구분 문자열 변환:</strong>
 *
 * <ul>
 *   <li>Domain: Set&lt;String&gt; (requiredPermissions, requiredRoles)
 *   <li>Entity: 콤마 구분 문자열 (VARCHAR 컬럼)
 * </ul>
 *
 * <p><strong>시간 변환:</strong>
 *
 * <ul>
 *   <li>Domain: Instant (UTC)
 *   <li>Entity: LocalDateTime (UTC 기준)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointPermissionJpaEntityMapper {

    private static final String COMMA_DELIMITER = ",";

    /**
     * Domain → Entity 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>신규 EndpointPermission 저장 (ID가 null)
     *   <li>기존 EndpointPermission 수정 (ID가 있음)
     * </ul>
     *
     * @param domain EndpointPermission 도메인
     * @return EndpointPermissionJpaEntity
     */
    public EndpointPermissionJpaEntity toEntity(EndpointPermission domain) {
        return EndpointPermissionJpaEntity.of(
                null,
                domain.endpointPermissionIdValue(),
                domain.serviceNameValue(),
                domain.pathValue(),
                domain.methodValue(),
                domain.descriptionValue(),
                domain.isPublic(),
                toCommaSeparatedString(domain.requiredPermissionValues()),
                toCommaSeparatedString(domain.requiredRoleValues()),
                domain.getVersion(),
                domain.isDeleted(),
                toLocalDateTime(domain.createdAt()),
                toLocalDateTime(domain.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * <p><strong>사용 시나리오:</strong>
     *
     * <ul>
     *   <li>데이터베이스에서 조회한 Entity를 Domain으로 변환
     *   <li>Application Layer로 전달
     * </ul>
     *
     * @param entity EndpointPermissionJpaEntity
     * @return EndpointPermission 도메인
     */
    public EndpointPermission toDomain(EndpointPermissionJpaEntity entity) {
        return EndpointPermission.reconstitute(
                EndpointPermissionId.of(entity.getEndpointPermissionId()),
                ServiceName.of(entity.getServiceName()),
                EndpointPath.of(entity.getPath()),
                HttpMethod.fromString(entity.getMethod()),
                entity.getDescription() != null
                        ? EndpointDescription.of(entity.getDescription())
                        : EndpointDescription.empty(),
                entity.isPublic(),
                RequiredPermissions.of(toStringSet(entity.getRequiredPermissions())),
                RequiredRoles.of(toStringSet(entity.getRequiredRoles())),
                entity.getVersion(),
                entity.isDeleted(),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /** Set&lt;String&gt; → 콤마 구분 문자열 변환 */
    private String toCommaSeparatedString(Set<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        return String.join(COMMA_DELIMITER, values);
    }

    /** 콤마 구분 문자열 → Set&lt;String&gt; 변환 */
    private Set<String> toStringSet(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(commaSeparated.split(COMMA_DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /** Instant → LocalDateTime 변환 (UTC 기준) */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /** LocalDateTime → Instant 변환 (UTC 기준) */
    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
