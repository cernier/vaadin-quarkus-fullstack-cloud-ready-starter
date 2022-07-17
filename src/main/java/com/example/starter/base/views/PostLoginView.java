package com.example.starter.base.views;

import com.example.starter.base.services.Services;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.quarkus.annotation.UIScoped;

@UIScoped
@Route(value = "post-login", absolute = true)
public class PostLoginView extends Div implements BeforeEnterObserver {

  @Override
  public void beforeEnter(BeforeEnterEvent event) {
    Services.get().getSecurityService().getLoggedUser(); // force logged user full fetch from DB before going home so that authorization pre-check will be fine
    event.getUI().getPage().setLocation("/");
  }

}