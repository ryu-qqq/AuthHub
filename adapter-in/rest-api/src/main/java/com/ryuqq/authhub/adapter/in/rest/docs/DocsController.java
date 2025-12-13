package com.ryuqq.authhub.adapter.in.rest.docs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API 문서 리다이렉트 컨트롤러
 *
 * <p>REST Docs 및 Swagger UI로 리다이렉트합니다.
 *
 * @author development-team
 * @since 1.0.0
 */
@Controller
public class DocsController {

    /**
     * REST Docs 메인 페이지로 리다이렉트
     *
     * @return REST Docs index.html로 리다이렉트
     */
    @GetMapping("/docs")
    public String restDocs() {
        return "redirect:/docs/index.html";
    }

    /**
     * Swagger UI로 리다이렉트
     *
     * @return Swagger UI index.html로 리다이렉트
     */
    @GetMapping("/swagger")
    public String swagger() {
        return "redirect:/swagger-ui/index.html";
    }
}
