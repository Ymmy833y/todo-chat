package org.ymmy.todo_chat.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ymmy.todo_chat.model.dto.LoginDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

  @NotBlank(message = "入力必須の項目です。")
  @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "使用できる文字はa~z、A~Z、0~9のみです。")
  private String userName;

  public LoginDto convertToDto() {
    return LoginDto.builder() //
        .ussrName(this.userName) //
        .build();
  }

}
