package com.example.starter.base.views;

import com.example.starter.base.services.security.SecurityService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;

import javax.inject.Inject;

@UIScoped
@Route(value = "logout", absolute = true)
public class LogoutView extends Div implements BeforeEnterObserver {

  @Inject
  SecurityService securityService;

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    securityService.logout();
    event.getUI().getPage().setLocation("/");
  }

}