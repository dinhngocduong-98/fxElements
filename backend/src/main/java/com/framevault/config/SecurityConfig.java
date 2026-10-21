package com.framevault.config;

import com.framevault.usecase.AccountSecurityUseCase;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.*;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  AuthenticationManager manager(UserDetailsService details, PasswordEncoder encoder) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(details);
    p.setPasswordEncoder(encoder);
    return new ProviderManager(p);
  }

  @Bean
  SecurityFilterChain chain(HttpSecurity http, AccountSecurityUseCase accounts) throws Exception {
    http.csrf(c -> c.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
        .authorizeRequests(
            a ->
                a.antMatchers("/api/v1/admin/**")
                    .hasRole("ADMIN")
                    .antMatchers(
                        "/api/v1/auth/**",
                        "/api/v1/products/**",
                        "/api/v1/categories",
                        "/api/v1/tags",
                        "/api/v1/collections",
                        "/api/v1/media/public/**")
                    .permitAll()
                    .antMatchers("/api/**")
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (q, r, x) -> {
                          r.setStatus(401);
                          r.setContentType("application/json");
                          r.setCharacterEncoding("UTF-8");
                          r.getWriter().write("{\"message\":\"Vui lòng đăng nhập\"}");
                        })
                    .accessDeniedHandler(
                        (q, r, x) -> {
                          r.setStatus(403);
                          r.setContentType("application/json");
                          r.setCharacterEncoding("UTF-8");
                          r.getWriter()
                              .write("{\"message\":\"Không có quyền hoặc CSRF đã hết hạn\"}");
                        }))
        .requestCache(c -> c.disable())
        .formLogin(c -> c.disable())
        .httpBasic(c -> c.disable())
        .logout(c -> c.disable())
        .headers(
            h ->
                h.contentSecurityPolicy(
                    "default-src 'self'; img-src 'self' data:; media-src 'self'; style-src 'self'"
                        + " 'unsafe-inline'; script-src 'self'; frame-ancestors 'none'"));
    http.addFilterBefore(
        new OncePerRequestFilter() {

          protected void doFilterInternal(
              HttpServletRequest q, HttpServletResponse r, FilterChain chain)
              throws ServletException, IOException {
            HttpSession s = q.getSession(false);
            Object authenticatedAt = s == null ? null : s.getAttribute("AUTHENTICATED_AT");
            if (s != null
                && authenticatedAt instanceof Long
                && System.currentTimeMillis() - (Long) authenticatedAt > 12 * 3600000L) {
              s.invalidate();
              org.springframework.security.core.context.SecurityContextHolder.clearContext();
            }
            org.springframework.security.core.Authentication a =
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication();
            if (a != null
                && !(a
                    instanceof
                    org.springframework.security.authentication.AnonymousAuthenticationToken)) {
              boolean active = accounts.isActive(a.getName());
              if (!active) {
                if (q.getSession(false) != null) q.getSession(false).invalidate();
                org.springframework.security.core.context.SecurityContextHolder.clearContext();
              }
            }
            chain.doFilter(q, r);
          }
        },
        AnonymousAuthenticationFilter.class);
    return http.build();
  }
}
