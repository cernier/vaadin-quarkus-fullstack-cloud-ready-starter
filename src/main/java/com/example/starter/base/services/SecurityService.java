package com.example.starter.base.services;

import com.example.starter.base.model.User;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.BooleanUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class SecurityService {

  private static final String AUTHENTICATION_SESSION_KEY = SecurityService.class.getName();

  @Inject
  UserService userService;

  @Transactional
  public Optional<User> getLoggedUser() {
    if (noUserIsLogged()) {
      return Optional.empty();
    }

    String username = "default";
    User result = userService.findByUsernameOptional(username)
        .orElseGet(() -> userService.saveOrUpdateWithUniqueUsername(
            User.builder()
                .username(username)
                .build())
        );

    return Optional.of(result);
  }

  public void postLogin() {
    VaadinSession.getCurrent().setAttribute(AUTHENTICATION_SESSION_KEY, Boolean.TRUE);
  }

  public void logout() {
    VaadinSession.getCurrent().setAttribute(AUTHENTICATION_SESSION_KEY, Boolean.FALSE);
  }

  private boolean noUserIsLogged() {
    return BooleanUtils.isNotTrue((Boolean) VaadinSession.getCurrent().getAttribute(AUTHENTICATION_SESSION_KEY));
  }

}
