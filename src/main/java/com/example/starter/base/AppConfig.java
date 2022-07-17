package com.example.starter.base;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Theme("starter-theme")
@PWA(name = "Vaadin-Quarkus fullstack cloud-ready starter", shortName = "vaadin-quarkus-fullstack-cloud-ready-starter")
@Push
@PreserveOnRefresh
public class AppConfig implements AppShellConfigurator {
}
