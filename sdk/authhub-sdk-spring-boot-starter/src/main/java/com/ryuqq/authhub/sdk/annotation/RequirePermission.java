package com.ryuqq.authhub.sdk.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequirePermission - API 엔드포인트에 필요한 권한을 선언하는 어노테이션
 *
 * <p>이 어노테이션은 두 가지 목적으로 사용됩니다:
 *
 * <ol>
 *   <li><strong>문서화</strong>: 엔드포인트에 필요한 권한을 명시적으로 선언
 *   <li><strong>자동 동기화</strong>: EndpointSyncRunner가 스캔하여 AuthHub에 등록
 * </ol>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api/v1/users")
 * public class UserController {
 *
 *     @GetMapping("/{id}")
 *     @RequirePermission("user:read")
 *     public UserResponse getUser(@PathVariable Long id) {
 *         // ...
 *     }
 *
 *     @PostMapping
 *     @RequirePermission(value = "user:create", description = "사용자 생성")
 *     public UserResponse createUser(@RequestBody CreateUserRequest request) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <p><strong>권한 키 형식:</strong>
 *
 * <ul>
 *   <li>형식: {@code {resource}:{action}}
 *   <li>예시: "user:read", "user:create", "organization:manage"
 * </ul>
 *
 * <p><strong>주의사항:</strong>
 *
 * <ul>
 *   <li>이 어노테이션은 권한 체크를 직접 수행하지 않습니다
 *   <li>실제 권한 체크는 Gateway에서 수행됩니다
 *   <li>백엔드에서 추가 검증이 필요하면 @PreAuthorize와 함께 사용하세요
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 * @see com.ryuqq.authhub.sdk.sync.EndpointScanner
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 필요한 권한 키
     *
     * <p>형식: {@code {resource}:{action}}
     *
     * @return 권한 키 (예: "user:read", "product:create")
     */
    String value();

    /**
     * 권한 설명 (선택사항)
     *
     * <p>자동 동기화 시 Permission 생성에 사용됩니다.
     *
     * @return 권한 설명
     */
    String description() default "";
}
