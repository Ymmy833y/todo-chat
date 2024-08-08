package org.ymmy.todo_chat.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ymmy.todo_chat.config.CustomUserDetails;
import org.ymmy.todo_chat.db.entity.Authority;
import org.ymmy.todo_chat.repository.AuthorityRepository;
import org.ymmy.todo_chat.repository.UserRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
    final var user = userRepository.selectByUserName(username).orElseThrow(() ->
        new UsernameNotFoundException(ErrorMessageEnum.NOT_FOUND_USER.getMessage()));
    final var authorities = authorityRepository.selectByUserId(user.getId())
        .stream().map(this::convertToSimpleGrantedAuthority).toList();

    return new CustomUserDetails(user.getId(), user.getName(), user.getPassword(), authorities);
  }

  private SimpleGrantedAuthority convertToSimpleGrantedAuthority(final Authority authority) {
    return new SimpleGrantedAuthority(authority.getRole());
  }
}
