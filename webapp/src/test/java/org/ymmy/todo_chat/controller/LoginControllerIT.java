package org.ymmy.todo_chat.controller;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.generator.ValueGenerators;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ymmy.todo_chat.model.form.LoginForm;
import org.ymmy.todo_chat.repository.UserRepository;
import org.ymmy.todo_chat.util.ErrorMessageEnum;
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
    final var userOperation = Operations.insertInto("user")
        .withGeneratedValue("id", ValueGenerators.sequence().startingAt(1))
        .withGeneratedValue("name",
            ValueGenerators.stringSequence("USER_").startingAt(1)) //
        .withGeneratedValue("display_name",
            ValueGenerators.stringSequence("DISPLAY_NAME_").startingAt(1)) //
        .row() //
        .end() //
        .withDefaultValue("created_by", 1L) //
        .build();

    DatabaseUtils.executeSqlFile(dataSource, "src/test/resources/db/deleteAll.sql");
    final var dbSetup = new DbSetup(new DataSourceDestination(dataSource),
        sequenceOf(userOperation));
    dbSetup.launch();
  }

  @Nested
  class Login {

    @Test
    void ログインしていない場合ログイン画面を表示できる() throws Exception {
      mockMvc.perform(get("/login"))
          .andExpect(status().isOk())
          .andExpect(view().name("login"))
          .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    void セッションに保持しているuserIdが不正の場合ログイン画面を表示できる() throws Exception {
      mockMvc.perform(get("/login").sessionAttr("userId", 999L))
          .andExpect(status().isOk())
          .andExpect(view().name("login"))
          .andExpect(model().attributeExists("loginForm"));
    }

    @Test
    void ログインしている場合ホーム画面にリダイレクトされる() throws Exception {
      mockMvc.perform(get("/login").sessionAttr("userId", 1L))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/home"));
    }

    @Test
    void ログイン情報が正しい場合ホーム画面にリダイレクトされる() throws Exception {
      final var loginForm = new LoginForm("USER_1");
      mockMvc.perform(post("/login")
              .flashAttr("loginForm", loginForm))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/home"));
    }

    @Test
    void ログイン情報が正しくない場合ログイン画面にリダイレクトされる() throws Exception {
      final var loginForm = new LoginForm("USER_99");
      mockMvc.perform(post("/login")
              .flashAttr("loginForm", loginForm))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"))
          .andExpect(flash().attributeExists("exception"))
          .andExpect(flash().attribute("exception", ErrorMessageEnum.NOT_FOUND_USER.getMessage()));
    }
  }

  @Nested
  class Logout {

    @Test
    void ログアウトする場合セッションが削除される() throws Exception {
      final var session = new MockHttpSession();
      session.setAttribute("userId", 1L);

      mockMvc.perform(post("/logout").session(session))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/login"));

      // セッションが無効化されたことを確認
      assertThat(session.isInvalid()).isTrue();
    }
  }

}
