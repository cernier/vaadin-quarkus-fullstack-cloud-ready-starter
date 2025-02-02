package com.example.starter.base.views;

import com.example.starter.base.services.Services;
import com.example.starter.base.views.about.AboutView;
import com.example.starter.base.views.helloworld.HelloWorldView;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

public class MainLayout extends AppLayout implements RouterLayout {

  /**
   * A simple navigation item component, based on ListItem element.
   */
  public static class MenuItemInfo extends ListItem {

    private final Class<? extends Component> view;

    public MenuItemInfo(String menuTitle, String iconClass, Class<? extends Component> view) {
      this.view = view;
      RouterLink link = new RouterLink();
      link.addClassNames("menu-item-link");
      link.setRoute(view);

      Span text = new Span(menuTitle);
      text.addClassNames("menu-item-text");

      link.add(new LineAwesomeIcon(iconClass), text);
      add(link);
    }

    public Class<?> getView() {
      return view;
    }

    /**
     * Simple wrapper to create icons using LineAwesome iconset. See
     * https://icons8.com/line-awesome
     */
    @NpmPackage(value = "line-awesome", version = "1.3.0")
    public static class LineAwesomeIcon extends Span {
      public LineAwesomeIcon(String lineawesomeClassnames) {
        addClassNames("menu-item-icon");
        if (!lineawesomeClassnames.isEmpty()) {
          addClassNames(lineawesomeClassnames);
        }
      }
    }

  }

  private H1 viewTitle = new H1();

  private final AccessAnnotationChecker accessChecker;

  public MainLayout() {
    accessChecker = new AccessAnnotationChecker();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);

    initLayoutContent();
  }

  private void initLayoutContent() {
    setPrimarySection(Section.DRAWER);
    addToNavbar(false, createHeaderContent());
    addToDrawer(createDrawerContent());
  }

  private Component createHeaderContent() {
    DrawerToggle toggle = new DrawerToggle();
    toggle.addClassNames("view-toggle");
    toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    toggle.getElement().setAttribute("aria-label", "Menu toggle");

    viewTitle.addClassNames("view-title");

    Header header = new Header(toggle, viewTitle);
    header.addClassNames("view-header");
    return header;
  }

  private Component createDrawerContent() {
    H2 appName = new H2("Vaadin-Quarkus app");
    appName.addClassNames("app-name");
    com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName, createNavigation(), createFooter());
    section.addClassNames("drawer-section");
    return section;
  }

  private Nav createNavigation() {
    Nav nav = new Nav();
    nav.addClassNames("menu-item-container");
    nav.getElement().setAttribute("aria-labelledby", "views");

    // Wrap the links in a list; improves accessibility
    UnorderedList list = new UnorderedList();
    list.addClassNames("navigation-list");
    nav.add(list);

    for (MenuItemInfo menuItem : createMenuItems()) {
      if (accessChecker.hasAccess(menuItem.getView())) {
        list.add(menuItem);
      }
    }
    return nav;
  }

  private MenuItemInfo[] createMenuItems() {
    return new MenuItemInfo[]{
        new MenuItemInfo("Hello World", "la la-globe", HelloWorldView.class),
        new MenuItemInfo("About", "la la-file", AboutView.class)
    };
  }

  private Footer createFooter() {
    Footer layout = new Footer();
    layout.addClassNames("footer");

    ContextMenu userMenu = new ContextMenu(layout);
    userMenu.setOpenOnClick(true);

    Services.get().getSecurityService().getLoggedUser().ifPresentOrElse(user -> {
          String userDisplayName = String.format("%s %s", user.getFirstName(), user.getLastName());
          Avatar avatar = new Avatar(userDisplayName, user.getPictureURL());
          avatar.addClassNames("me-xs");

          userMenu.addItem("Profile");
          userMenu.addItem("Logout", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));

          Span name = new Span(userDisplayName);
          name.addClassNames("font-medium", "text-s", "text-secondary");

          layout.add(avatar, name);
        },
        () -> {
          Avatar avatar = new Avatar();
          avatar.addClassNames("me-xs");

          userMenu.addItem("Sign in", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/login")));

          Span signIn = new Span("Not signed");
          signIn.addClassNames("font-medium", "text-s", "text-secondary");

          layout.add(avatar, signIn);
        });

    return layout;
  }

  @Override
  protected void afterNavigation() {
    super.afterNavigation();
    viewTitle.setText(getCurrentPageTitle());
  }

  private String getCurrentPageTitle() {
    PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
    return title == null ? "" : title.value();
  }

}
