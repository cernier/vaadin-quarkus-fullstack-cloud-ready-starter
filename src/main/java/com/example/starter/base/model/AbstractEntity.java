package com.example.starter.base.model;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import java.util.Optional;

public abstract class AbstractEntity extends PanacheEntity {

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public static Optional<Boolean> preEquals(Object thiz, Object other) {
    if (other == null) {
      return Optional.of(Boolean.FALSE);
    }
    if (other == thiz) {
      return Optional.of(Boolean.TRUE);
    }
    if (other.getClass() != thiz.getClass()) {
      return Optional.of(Boolean.FALSE);
    }
    return Optional.empty();
  }

  public static <E extends AbstractEntity> E saveOrUpdate(E entity) {
    return saveOrUpdate(entity, false);
  }

  public static <E extends AbstractEntity> E saveOrUpdateWithIdOfExisting(E entity, Optional<Long> idOfExisting) {
    idOfExisting.ifPresent(entity::setId);
    return saveOrUpdate(entity, idOfExisting.isPresent());
  }

  public static <E extends AbstractEntity> E saveOrUpdate(E entity, boolean forceUpdate) {
    if (entity.isPersistent() || entity.getId() != null || forceUpdate) {
      entity = Panache.getEntityManager().merge(entity);
    } else {
      entity.persist();
    }
    return entity;
  }

}
