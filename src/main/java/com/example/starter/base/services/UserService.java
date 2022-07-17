package com.example.starter.base.services;

import com.example.starter.base.model.User;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserService {

  private static final String CACHE_NAME = "user";

  @Inject
  @CacheName(CACHE_NAME)
  Cache cache;

  @CacheResult(cacheName = UserService.CACHE_NAME)
  public Optional<User> findByUsernameOptional(@CacheKey String username) {
    return User.findByUsernameOptional(username);
  }

  @Transactional
  public User saveOrUpdateWithUniqueUsername(User user) {
    return cache.invalidate(user.getUsername())
        .chain(() -> Uni.createFrom().item(User.saveOrUpdateWithUniqueUsername(user)))
        .await().indefinitely();
  }
}