package com.ryuqq.authhub.adapter.in.rest.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.json.ProblemDetailJacksonMixin;

/**
 * Jackson ObjectMapper 설정
 *
 * <p>RFC 7807 ProblemDetail의 extension properties가 루트 레벨에 평탄화되도록 ProblemDetailJacksonMixin을 등록합니다.
 *
 * <p><strong>Mixin 적용 전:</strong>
 *
 * <pre>{@code
 * {
 *   "type": "about:blank",
 *   "status": 400,
 *   "properties": {
 *     "code": "VALIDATION_FAILED",
 *     "timestamp": "..."
 *   }
 * }
 * }</pre>
 *
 * <p><strong>Mixin 적용 후:</strong>
 *
 * <pre>{@code
 * {
 *   "type": "about:blank",
 *   "status": 400,
 *   "code": "VALIDATION_FAILED",
 *   "timestamp": "..."
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Configuration
public class JacksonConfig {

    /**
     * 애플리케이션 전역 ObjectMapper 설정
     *
     * <p>다음 기능을 설정합니다:
     *
     * <ul>
     *   <li>JavaTimeModule - Java 8 Date/Time API (LocalDateTime, Instant 등) 지원
     *   <li>ParameterNamesModule - Java Record 파라미터 이름 인식
     *   <li>ProblemDetailJacksonMixin - RFC 7807 extension properties 평탄화
     *   <li>WRITE_DATES_AS_TIMESTAMPS 비활성화 - ISO-8601 형식 사용
     *   <li>FAIL_ON_UNKNOWN_PROPERTIES 비활성화 - 알 수 없는 속성 무시
     *   <li>NON_NULL 포함 정책 - null 값 제외
     * </ul>
     *
     * @return 설정된 ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Java 8 Date/Time 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new ParameterNamesModule());

        // ProblemDetail Mixin 등록
        objectMapper.addMixIn(ProblemDetail.class, ProblemDetailJacksonMixin.class);

        // 직렬화 설정
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 역직렬화 설정
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // null 값 제외
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }
}
