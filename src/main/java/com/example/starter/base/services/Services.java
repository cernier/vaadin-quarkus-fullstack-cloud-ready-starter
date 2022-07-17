package com.example.starter.base.services;

import com.example.starter.base.services.security.SecurityService;
import com.vaadin.flow.server.VaadinSession;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Getter
@ApplicationScoped
public class Services {

  @Inject
  UserService userService;

  @Inject
  SecurityService securityService;

  public static Services get() {
    return VaadinSession.getCurrent().getAttribute(Services.class);
  }

  public void registerIn(VaadinSession vaadinSession) {
    vaadinSession.setAttribute(Services.class, this);
  }
}
