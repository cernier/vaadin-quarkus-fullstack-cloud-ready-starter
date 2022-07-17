package com.example.starter.base.views.helloworld;

import com.example.starter.base.model.User;
import com.example.starter.base.services.SecurityService;
import com.example.starter.base.services.Services;
import com.example.starter.base.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.quarkus.annotation.UIScoped;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@UIScoped
@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class HelloWorldView extends HorizontalLayout {

  private TextField name;
  private Button sayHello;

  @Inject
  Services services;

  @PostConstruct
  public void init() {
    services.registerIn(VaadinSession.getCurrent());
    initializeViewContent();
  }

  public HelloWorldView() {
  }

  private void initializeViewContent() {
    Optional<User> loggedUser = Optional.ofNullable(services.getSecurityService())
        .flatMap(SecurityService::getLoggedUser);
    name = new TextField("Your name");
    sayHello = new Button("Say hello");
    sayHello.addClickListener(e -> {
      Notification.show("Hello " + name.getValue() +
          loggedUser.map(user -> " (logged user: " + user.getFirstName() + " " + user.getLastName() + ")")
              .orElse(" (no logged user)"));
    });

    setMargin(true);
    setVerticalComponentAlignment(Alignment.END, name, sayHello);

    add(name, sayHello);
  }

}
