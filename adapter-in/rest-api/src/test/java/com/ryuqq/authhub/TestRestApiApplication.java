package com.ryuqq.authhub;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST API 모듈 테스트용 Spring Boot Application 설정
 *
 * <p>adapter-in/rest-api 모듈의 슬라이스 테스트(@WebMvcTest 등)에서 사용됩니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "com.ryuqq.authhub.adapter.in.rest")
public class TestRestApiApplication {
}
