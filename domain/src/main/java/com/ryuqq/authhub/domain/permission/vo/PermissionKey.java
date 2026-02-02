package com.ryuqq.authhub.domain.permission.vo;

/**
 * PermissionKey - 권한 키 Value Object
 *
 * <p>권한을 식별하는 유니크 키를 나타내는 불변 값 객체입니다. "{resource}:{action}" 형식입니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>null이거나 빈 값 불가
 *   <li>최대 102자 (resource 50 + ":" + action 50 + 여유 1)
 *   <li>"{resource}:{action}" 형식 필수
 * </ul>
 *
 * <p><strong>예시:</strong>
 *
 * <ul>
 *   <li>user:read - 사용자 조회 권한
 *   <li>user:create - 사용자 생성 권한
 *   <li>organization:manage - 조직 관리 권한
 * </ul>
 *
 * @param value 권한 키 문자열
 * @author development-team
 * @since 1.0.0
 */
public record PermissionKey(String value) {

    private static final int MAX_LENGTH = 102;
    private static final String PATTERN = "^[a-z][a-z0-9-]*:[a-z][a-z0-9-]*$";

    public PermissionKey {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("permissionKey는 null이거나 빈 값일 수 없습니다");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("permissionKey는 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        if (!value.matches(PATTERN)) {
            throw new IllegalArgumentException(
                    "permissionKey는 '{resource}:{action}' 형식이어야 합니다: " + value);
        }
    }

    /**
     * 팩토리 메서드
     *
     * @param value 권한 키 문자열
     * @return PermissionKey 인스턴스
     */
    public static PermissionKey of(String value) {
        return new PermissionKey(value);
    }

    /**
     * Resource와 Action으로 PermissionKey 생성
     *
     * @param resource 리소스
     * @param action 행위
     * @return PermissionKey 인스턴스
     */
    public static PermissionKey of(Resource resource, Action action) {
        return new PermissionKey(resource.value() + ":" + action.value());
    }

    /**
     * Resource 부분 추출
     *
     * @return resource 문자열
     */
    public String extractResource() {
        return value.split(":")[0];
    }

    /**
     * Action 부분 추출
     *
     * @return action 문자열
     */
    public String extractAction() {
        return value.split(":")[1];
    }
}
