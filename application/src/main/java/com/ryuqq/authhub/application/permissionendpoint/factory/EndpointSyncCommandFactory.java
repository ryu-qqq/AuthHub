package com.ryuqq.authhub.application.permissionendpoint.factory;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * EndpointSyncCommandFactory - 엔드포인트 동기화 Factory
 *
 * <p>동기화 시 필요한 Permission과 PermissionEndpoint 도메인 객체를 생성합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>permissionKey 파싱 (resource:action)
 *   <li>Permission 도메인 객체 생성
 *   <li>PermissionEndpoint 도메인 객체 생성
 *   <li>TimeProvider를 통한 현재 시간 주입
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>비즈니스 로직 금지 (단순 생성만)
 *   <li>검증 로직 금지 (Coordinator에서 수행)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointSyncCommandFactory {

    private final TimeProvider timeProvider;

    public EndpointSyncCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 누락된 Permission 목록 생성
     *
     * @param missingPermissionKeys 생성이 필요한 permissionKey 목록
     * @param itemsByPermissionKey permissionKey → EndpointSyncItem 매핑 (description 참조용)
     * @return 생성된 Permission 목록
     */
    public List<Permission> createMissingPermissions(
            List<String> missingPermissionKeys,
            Map<String, EndpointSyncItem> itemsByPermissionKey) {
        Instant now = timeProvider.now();
        return missingPermissionKeys.stream()
                .map(
                        permissionKey -> {
                            EndpointSyncItem item = itemsByPermissionKey.get(permissionKey);
                            String description =
                                    item != null ? item.description() : "Auto-created permission";
                            return createPermission(permissionKey, description, now);
                        })
                .toList();
    }

    /**
     * Permission 생성
     *
     * @param permissionKey 권한 키 (예: "product:create")
     * @param description 설명
     * @param now 현재 시간
     * @return 생성된 Permission
     */
    public Permission createPermission(String permissionKey, String description, Instant now) {
        String[] parts = parsePermissionKey(permissionKey);
        String resource = parts[0];
        String action = parts[1];
        return Permission.createCustom(resource, action, description, now);
    }

    /**
     * 누락된 PermissionEndpoint 목록 생성
     *
     * @param newEndpointItems 생성이 필요한 EndpointSyncItem 목록
     * @param permissionKeyToIdMap permissionKey → permissionId 매핑
     * @return 생성된 PermissionEndpoint 목록
     */
    public List<PermissionEndpoint> createMissingEndpoints(
            List<EndpointSyncItem> newEndpointItems, Map<String, Long> permissionKeyToIdMap) {
        Instant now = timeProvider.now();
        return newEndpointItems.stream()
                .map(
                        item -> {
                            Long permissionId = permissionKeyToIdMap.get(item.permissionKey());
                            if (permissionId == null) {
                                throw new IllegalStateException(
                                        "Permission not found for key: " + item.permissionKey());
                            }
                            return createPermissionEndpoint(item, permissionId, now);
                        })
                .toList();
    }

    /**
     * PermissionEndpoint 생성
     *
     * @param item EndpointSyncItem
     * @param permissionId 권한 ID
     * @param now 현재 시간
     * @return 생성된 PermissionEndpoint
     */
    public PermissionEndpoint createPermissionEndpoint(
            EndpointSyncItem item, Long permissionId, Instant now) {
        return PermissionEndpoint.create(
                permissionId,
                item.pathPattern(),
                HttpMethod.from(item.httpMethod()),
                item.description(),
                now);
    }

    /**
     * permissionKey 파싱
     *
     * @param permissionKey 권한 키 (예: "product:create")
     * @return [resource, action] 배열
     * @throws IllegalArgumentException 형식이 올바르지 않은 경우
     */
    private String[] parsePermissionKey(String permissionKey) {
        String[] parts = permissionKey.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid permissionKey format: "
                            + permissionKey
                            + ". Expected format: 'resource:action'");
        }
        return parts;
    }
}
