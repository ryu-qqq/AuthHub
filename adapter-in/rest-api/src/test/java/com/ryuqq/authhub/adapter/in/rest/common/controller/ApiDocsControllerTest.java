package com.ryuqq.authhub.adapter.in.rest.common.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.authhub.adapter.in.rest.common.ControllerTestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ApiDocsController 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@WebMvcTest(ApiDocsController.class)
@Import(ControllerTestSecurityConfig.class)
@DisplayName("ApiDocsController 단위 테스트")
class ApiDocsControllerTest {

    @Autowired private MockMvc mockMvc;

    @Nested
    @DisplayName("GET /api/admin/docs")
    class GetApiDocs {

        @Test
        @DisplayName("index.html로 리다이렉트한다")
        void shouldRedirectToIndexHtml() throws Exception {
            mockMvc.perform(get("/api/admin/docs"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/api/admin/docs/index.html"));
        }
    }
}
