package com.ryuqq.authhub.application.userrole.service.query;

import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.application.userrole.dto.response.UserPermissionsResult;
import com.ryuqq.authhub.application.userrole.facade.UserRoleReadFacade;
import com.ryuqq.authhub.application.userrole.port.in.query.GetUserPermissionsUseCase;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;

/**
 * GetUserPermissionsService - Gateway용 사용자 권한 조회 서비스
 *
 * <p>Gateway가 사용자 인가 검증을 위해 역할/권한 정보를 조회합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Query Service는 조회만 수행
 *   <li>@Transactional(readOnly=true) 생략 가능 (읽기 전용)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class GetUserPermissionsService implements GetUserPermissionsUseCase {

    private final UserRoleReadFacade userRoleReadFacade;

    public GetUserPermissionsService(UserRoleReadFacade userRoleReadFacade) {
        this.userRoleReadFacade = userRoleReadFacade;
    }

    @Override
    public UserPermissionsResult getByUserId(String userId) {
        RolesAndPermissionsComposite composite =
                userRoleReadFacade.findRolesAndPermissionsByUserId(UserId.of(userId));

        Set<String> roles = composite.roleNames();
        Set<String> permissions = composite.permissionKeys();
        String hash = computeHash(roles, permissions);
        Instant generatedAt = Instant.now();

        return new UserPermissionsResult(userId, roles, permissions, hash, generatedAt);
    }

    private String computeHash(Set<String> roles, Set<String> permissions) {
        TreeSet<String> sortedRoles = new TreeSet<>(roles);
        TreeSet<String> sortedPermissions = new TreeSet<>(permissions);
        String content = String.join(",", sortedRoles) + "|" + String.join(",", sortedPermissions);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
