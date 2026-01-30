package com.ryuqq.authhub.adapter.in.rest.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API 문서 접근용 Controller
 *
 * <p>Spring REST Docs로 생성된 HTML 문서를 서빙합니다.
 *
 * <p><strong>API Gateway 라우팅 패턴:</strong>
 *
 * <ul>
 *   <li>Gateway: /api/admin/** → 이 서버 (prefix strip 없음)
 *   <li>REST Docs: /api/admin/docs → 문서 메인 페이지
 * </ul>
 *
 * <p><strong>접근 경로:</strong>
 *
 * <ul>
 *   <li>{@code /api/admin/docs} - API 문서 메인 페이지
 *   <li>{@code /api/admin/docs/index.html} - API 문서 메인 페이지 (직접 접근)
 * </ul>
 *
 * <p><strong>문서 위치:</strong>
 *
 * <ul>
 *   <li>소스: {@code src/docs/asciidoc/}
 *   <li>빌드 결과: {@code build/docs/asciidoc/}
 *   <li>배포 위치: {@code static/docs/} (bootJar 내)
 * </ul>
 *
 * <p><strong>빌드 방법:</strong>
 *
 * <pre>{@code
 * ./gradlew :bootstrap:bootstrap-admin-api:asciidoctor
 * }</pre>
 *
 * @author Development Team
 * @since 1.0.0
 * @see <a href="https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/">Spring
 *     REST Docs</a>
 */
@Controller
public class ApiDocsController {

    private static final String DOCS_PATH = "/api/admin/docs";

    /**
     * API 문서 메인 페이지로 리다이렉트
     *
     * <p>{@code /api/admin/docs} 접근 시 {@code /api/admin/docs/index.html}로 리다이렉트합니다.
     *
     * @return 리다이렉트 경로
     */
    @GetMapping(DOCS_PATH)
    public String redirectToApiDocs() {
        return "redirect:" + DOCS_PATH + "/index.html";
    }
}
