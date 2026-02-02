package com.ryuqq.authhub.application.permissionendpoint.internal;

import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.factory.EndpointSyncCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * EndpointSyncCoordinator - 엔드포인트 동기화 Coordinator
 *
 * <p>Permission과 PermissionEndpoint의 생성을 조율합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>IN절로 기존 Permission 한방 조회 및 필터링
 *   <li>IN절로 기존 PermissionEndpoint 한방 조회 및 필터링
 *   <li>Factory를 통한 도메인 객체 생성
 *   <li>벌크 동기화의 트랜잭션 일관성 보장
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>요청에서 permissionKey 목록 추출
 *   <li>기존 Permission IN절 한방 조회
 *   <li>누락된 Permission Factory로 생성 → 저장
 *   <li>permissionKey → permissionId 매핑 완성
 *   <li>기존 PermissionEndpoint IN절 한방 조회
 *   <li>누락된 PermissionEndpoint Factory로 생성 → 저장
 * </ol>
 *
 * <p><strong>트랜잭션 참고:</strong>
 *
 * <p>벌크 동기화 작업은 여러 도메인(Permission, PermissionEndpoint)을 동시에 생성하므로, Coordinator 레벨에서 트랜잭션을 관리하여 원자성을
 * 보장합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointSyncCoordinator {

    private final PermissionReadManager permissionReadManager;
    private final PermissionCommandManager permissionCommandManager;
    private final PermissionEndpointReadManager permissionEndpointReadManager;
    private final PermissionEndpointCommandManager permissionEndpointCommandManager;
    private final EndpointSyncCommandFactory factory;

    public EndpointSyncCoordinator(
            PermissionReadManager permissionReadManager,
            PermissionCommandManager permissionCommandManager,
            PermissionEndpointReadManager permissionEndpointReadManager,
            PermissionEndpointCommandManager permissionEndpointCommandManager,
            EndpointSyncCommandFactory factory) {
        this.permissionReadManager = permissionReadManager;
        this.permissionCommandManager = permissionCommandManager;
        this.permissionEndpointReadManager = permissionEndpointReadManager;
        this.permissionEndpointCommandManager = permissionEndpointCommandManager;
        this.factory = factory;
    }

    /**
     * 엔드포인트 동기화 조율
     *
     * <p>IN절 한방 조회 + 필터링 패턴으로 벌크 작업을 최적화합니다.
     *
     * @param command 동기화 Command
     * @return 동기화 결과
     */
    @Transactional
    public SyncEndpointsResult coordinate(SyncEndpointsCommand command) {
        List<EndpointSyncItem> items = command.endpoints();
        if (items == null || items.isEmpty()) {
            return SyncEndpointsResult.of(command.serviceName(), 0, 0, 0, 0);
        }

        // 1. permissionKey → EndpointSyncItem 매핑 생성
        Map<String, EndpointSyncItem> itemsByPermissionKey =
                items.stream()
                        .collect(
                                Collectors.toMap(
                                        EndpointSyncItem::permissionKey,
                                        Function.identity(),
                                        (existing, replacement) -> existing));

        // 2. Permission 동기화 (IN절 조회 + 필터링 + 저장)
        PermissionSyncResult permissionResult =
                syncPermissions(
                        itemsByPermissionKey.keySet().stream().toList(), itemsByPermissionKey);

        // 3. PermissionEndpoint 동기화 (IN절 조회 + 필터링 + 저장)
        EndpointSyncResultInternal endpointResult =
                syncEndpoints(
                        command.serviceName(), items, permissionResult.permissionKeyToIdMap());

        return SyncEndpointsResult.of(
                command.serviceName(),
                items.size(),
                permissionResult.createdCount(),
                endpointResult.createdCount(),
                endpointResult.skippedCount());
    }

    /**
     * Permission 동기화
     *
     * <p>IN절로 기존 Permission 조회 → 누락된 것만 생성
     *
     * @param permissionKeys 필요한 permissionKey 목록
     * @param itemsByPermissionKey permissionKey → EndpointSyncItem 매핑
     * @return Permission 동기화 결과
     */
    private PermissionSyncResult syncPermissions(
            List<String> permissionKeys, Map<String, EndpointSyncItem> itemsByPermissionKey) {
        // 1. IN절로 기존 Permission 한방 조회
        List<Permission> existingPermissions =
                permissionReadManager.findAllByPermissionKeys(permissionKeys);

        // 2. 기존 Permission의 permissionKey → permissionId 매핑
        Map<String, Long> permissionKeyToIdMap = new HashMap<>();
        Set<String> existingKeys =
                existingPermissions.stream()
                        .map(
                                permission -> {
                                    permissionKeyToIdMap.put(
                                            permission.permissionKeyValue(),
                                            permission.permissionIdValue());
                                    return permission.permissionKeyValue();
                                })
                        .collect(Collectors.toSet());

        // 3. 누락된 permissionKey 필터링
        List<String> missingKeys =
                permissionKeys.stream().filter(key -> !existingKeys.contains(key)).toList();

        int createdCount = 0;

        // 4. 누락된 Permission 생성 및 저장
        if (!missingKeys.isEmpty()) {
            List<Permission> newPermissions =
                    factory.createMissingPermissions(missingKeys, itemsByPermissionKey);

            // 저장 및 매핑 추가
            Map<String, Long> newKeyToIdMap =
                    permissionCommandManager.persistAllAndReturnKeyToIdMap(newPermissions);
            permissionKeyToIdMap.putAll(newKeyToIdMap);
            createdCount = newPermissions.size();
        }

        return new PermissionSyncResult(permissionKeyToIdMap, createdCount);
    }

    /**
     * PermissionEndpoint 동기화
     *
     * <p>IN절로 기존 PermissionEndpoint 조회 → 누락된 것만 생성
     *
     * @param serviceName 서비스 이름
     * @param items 모든 EndpointSyncItem
     * @param permissionKeyToIdMap permissionKey → permissionId 매핑
     * @return PermissionEndpoint 동기화 결과
     */
    private EndpointSyncResultInternal syncEndpoints(
            String serviceName,
            List<EndpointSyncItem> items,
            Map<String, Long> permissionKeyToIdMap) {
        // 1. urlPattern 목록 추출
        List<String> urlPatterns = items.stream().map(EndpointSyncItem::pathPattern).toList();

        // 2. IN절로 기존 PermissionEndpoint 한방 조회 후 서비스명으로 필터링
        List<PermissionEndpoint> existingEndpoints =
                permissionEndpointReadManager.findAllByUrlPatterns(urlPatterns).stream()
                        .filter(endpoint -> serviceName.equals(endpoint.serviceNameValue()))
                        .toList();

        // 3. 기존 엔드포인트의 (serviceName, urlPattern, httpMethod) Set 생성
        Set<String> existingEndpointKeys =
                existingEndpoints.stream()
                        .map(
                                endpoint ->
                                        endpoint.serviceNameValue()
                                                + "|"
                                                + endpoint.urlPatternValue()
                                                + "|"
                                                + endpoint.httpMethodValue())
                        .collect(Collectors.toSet());

        // 4. 누락된 엔드포인트 필터링 (serviceName 포함 키 비교)
        List<EndpointSyncItem> newEndpointItems =
                items.stream()
                        .filter(
                                item -> {
                                    String key =
                                            serviceName
                                                    + "|"
                                                    + item.pathPattern()
                                                    + "|"
                                                    + item.httpMethod();
                                    return !existingEndpointKeys.contains(key);
                                })
                        .toList();

        int skippedCount = items.size() - newEndpointItems.size();
        int createdCount = 0;

        // 5. 누락된 PermissionEndpoint 생성 및 저장
        if (!newEndpointItems.isEmpty()) {
            List<PermissionEndpoint> newEndpoints =
                    factory.createMissingEndpoints(
                            serviceName, newEndpointItems, permissionKeyToIdMap);
            permissionEndpointCommandManager.persistAll(newEndpoints);
            createdCount = newEndpoints.size();
        }

        return new EndpointSyncResultInternal(createdCount, skippedCount);
    }

    /** Permission 동기화 결과 (내부용) */
    private record PermissionSyncResult(Map<String, Long> permissionKeyToIdMap, int createdCount) {}

    /** PermissionEndpoint 동기화 결과 (내부용) */
    private record EndpointSyncResultInternal(int createdCount, int skippedCount) {}
}
