package com.example.starter.base.services.security;

import com.example.starter.base.model.Role;
import com.example.starter.base.model.User;
import com.example.starter.base.services.UserService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import lombok.Setter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApplicationScoped
public class RolesAugmentorFromUserInPersistence implements SecurityIdentityAugmentor {

  @Inject
  Instance<SecurityIdentityEnhancerWithUserRoles> enhancerWithUserRolesProvider;

  @Override
  public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
    SecurityIdentityEnhancerWithUserRoles enhancerWithUserRoles = enhancerWithUserRolesProvider.get();
    enhancerWithUserRoles.setSecurityIdentity(identity);
    return context.runBlocking(enhancerWithUserRoles);
  }

  @Dependent
  private static class SecurityIdentityEnhancerWithUserRoles implements Supplier<SecurityIdentity> {

    @Inject
    UserService userService;

    @Setter
    private SecurityIdentity securityIdentity;

    @ActivateRequestContext
    @Override
    public SecurityIdentity get() {
      if (securityIdentity.isAnonymous()) {
        return securityIdentity;
      } else {
        // create a new builder and copy principal, attributes, credentials and roles from the original identity
        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(securityIdentity);

        // add custom role source here
        builder.addRoles(userService.findByUsernameOptional(securityIdentity.getPrincipal().getName())
            .map(User::getRoles).stream().flatMap(Collection::stream)
            .map(Role::name).collect(Collectors.toSet()));

        return builder.build();
      }
    }
  }
}
