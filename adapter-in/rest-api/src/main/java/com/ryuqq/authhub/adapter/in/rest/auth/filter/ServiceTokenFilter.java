package com.ryuqq.authhub.adapter.in.rest.auth.filter;

import com.ryuqq.auth.common.constant.Roles;
import com.ryuqq.auth.common.header.SecurityHeaders;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContext;
import com.ryuqq.authhub.adapter.in.rest.auth.component.SecurityContextHolder;
import com.ryuqq.authhub.adapter.in.rest.auth.config.ServiceTokenProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 서비스 토큰 인증 필터 (서버간 통신)
 *
 * <p>X-Service-Token 헤더를 검증하여 서비스 계정 인증을 수행합니다.
 *
 * <p><strong>처리 순서:</strong>
 *
 * <ol>
 *   <li>X-Service-Token 헤더 존재 확인
 *   <li>설정된 secret과 비교하여 검증
 *   <li>유효하면 ServiceContext 생성 (serviceAccount=true)
 *   <li>원본 사용자 정보가 있으면 함께 설정
 * </ol>
 *
 * <p><strong>확장 헤더:</strong>
 *
 * <ul>
 *   <li>X-Service-Token: 서비스 인증 토큰
 *   <li>X-Service-Name: 호출 서비스명
 *   <li>X-Original-User-Id: 원본 사용자 ID
 *   <li>X-Original-Tenant-Id: 원본 테넌트 ID
 *   <li>X-Original-Organization-Id: 원본 조직 ID
 *   <li>X-Correlation-Id: 분산 추적 ID
 *   <li>X-Request-Source: 요청 출처 서비스
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class ServiceTokenFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ServiceTokenFilter.class);

    private final ServiceTokenProperties serviceTokenProperties;

    public ServiceTokenFilter(ServiceTokenProperties serviceTokenProperties) {
        this.serviceTokenProperties = serviceTokenProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String serviceToken = request.getHeader(SecurityHeaders.SERVICE_TOKEN);

        if (!StringUtils.hasText(serviceToken) || !serviceTokenProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!isValidServiceToken(serviceToken)) {
            log.warn(
                    "[SERVICE-AUTH] Invalid service token from: {}",
                    request.getHeader(SecurityHeaders.SERVICE_NAME));
            filterChain.doFilter(request, response);
            return;
        }

        try {
            SecurityContext context = buildServiceSecurityContext(request);
            SecurityContextHolder.setContext(context);
            synchronizeWithSpringSecurityContext(context);

            log.info(
                    "[SERVICE-AUTH] Service authenticated: serviceName={}, originalUserId={},"
                            + " correlationId={}",
                    context.getRequestSource(),
                    context.getUserId(),
                    context.getCorrelationId());

            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    private boolean isValidServiceToken(String token) {
        String configuredSecret = serviceTokenProperties.getSecret();
        if (!StringUtils.hasText(configuredSecret) || !StringUtils.hasText(token)) {
            return false;
        }
        // 타이밍 공격 방지를 위한 상수 시간 비교
        return MessageDigest.isEqual(
                configuredSecret.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8));
    }

    private SecurityContext buildServiceSecurityContext(HttpServletRequest request) {
        String serviceName = parseHeader(request, SecurityHeaders.SERVICE_NAME);
        String originalUserId = parseHeader(request, SecurityHeaders.ORIGINAL_USER_ID);
        String originalTenantId = parseHeader(request, SecurityHeaders.ORIGINAL_TENANT_ID);
        String originalOrgId = parseHeader(request, SecurityHeaders.ORIGINAL_ORGANIZATION_ID);
        String correlationId = parseHeader(request, SecurityHeaders.CORRELATION_ID);
        String requestSource = parseHeader(request, SecurityHeaders.REQUEST_SOURCE);

        if (!StringUtils.hasText(requestSource)) {
            requestSource = serviceName;
        }

        return SecurityContext.builder()
                .userId(originalUserId)
                .tenantId(originalTenantId)
                .organizationId(originalOrgId)
                .roles(Set.of(Roles.SERVICE))
                .permissions(Set.of())
                .serviceAccount(true)
                .requestSource(requestSource)
                .correlationId(correlationId)
                .build();
    }

    private String parseHeader(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return StringUtils.hasText(value) ? value : null;
    }

    private void synchronizeWithSpringSecurityContext(SecurityContext context) {
        Set<SimpleGrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority(Roles.SERVICE));

        String principal =
                context.getRequestSource() != null ? context.getRequestSource() : "service";

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        // Law of Demeter 준수: getter 체이닝 대신 로컬 변수 사용
        org.springframework.security.core.context.SecurityContext securityContext =
                org.springframework.security.core.context.SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String serviceToken = request.getHeader(SecurityHeaders.SERVICE_TOKEN);
        return !StringUtils.hasText(serviceToken);
    }
}
