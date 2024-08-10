package org.ymmy.todo_chat.config;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Long userId;
  private final String username;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;

}
