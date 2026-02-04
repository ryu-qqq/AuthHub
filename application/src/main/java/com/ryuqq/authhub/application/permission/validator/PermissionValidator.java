package com.ryuqq.authhub.application.permission.validator;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.service.id.ServiceId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * PermissionValidator - 권한 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * <p>MGR-001: 파라미터는 원시타입 대신 VO를 사용합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionValidator {

    private final PermissionReadManager readManager;

    public PermissionValidator(PermissionReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * Permission 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param id Permission ID (VO)
     * @return Permission 조회된 도메인 객체
     * @throws com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException 존재하지 않는 경우
     */
    public Permission findExistingOrThrow(PermissionId id) {
        return readManager.findById(id);
    }

    /**
     * 서비스 내 권한 키 중복 검증
     *
     * <p>동일 서비스 내에서 permissionKey 중복을 검증합니다.
     *
     * @param serviceId 서비스 ID
     * @param permissionKey 검증할 권한 키 (예: "user:read")
     * @throws DuplicatePermissionKeyException 중복 시
     */
    public void validateKeyNotDuplicated(ServiceId serviceId, String permissionKey) {
        if (readManager.existsByServiceIdAndPermissionKey(serviceId, permissionKey)) {
            throw new DuplicatePermissionKeyException(permissionKey);
        }
    }

    /**
     * Permission 다건 존재 검증 (IN절 일괄 조회)
     *
     * <p>요청된 모든 Permission ID가 존재하는지 한 번의 쿼리로 검증합니다. 존재하지 않는 ID가 있으면 첫 번째 누락된 ID에 대해 예외를 발생시킵니다.
     *
     * @param ids Permission ID 목록
     * @return 조회된 Permission 목록 (검증 통과)
     * @throws PermissionNotFoundException 존재하지 않는 ID가 있는 경우
     */
    public List<Permission> validateAllExist(List<PermissionId> ids) {
        List<Permission> permissions = readManager.findAllByIds(ids);

        // 조회된 Permission ID Set
        Set<Long> foundIds =
                permissions.stream()
                        .map(p -> p.getPermissionId().value())
                        .collect(Collectors.toSet());

        // 요청한 ID 중 조회되지 않은 ID 찾기
        ids.stream()
                .filter(id -> !foundIds.contains(id.value()))
                .findFirst()
                .ifPresent(
                        missingId -> {
                            throw new PermissionNotFoundException(missingId);
                        });

        return permissions;
    }
}
