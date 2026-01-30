package com.ryuqq.setof.adapter.out.persistence.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * PersistenceObjectMapper - Persistence Layer 전용 ObjectMapper 래퍼
 *
 * <p>Persistence Layer에서 JSON 파싱/직렬화를 위한 중앙 관리 ObjectMapper입니다.
 *
 * <p><strong>사용 목적:</strong>
 *
 * <ul>
 *   <li>JSON 파싱 에러 처리 중앙화
 *   <li>Persistence Layer 전용 설정 관리
 *   <li>에러 발생 시 일관된 예외 처리
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * @Component
 * public class TechStackEntityMapper {
 *     private final PersistenceObjectMapper objectMapper;
 *
 *     public TechStackEntityMapper(PersistenceObjectMapper objectMapper) {
 *         this.objectMapper = objectMapper;
 *     }
 *
 *     private List<String> parseJsonArray(String json) {
 *         return objectMapper.readValue(json, new TypeReference<List<String>>() {});
 *     }
 * }
 * }</pre>
 *
 * @author ryu-qqq
 */
@Component
public class PersistenceObjectMapper {

    private static final TypeReference<List<String>> LIST_STRING_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public PersistenceObjectMapper(
            @org.springframework.beans.factory.annotation.Qualifier("persistenceJsonObjectMapper")
                    ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * JSON 문자열을 객체로 파싱
     *
     * @param json JSON 문자열
     * @param typeReference 타입 참조
     * @param <T> 타입
     * @return 파싱된 객체
     * @throws IllegalArgumentException JSON 파싱 실패 시
     */
    public <T> T readValue(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("JSON string cannot be null or blank");
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse JSON: " + json + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * JSON 문자열을 List<String>으로 파싱
     *
     * <p>Persistence Layer에서 가장 많이 사용되는 JSON 배열 파싱을 위한 편의 메서드입니다.
     *
     * @param json JSON 배열 문자열
     * @return List<String>
     * @throws IllegalArgumentException JSON 파싱 실패 시
     */
    public List<String> readValueAsStringList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, LIST_STRING_TYPE);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to parse JSON array: " + json + ". Reason: " + e.getMessage(), e);
        }
    }

    /**
     * 객체를 JSON 문자열로 직렬화
     *
     * @param value 직렬화할 객체
     * @param <T> 타입
     * @return JSON 문자열
     * @throws IllegalArgumentException JSON 직렬화 실패 시
     */
    public <T> String writeValueAsString(T value) {
        if (value == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to serialize object to JSON. Reason: " + e.getMessage(), e);
        }
    }

    /**
     * List<String>을 JSON 배열 문자열로 직렬화
     *
     * <p>Persistence Layer에서 가장 많이 사용되는 JSON 배열 직렬화를 위한 편의 메서드입니다.
     *
     * @param list 직렬화할 리스트
     * @return JSON 배열 문자열
     * @throws IllegalArgumentException JSON 직렬화 실패 시
     */
    public String writeValueAsString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "Failed to serialize list to JSON array. Reason: " + e.getMessage(), e);
        }
    }
}
