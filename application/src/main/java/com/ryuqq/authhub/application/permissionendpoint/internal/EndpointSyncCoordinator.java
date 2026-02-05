package com.ryuqq.authhub.application.permissionendpoint.internal;

import com.ryuqq.authhub.application.common.time.TimeProvider;
import com.ryuqq.authhub.application.permission.manager.PermissionCommandManager;
import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand;
import com.ryuqq.authhub.application.permissionendpoint.dto.command.SyncEndpointsCommand.EndpointSyncItem;
import com.ryuqq.authhub.application.permissionendpoint.dto.response.SyncEndpointsResult;
import com.ryuqq.authhub.application.permissionendpoint.factory.EndpointSyncCommandFactory;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointCommandManager;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.application.role.manager.RoleReadManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionCommandManager;
import com.ryuqq.authhub.application.rolepermission.manager.RolePermissionReadManager;
import com.ryuqq.authhub.application.service.manager.ServiceReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.role.aggregate.Role;
import com.ryuqq.authhub.domain.role.id.RoleId;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.rolepermission.aggregate.RolePermission;
import com.ryuqq.authhub.domain.service.aggregate.Service;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import com.ryuqq.authhub.domain.service.vo.ServiceCode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *   <li>serviceCode → serviceId 리졸브 및 Permission에 serviceId 설정
 *   <li>자동 Role-Permission 매핑 (SERVICE scope 기본 Role 기준)
 * </ul>
 *
 * <p><strong>처리 흐름:</strong>
 *
 * <ol>
 *   <li>serviceCode → serviceId 리졸브
 *   <li>요청에서 permissionKey 목록 추출
 *   <li>기존 Permission IN절 한방 조회
 *   <li>누락된 Permission Factory로 생성 → 저장 (serviceId 포함)
 *   <li>permissionKey → permissionId 매핑 완성
 *   <li>기존 PermissionEndpoint IN절 한방 조회
 *   <li>누락된 PermissionEndpoint Factory로 생성 → 저장
 *   <li>자동 Role-Permission 매핑 (새로 생성된 Permission에 대해)
 * </ol>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class EndpointSyncCoordinator {

    private static final Logger log = LoggerFactory.getLogger(EndpointSyncCoordinator.class);

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_EDITOR = "EDITOR";
    private static final String ROLE_VIEWER = "VIEWER";

    private final PermissionReadManager permissionReadManager;
    private final PermissionCommandManager permissionCommandManager;
    private final PermissionEndpointReadManager permissionEndpointReadManager;
    private final PermissionEndpointCommandManager permissionEndpointCommandManager;
    private final EndpointSyncCommandFactory factory;
    private final ServiceReadManager serviceReadManager;
    private final RoleReadManager roleReadManager;
    private final RolePermissionReadManager rolePermissionReadManager;
    private final RolePermissionCommandManager rolePermissionCommandManager;
    private final TimeProvider timeProvider;

    public EndpointSyncCoordinator(
            PermissionReadManager permissionReadManager,
            PermissionCommandManager permissionCommandManager,
            PermissionEndpointReadManager permissionEndpointReadManager,
            PermissionEndpointCommandManager permissionEndpointCommandManager,
            EndpointSyncCommandFactory factory,
            ServiceReadManager serviceReadManager,
            RoleReadManager roleReadManager,
            RolePermissionReadManager rolePermissionReadManager,
            RolePermissionCommandManager rolePermissionCommandManager,
            TimeProvider timeProvider) {
        this.permissionReadManager = permissionReadManager;
        this.permissionCommandManager = permissionCommandManager;
        this.permissionEndpointReadManager = permissionEndpointReadManager;
        this.permissionEndpointCommandManager = permissionEndpointCommandManager;
        this.factory = factory;
        this.serviceReadManager = serviceReadManager;
        this.roleReadManager = roleReadManager;
        this.rolePermissionReadManager = rolePermissionReadManager;
        this.rolePermissionCommandManager = rolePermissionCommandManager;
        this.timeProvider = timeProvider;
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
            return SyncEndpointsResult.of(command.serviceName(), 0, 0, 0, 0, 0);
        }

        // 0. serviceCode → serviceId 리졸브
        ServiceId serviceId = resolveServiceId(command.serviceCode());

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
                        itemsByPermissionKey.keySet().stream().toList(),
                        itemsByPermissionKey,
                        serviceId);

        // 3. PermissionEndpoint 동기화 (IN절 조회 + 필터링 + 저장)
        EndpointSyncResultInternal endpointResult =
                syncEndpoints(
                        command.serviceName(), items, permissionResult.permissionKeyToIdMap());

        // 4. 자동 Role-Permission 매핑
        int mappedCount = 0;
        if (serviceId != null && permissionResult.createdCount() > 0) {
            mappedCount =
                    autoMapRolePermissions(
                            serviceId,
                            permissionResult.createdPermissionKeyToIdMap(),
                            itemsByPermissionKey);
        }

        return SyncEndpointsResult.of(
                command.serviceName(),
                items.size(),
                permissionResult.createdCount(),
                endpointResult.createdCount(),
                endpointResult.skippedCount(),
                mappedCount);
    }

    /**
     * serviceCode → ServiceId 리졸브
     *
     * @param serviceCode 서비스 코드 (nullable)
     * @return ServiceId 또는 null
     */
    private ServiceId resolveServiceId(String serviceCode) {
        ServiceCode code = ServiceCode.fromNullable(serviceCode);
        if (code == null) {
            return null;
        }

        Optional<Service> service = serviceReadManager.findByCodeOptional(code);
        if (service.isEmpty()) {
            log.warn(
                    "Service not found for serviceCode: {}. Proceeding without serviceId.",
                    serviceCode);
            return null;
        }

        return ServiceId.of(service.get().serviceIdValue());
    }

    /**
     * Permission 동기화
     *
     * <p>IN절로 기존 Permission 조회 → 누락된 것만 생성
     *
     * @param permissionKeys 필요한 permissionKey 목록
     * @param itemsByPermissionKey permissionKey → EndpointSyncItem 매핑
     * @param serviceId 서비스 ID (nullable)
     * @return Permission 동기화 결과
     */
    private PermissionSyncResult syncPermissions(
            List<String> permissionKeys,
            Map<String, EndpointSyncItem> itemsByPermissionKey,
            ServiceId serviceId) {
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
        Map<String, Long> createdPermissionKeyToIdMap = new HashMap<>();

        // 4. 누락된 Permission 생성 및 저장 (serviceId 포함)
        if (!missingKeys.isEmpty()) {
            List<Permission> newPermissions =
                    factory.createMissingPermissions(missingKeys, itemsByPermissionKey, serviceId);

            // 저장 및 매핑 추가
            Map<String, Long> newKeyToIdMap =
                    permissionCommandManager.persistAllAndReturnKeyToIdMap(newPermissions);
            permissionKeyToIdMap.putAll(newKeyToIdMap);
            createdPermissionKeyToIdMap.putAll(newKeyToIdMap);
            createdCount = newPermissions.size();
        }

        return new PermissionSyncResult(
                permissionKeyToIdMap, createdPermissionKeyToIdMap, createdCount);
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

    /**
     * 자동 Role-Permission 매핑
     *
     * <p>새로 생성된 Permission의 action 패턴에 따라 SERVICE scope 기본 Role에 자동 매핑합니다.
     *
     * <ul>
     *   <li>read → ADMIN, EDITOR, VIEWER
     *   <li>create, update → ADMIN, EDITOR
     *   <li>delete, 기타 → ADMIN only
     * </ul>
     *
     * @param serviceId 서비스 ID
     * @param createdPermissionKeyToIdMap 새로 생성된 permissionKey → permissionId 매핑
     * @param itemsByPermissionKey permissionKey → EndpointSyncItem 매핑 (action 참조용)
     * @return 생성된 Role-Permission 매핑 수
     */
    private int autoMapRolePermissions(
            ServiceId serviceId,
            Map<String, Long> createdPermissionKeyToIdMap,
            Map<String, EndpointSyncItem> itemsByPermissionKey) {
        // 1. SERVICE scope 기본 Role 3개 조회 (없으면 스킵)
        Optional<Role> adminRole = findServiceRole(serviceId, ROLE_ADMIN);
        Optional<Role> editorRole = findServiceRole(serviceId, ROLE_EDITOR);
        Optional<Role> viewerRole = findServiceRole(serviceId, ROLE_VIEWER);

        if (adminRole.isEmpty()) {
            log.info(
                    "No ADMIN role found for serviceId: {}. Skipping auto role-permission mapping.",
                    serviceId.value());
            return 0;
        }

        Instant now = timeProvider.now();
        List<RolePermission> newMappings = new ArrayList<>();

        for (Map.Entry<String, Long> entry : createdPermissionKeyToIdMap.entrySet()) {
            String permissionKey = entry.getKey();
            Long permissionIdValue = entry.getValue();
            PermissionId permissionId = PermissionId.of(permissionIdValue);

            String action = extractAction(permissionKey);
            List<Role> targetRoles =
                    determineTargetRoles(action, adminRole, editorRole, viewerRole);

            for (Role role : targetRoles) {
                newMappings.add(
                        RolePermission.create(RoleId.of(role.roleIdValue()), permissionId, now));
            }
        }

        if (newMappings.isEmpty()) {
            return 0;
        }

        // 2. 중복 체크 - 이미 매핑된 것 제외
        List<RolePermission> filteredMappings = filterExistingMappings(newMappings);

        if (filteredMappings.isEmpty()) {
            return 0;
        }

        // 3. 벌크 저장
        rolePermissionCommandManager.persistAll(filteredMappings);
        log.info(
                "Auto-mapped {} role-permission relationships for serviceId: {}",
                filteredMappings.size(),
                serviceId.value());

        return filteredMappings.size();
    }

    /**
     * SERVICE scope Role 조회 (Optional)
     *
     * <p>예외를 던지지 않는 Optional 반환 메서드를 사용하여 트랜잭션 rollback-only 마킹을 방지합니다.
     *
     * @param serviceId 서비스 ID
     * @param roleName 역할 이름
     * @return Role (Optional)
     */
    private Optional<Role> findServiceRole(ServiceId serviceId, String roleName) {
        return roleReadManager.findOptionalByTenantIdAndServiceIdAndName(
                null, serviceId, RoleName.of(roleName));
    }

    /**
     * permissionKey에서 action 추출
     *
     * @param permissionKey 권한 키 (예: "product:create")
     * @return action 문자열
     */
    private String extractAction(String permissionKey) {
        String[] parts = permissionKey.split(":");
        return parts.length == 2 ? parts[1] : permissionKey;
    }

    /**
     * action 패턴에 따라 매핑 대상 Role 결정
     *
     * @param action action 문자열
     * @param adminRole ADMIN Role (Optional)
     * @param editorRole EDITOR Role (Optional)
     * @param viewerRole VIEWER Role (Optional)
     * @return 매핑 대상 Role 목록
     */
    private List<Role> determineTargetRoles(
            String action,
            Optional<Role> adminRole,
            Optional<Role> editorRole,
            Optional<Role> viewerRole) {
        List<Role> targetRoles = new ArrayList<>();

        // ADMIN은 항상 포함
        adminRole.ifPresent(targetRoles::add);

        switch (action.toLowerCase()) {
            case "read", "list", "search", "get" -> {
                // read 계열 → ADMIN, EDITOR, VIEWER
                editorRole.ifPresent(targetRoles::add);
                viewerRole.ifPresent(targetRoles::add);
            }
            case "create", "update", "write", "edit" -> {
                // create/update 계열 → ADMIN, EDITOR
                editorRole.ifPresent(targetRoles::add);
            }
            default -> {
                // delete 및 기타 → ADMIN only (이미 추가됨)
            }
        }

        return targetRoles;
    }

    /**
     * 이미 존재하는 매핑 필터링
     *
     * @param newMappings 생성 예정 매핑 목록
     * @return 중복 제거된 매핑 목록
     */
    private List<RolePermission> filterExistingMappings(List<RolePermission> newMappings) {
        // RoleId별로 그룹핑하여 조회 최소화
        Map<Long, List<RolePermission>> byRoleId =
                newMappings.stream().collect(Collectors.groupingBy(RolePermission::roleIdValue));

        List<RolePermission> filtered = new ArrayList<>();

        for (Map.Entry<Long, List<RolePermission>> entry : byRoleId.entrySet()) {
            RoleId roleId = RoleId.of(entry.getKey());
            List<RolePermission> mappings = entry.getValue();
            List<PermissionId> permissionIds =
                    mappings.stream().map(RolePermission::getPermissionId).toList();

            // 이미 부여된 permissionId 조회
            List<PermissionId> alreadyGranted =
                    rolePermissionReadManager.findGrantedPermissionIds(roleId, permissionIds);
            Set<Long> grantedSet =
                    alreadyGranted.stream().map(PermissionId::value).collect(Collectors.toSet());

            // 이미 부여된 것 제외
            mappings.stream()
                    .filter(rp -> !grantedSet.contains(rp.permissionIdValue()))
                    .forEach(filtered::add);
        }

        return filtered;
    }

    /**
     * Permission 동기화 결과 (내부용)
     *
     * @param permissionKeyToIdMap 전체 permissionKey → permissionId 매핑
     * @param createdPermissionKeyToIdMap 새로 생성된 permissionKey → permissionId 매핑
     * @param createdCount 생성된 Permission 수
     */
    private record PermissionSyncResult(
            Map<String, Long> permissionKeyToIdMap,
            Map<String, Long> createdPermissionKeyToIdMap,
            int createdCount) {}

    /** PermissionEndpoint 동기화 결과 (내부용) */
    private record EndpointSyncResultInternal(int createdCount, int skippedCount) {}
}
