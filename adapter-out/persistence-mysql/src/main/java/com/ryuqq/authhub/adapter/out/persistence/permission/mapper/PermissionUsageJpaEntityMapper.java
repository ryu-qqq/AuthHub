package com.ryuqq.authhub.adapter.out.persistence.permission.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.authhub.adapter.out.persistence.permission.entity.PermissionUsageJpaEntity;
import com.ryuqq.authhub.domain.permission.aggregate.PermissionUsage;
import com.ryuqq.authhub.domain.permission.identifier.PermissionUsageId;
import com.ryuqq.authhub.domain.permission.vo.CodeLocation;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import com.ryuqq.authhub.domain.permission.vo.ServiceName;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * PermissionUsageJpaEntityMapper - Entity ↔ Domain 변환 Mapper
 *
 * <p>Persistence Layer의 JPA Entity와 Domain Layer의 PermissionUsage 간 변환을 담당합니다.
 *
 * <p><strong>변환 책임:</strong>
 *
 * <ul>
 *   <li>PermissionUsage → PermissionUsageJpaEntity (저장용)
 *   <li>PermissionUsageJpaEntity → PermissionUsage (조회용)
 * </ul>
 *
 * <p><strong>locations 변환:</strong>
 *
 * <ul>
 *   <li>Domain: List<CodeLocation>
 *   <li>Entity: String (JSON 배열)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionUsageJpaEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(PermissionUsageJpaEntityMapper.class);

    private final ObjectMapper objectMapper;

    public PermissionUsageJpaEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Domain → Entity 변환
     *
     * @param domain PermissionUsage 도메인
     * @return PermissionUsageJpaEntity
     */
    public PermissionUsageJpaEntity toEntity(PermissionUsage domain) {
        return PermissionUsageJpaEntity.of(
                domain.usageIdValue(),
                domain.permissionKeyValue(),
                domain.serviceNameValue(),
                toJson(domain.locationValues()),
                toLocalDateTime(domain.getLastScannedAt()),
                toLocalDateTime(domain.createdAt()),
                toLocalDateTime(domain.updatedAt()));
    }

    /**
     * Domain → Entity 변환 (업데이트용)
     *
     * <p>UUID PK 전략에서는 도메인의 usageId가 PK이므로 기존 ID 파라미터가 불필요합니다.
     *
     * @param domain PermissionUsage 도메인
     * @return PermissionUsageJpaEntity
     */
    public PermissionUsageJpaEntity toEntityForUpdate(PermissionUsage domain) {
        return PermissionUsageJpaEntity.of(
                domain.usageIdValue(),
                domain.permissionKeyValue(),
                domain.serviceNameValue(),
                toJson(domain.locationValues()),
                toLocalDateTime(domain.getLastScannedAt()),
                toLocalDateTime(domain.createdAt()),
                toLocalDateTime(domain.updatedAt()));
    }

    /**
     * Entity → Domain 변환
     *
     * @param entity PermissionUsageJpaEntity
     * @return PermissionUsage 도메인
     */
    public PermissionUsage toDomain(PermissionUsageJpaEntity entity) {
        List<CodeLocation> locations =
                fromJson(entity.getLocations()).stream().map(CodeLocation::of).toList();

        return PermissionUsage.reconstitute(
                PermissionUsageId.of(entity.getUsageId()),
                PermissionKey.of(entity.getPermissionKey()),
                ServiceName.of(entity.getServiceName()),
                locations,
                toInstant(entity.getLastScannedAt()),
                toInstant(entity.getCreatedAt()),
                toInstant(entity.getUpdatedAt()));
    }

    /** Instant → LocalDateTime 변환 (UTC 기준) */
    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }

    /** LocalDateTime → Instant 변환 (UTC 기준) */
    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }

    /** List<String> → JSON String 변환 */
    private String toJson(List<String> locations) {
        if (locations == null || locations.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(locations);
        } catch (JsonProcessingException e) {
            log.warn("[PERMISSION-USAGE] Failed to serialize locations: {}", locations, e);
            return "[]";
        }
    }

    /** JSON String → List<String> 변환 */
    private List<String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("[PERMISSION-USAGE] Failed to deserialize locations: {}", json, e);
            return List.of();
        }
    }
}
