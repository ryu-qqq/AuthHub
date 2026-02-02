# AuthHub Gateway Client SDK 사용 가이드

게이트웨이에서 AuthHub의 Permission Spec API를 호출하기 위한 SDK 사용 가이드입니다.

---

## 1. 의존성 추가

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    implementation("com.ryuqq.authhub:authhub-sdk-core:1.1.0")
}
```

### Gradle (Groovy)
```groovy
dependencies {
    implementation 'com.ryuqq.authhub:authhub-sdk-core:1.1.0'
}
```

### Maven
```xml
<dependency>
    <groupId>com.ryuqq.authhub</groupId>
    <artifactId>authhub-sdk-core</artifactId>
    <version>1.1.0</version>
</dependency>
```

---

## 2. GatewayClient 생성

### 기본 사용법

```java
import com.ryuqq.authhub.sdk.client.GatewayClient;
import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.internal.EndpointPermissionSpecList;

// 클라이언트 생성
GatewayClient client = GatewayClient.builder()
    .baseUrl("https://authhub.example.com")  // AuthHub 서버 URL
    .serviceName("gateway")                   // 서비스 이름
    .serviceToken("your-service-token")       // 서비스 토큰
    .build();

// Permission Spec 조회
ApiResponse<EndpointPermissionSpecList> response = client.internal().getPermissionSpec();

// 응답 처리
if (response.success()) {
    EndpointPermissionSpecList specList = response.data();
    String version = specList.version();
    Instant updatedAt = specList.updatedAt();
    List<EndpointPermissionSpec> endpoints = specList.endpoints();

    for (EndpointPermissionSpec spec : endpoints) {
        System.out.println("Service: " + spec.serviceName());
        System.out.println("Path: " + spec.pathPattern());
        System.out.println("Method: " + spec.httpMethod());
        System.out.println("Permissions: " + spec.requiredPermissions());
        System.out.println("Public: " + spec.isPublic());
    }
}
```

### 타임아웃 설정

```java
import java.time.Duration;

GatewayClient client = GatewayClient.builder()
    .baseUrl("https://authhub.example.com")
    .serviceName("gateway")
    .serviceToken("your-service-token")
    .connectTimeout(Duration.ofSeconds(5))   // 연결 타임아웃 (기본: 5초)
    .readTimeout(Duration.ofSeconds(30))     // 읽기 타임아웃 (기본: 30초)
    .build();
```

---

## 3. Spring Boot 통합

### Configuration 클래스

```java
import com.ryuqq.authhub.sdk.client.GatewayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthHubClientConfig {

    @Value("${authhub.base-url}")
    private String baseUrl;

    @Value("${authhub.service-name}")
    private String serviceName;

    @Value("${authhub.service-token}")
    private String serviceToken;

    @Bean
    public GatewayClient gatewayClient() {
        return GatewayClient.builder()
            .baseUrl(baseUrl)
            .serviceName(serviceName)
            .serviceToken(serviceToken)
            .build();
    }
}
```

### application.yml 설정

```yaml
authhub:
  base-url: ${AUTHHUB_BASE_URL:http://localhost:8080}
  service-name: gateway
  service-token: ${AUTHHUB_SERVICE_TOKEN}
```

### 환경별 설정

```yaml
# application-local.yml
authhub:
  base-url: http://localhost:8080

# application-stage.yml
authhub:
  base-url: http://authhub-web-api-stage.connectly.local:8080

# application-prod.yml
authhub:
  base-url: http://authhub-web-api-prod.connectly.local:8080
```

---

## 4. 응답 모델

### EndpointPermissionSpecList

| 필드 | 타입 | 설명 |
|------|------|------|
| `version` | `String` | 스펙 버전 (ETag용, epoch millis) |
| `updatedAt` | `Instant` | 마지막 수정 시간 |
| `endpoints` | `List<EndpointPermissionSpec>` | 엔드포인트 목록 |

### EndpointPermissionSpec

| 필드 | 타입 | 설명 | 예시 |
|------|------|------|------|
| `serviceName` | `String` | 서비스 이름 | `"product-service"` |
| `pathPattern` | `String` | URL 패턴 | `"/api/v1/users/{id}"` |
| `httpMethod` | `String` | HTTP 메서드 | `"GET"`, `"POST"` |
| `requiredPermissions` | `List<String>` | 필요 권한 목록 | `["user:read"]` |
| `requiredRoles` | `List<String>` | 필요 역할 목록 | `["ADMIN"]` |
| `isPublic` | `boolean` | 공개 엔드포인트 여부 | `true`/`false` |
| `description` | `String` | 엔드포인트 설명 | `"사용자 조회"` |

---

## 5. 예외 처리

```java
import com.ryuqq.authhub.sdk.exception.*;

try {
    ApiResponse<EndpointPermissionSpecList> response = client.internal().getPermissionSpec();
} catch (AuthHubUnauthorizedException e) {
    // 401: 서비스 토큰 인증 실패
    log.error("인증 실패: {}", e.getMessage());
} catch (AuthHubForbiddenException e) {
    // 403: 권한 없음
    log.error("권한 없음: {}", e.getMessage());
} catch (AuthHubServerException e) {
    // 5xx: 서버 오류
    log.error("서버 오류: {}", e.getMessage());
} catch (AuthHubException e) {
    // 기타 오류
    log.error("API 호출 실패: {}", e.getMessage());
}
```

---

## 6. 캐싱 권장 사항

Permission Spec은 자주 변경되지 않으므로 캐싱을 권장합니다.

### Redis 캐싱 예시

```java
@Service
public class PermissionSpecCacheService {

    private final GatewayClient gatewayClient;
    private final RedisTemplate<String, PermissionSpec> redisTemplate;

    private static final String CACHE_KEY = "permission:spec";
    private static final Duration TTL = Duration.ofHours(1);

    public PermissionSpec getPermissionSpec() {
        // 1. Redis에서 조회
        PermissionSpec cached = redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            return cached;
        }

        // 2. Cache Miss → AuthHub 조회
        ApiResponse<EndpointPermissionSpecList> response =
            gatewayClient.internal().getPermissionSpec();

        if (response.success()) {
            PermissionSpec spec = convertToSpec(response.data());

            // 3. Redis에 저장 (TTL: 1시간)
            redisTemplate.opsForValue().set(CACHE_KEY, spec, TTL);
            return spec;
        }

        throw new RuntimeException("Permission Spec 조회 실패");
    }

    public void invalidateCache() {
        redisTemplate.delete(CACHE_KEY);
    }
}
```

### 권장 TTL 설정

| 캐시 대상 | 권장 TTL | 이유 |
|----------|---------|------|
| Permission Spec | 1시간 | 엔드포인트 정의는 자주 변경 안 됨 |
| Permission Hash (사용자별) | 30분 | 권한 변경 반영 필요 |

---

## 7. WebFlux 환경 (Reactive)

현재 SDK는 **동기(Blocking) 방식**입니다.
WebFlux 환경에서는 `Mono.fromCallable()`로 래핑하여 사용하세요.

```java
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ReactivePermissionSpecService {

    private final GatewayClient gatewayClient;

    public Mono<EndpointPermissionSpecList> getPermissionSpec() {
        return Mono.fromCallable(() -> gatewayClient.internal().getPermissionSpec())
            .subscribeOn(Schedulers.boundedElastic())  // Blocking 호출을 별도 스레드에서 실행
            .filter(ApiResponse::success)
            .map(ApiResponse::data);
    }
}
```

> **Note**: 향후 Reactive 전용 SDK (`GatewayReactiveClient`)를 제공할 예정입니다.

---

## 8. 마이그레이션 가이드

### 기존 WebClient 방식 → SDK 방식

**Before (직접 WebClient 구현)**:
```java
webClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/api/v1/internal/endpoint-permissions/spec")
        .queryParam("serviceName", "gateway")
        .build())
    .header("X-Service-Token", serviceToken)
    .retrieve()
    .bodyToMono(PermissionSpecResponse.class)
    .map(this::parseResponse)
    ...
```

**After (SDK 사용)**:
```java
ApiResponse<EndpointPermissionSpecList> response =
    gatewayClient.internal().getPermissionSpec();
```

### 변경 사항

1. **의존성 추가**: `authhub-sdk-core:1.1.0`
2. **GatewayClient Bean 등록**
3. **기존 WebClient 코드 삭제**
4. **응답 DTO 변경**: SDK의 `EndpointPermissionSpecList` 사용

---

## 9. 문의

- **SDK 관련 문의**: AuthHub 개발팀
- **버그 리포트**: GitHub Issues
- **API 문서**: `/swagger-ui.html`

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.1.0 | 2026-02-02 | GatewayClient 추가 (Internal API 지원) |
| 1.0.0 | 2025-01-15 | 최초 릴리즈 (AuthHubClient) |
