package org.ymmy.todo_chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "org.ymmy.todo_chat.db")
public class TodoChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoChatApplication.class, args);
	}

}
