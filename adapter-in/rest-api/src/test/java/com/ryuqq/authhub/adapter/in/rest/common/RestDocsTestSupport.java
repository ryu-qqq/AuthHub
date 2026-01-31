package com.ryuqq.authhub.adapter.in.rest.common;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * REST Docs 테스트 기반 클래스
 *
 * <p>모든 컨트롤러 테스트의 공통 설정을 제공합니다.
 *
 * <ul>
 *   <li>MockMvc + REST Docs 설정
 *   <li>JSON 직렬화 ObjectMapper
 *   <li>요청/응답 Pretty Print
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTestSupport {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUpRestDocs(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(
                                documentationConfiguration(restDocumentation)
                                        .operationPreprocessors()
                                        .withRequestDefaults(
                                                prettyPrint(),
                                                modifyHeaders()
                                                        .remove("X-CSRF-TOKEN")
                                                        .remove("Host")
                                                        .remove("Content-Length"))
                                        .withResponseDefaults(
                                                prettyPrint(),
                                                modifyHeaders()
                                                        .remove("X-Content-Type-Options")
                                                        .remove("X-XSS-Protection")
                                                        .remove("Cache-Control")
                                                        .remove("Pragma")
                                                        .remove("Expires")
                                                        .remove("X-Frame-Options")
                                                        .remove("Content-Length")
                                                        .remove("Vary")))
                        .build();
    }
}
