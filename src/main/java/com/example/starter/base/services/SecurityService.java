package com.example.starter.base.services;

import com.example.starter.base.model.User;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.OidcSession;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ApplicationScoped
public class SecurityService {

  @Inject
  @IdToken
  JsonWebToken idToken;

  @Inject
  OidcSession oidcSession;

  @Inject
  UserService userService;

  @Transactional
  public Optional<User> getLoggedUser() {
    if (noUserIsLogged()) {
      return Optional.empty();
    }
    String username = idToken.getSubject();
    if (StringUtils.isEmpty(username)) {
      return Optional.empty();
    }
    User result = userService.findByUsernameOptional(username)
        .orElseGet(() -> User.builder()
            .username(username)
            .build());
    LocalDateTime issuedTokenDateTime = LocalDateTime.ofEpochSecond(idToken.getIssuedAtTime(), 0, ZoneOffset.UTC);
    if (result.getLastUpdatedAt() != null && !result.getLastUpdatedAt().isBefore(issuedTokenDateTime)) {
      return Optional.of(result);
    }
    result.setLastUpdatedAt(issuedTokenDateTime);
    result.setFirstName(idToken.getClaim("given_name"));
    result.setLastName(idToken.getClaim("family_name"));
    result.setEmail(idToken.getClaim("email"));
    result.setPictureURL(idToken.getClaim("picture"));

    return Optional.of(userService.saveOrUpdateWithUniqueUsername(result));
  }

  public void logout() {
    oidcSession.logout().await().indefinitely();
  }

  private boolean noUserIsLogged() {
    return oidcSession.getIdToken() == null;
  }

}
