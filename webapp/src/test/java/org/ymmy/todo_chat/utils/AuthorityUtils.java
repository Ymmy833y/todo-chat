package org.ymmy.todo_chat.utils;

import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.ymmy.todo_chat.config.CustomUserDetails;
import org.ymmy.todo_chat.util.AuthorityEnum;

public class AuthorityUtils {

  private final static String USER_NAME = "USER_NAME";
  private final static String PASSWORD = "PASSWORD";

  public static Authentication getDefaultAuth(final Long userId) {
    return getAuthWithRole(userId, List.of(AuthorityEnum.GENERAL));
  }

  public static Authentication getAuthWithRole(final Long userId,
      final List<AuthorityEnum> authorityEnumList) {
    final var userDetails = new CustomUserDetails(userId, USER_NAME, PASSWORD,
        authorityEnumList.stream().map(e -> new SimpleGrantedAuthority(e.getCode())).toList());
    return new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
  }
}
