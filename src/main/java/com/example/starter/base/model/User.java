package com.example.starter.base.model;

import com.google.common.base.Objects;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User extends AbstractEntity {

  @NotEmpty
  @Column(unique = true)
  private String username;

  private String firstName;

  private String lastName;

  private String email;

  private String pictureURL;

  private LocalDateTime lastUpdatedAt;

  public static Optional<User> findByUsernameOptional(String username) {
    return find("username", username).singleResultOptional();
  }

  public static User saveOrUpdateWithUniqueUsername(User user) {
    return saveOrUpdateWithIdOfExisting(user,
        findByUsernameOptional(user.username).map(User::getId));
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : Objects.hashCode(username);
  }

  @Override
  public boolean equals(Object other) {
    return preEquals(this, other).orElseGet(() ->
        id != null ? id.equals(((User) other).id) :
            Objects.equal(this.username, ((User) other).username)
    );
  }

}
