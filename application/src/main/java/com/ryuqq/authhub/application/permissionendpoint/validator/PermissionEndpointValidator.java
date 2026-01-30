package com.ryuqq.authhub.application.permissionendpoint.validator;

import com.ryuqq.authhub.application.permission.manager.PermissionReadManager;
import com.ryuqq.authhub.application.permissionendpoint.manager.PermissionEndpointReadManager;
import com.ryuqq.authhub.domain.permission.aggregate.Permission;
import com.ryuqq.authhub.domain.permission.id.PermissionId;
import com.ryuqq.authhub.domain.permissionendpoint.aggregate.PermissionEndpoint;
import com.ryuqq.authhub.domain.permissionendpoint.exception.DuplicatePermissionEndpointException;
import com.ryuqq.authhub.domain.permissionendpoint.id.PermissionEndpointId;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * PermissionEndpointValidator - PermissionEndpoint 유효성 검증 Validator
 *
 * <p>PermissionEndpoint 생성/수정 시 비즈니스 규칙을 검증합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>ReadManager만 의존 (Port 직접 의존 금지)
 *   <li>검증 실패 시 도메인 예외 throw
 *   <li>비즈니스 로직 금지 (검증만 담당)
 * </ul>
 *
 * <p>VAL-003: Validator는 ReadManager만 의존합니다.
 *
 * <p>APP-DEP-004: Validator가 자기 도메인의 ReadManager만 의존합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionEndpointValidator {

    private final PermissionReadManager permissionReadManager;
    private final PermissionEndpointReadManager permissionEndpointReadManager;

    public PermissionEndpointValidator(
            PermissionReadManager permissionReadManager,
            PermissionEndpointReadManager permissionEndpointReadManager) {
        this.permissionReadManager = permissionReadManager;
        this.permissionEndpointReadManager = permissionEndpointReadManager;
    }

    /**
     * Permission 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param permissionId 권한 ID
     * @return Permission 조회된 도메인 객체
     * @throws com.ryuqq.authhub.domain.permission.exception.PermissionNotFoundException 존재하지 않는 경우
     */
    public Permission validatePermissionExists(PermissionId permissionId) {
        return permissionReadManager.findById(permissionId);
    }

    /**
     * PermissionEndpoint 조회 및 존재 여부 검증
     *
     * <p>APP-VAL-001: 검증 성공 시 조회한 Domain 객체를 반환합니다.
     *
     * @param permissionEndpointId 엔드포인트 ID
     * @return PermissionEndpoint 조회된 도메인 객체
     * @throws
     *     com.ryuqq.authhub.domain.permissionendpoint.exception.PermissionEndpointNotFoundException
     *     존재하지 않는 경우
     */
    public PermissionEndpoint findExistingOrThrow(PermissionEndpointId permissionEndpointId) {
        return permissionEndpointReadManager.findById(permissionEndpointId);
    }

    /**
     * URL 패턴과 HTTP 메서드 조합의 중복 검증
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @throws DuplicatePermissionEndpointException 중복된 경우
     */
    public void validateNoDuplicate(String urlPattern, HttpMethod httpMethod) {
        if (permissionEndpointReadManager.existsByUrlPatternAndHttpMethod(urlPattern, httpMethod)) {
            throw new DuplicatePermissionEndpointException(urlPattern, httpMethod.name());
        }
    }

    /**
     * 수정 시 URL 패턴과 HTTP 메서드 조합의 중복 검증 (자기 자신 제외)
     *
     * @param permissionEndpointId 수정 대상 엔드포인트 ID
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @throws DuplicatePermissionEndpointException 중복된 경우
     */
    public void validateNoDuplicateExcludeSelf(
            PermissionEndpointId permissionEndpointId, String urlPattern, HttpMethod httpMethod) {
        if (urlPattern == null || httpMethod == null) {
            return;
        }
        permissionEndpointReadManager
                .findByUrlPatternAndHttpMethod(urlPattern, httpMethod)
                .ifPresent(
                        existing -> {
                            if (!existing.getPermissionEndpointId().equals(permissionEndpointId)) {
                                throw new DuplicatePermissionEndpointException(
                                        urlPattern, httpMethod.name());
                            }
                        });
    }
}
