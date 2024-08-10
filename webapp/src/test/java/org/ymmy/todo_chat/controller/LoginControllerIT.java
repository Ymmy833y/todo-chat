package org.ymmy.todo_chat.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ymmy.todo_chat.repository.UserRepository;
import org.ymmy.todo_chat.utils.AuthorityUtils;
import org.ymmy.todo_chat.utils.DatabaseUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginControllerIT {

  @Autowired
  DataSource dataSource;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() throws Exception {
    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
  }

  @Nested
  class Login {

    @Test
    void ログインしていない場合ログイン画面が表示される() throws Exception {
      mockMvc.perform(get("/login"))
          .andExpect(status().isOk())
          .andExpect(view().name("/login"));
    }

    @Test
    void ログインしている場合ホーム画面にリダイレクトされる() throws Exception {
      final var userId = 1L;
      final var auth = AuthorityUtils.getDefaultAuth(userId);
      mockMvc.perform(get("/login")
              .with(authentication(auth))
              .with(csrf()))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/home"));
    }
  }
}
