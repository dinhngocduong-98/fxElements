package com.framevault.security;

import javax.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.*;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

  private final AuthenticationManager manager;

  private final FindByIndexNameSessionRepository<? extends Session> sessions;

  public SessionManager(
      AuthenticationManager manager, FindByIndexNameSessionRepository<? extends Session> sessions) {
    this.manager = manager;
    this.sessions = sessions;
  }

  public String login(String email, String password, HttpServletRequest q, HttpServletResponse r) {
    Authentication a =
        manager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    q.getSession();
    q.changeSessionId();
    q.getSession().setAttribute("AUTHENTICATED_AT", System.currentTimeMillis());
    SecurityContext c = SecurityContextHolder.createEmptyContext();
    c.setAuthentication(a);
    SecurityContextHolder.setContext(c);
    new HttpSessionSecurityContextRepository().saveContext(c, q, r);
    new HttpSessionCsrfTokenRepository().saveToken(null, q, r);
    return a.getName();
  }

  public void logout(HttpServletRequest q) {
    if (q.getSession(false) != null) q.getSession(false).invalidate();
    SecurityContextHolder.clearContext();
  }

  public void revoke(String email) {
    sessions.findByPrincipalName(email).keySet().forEach(sessions::deleteById);
  }
}
