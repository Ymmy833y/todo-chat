package org.ymmy.todo_chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebSocketController {

  @GetMapping("/websocket")
  public String websocket(final Model model, @RequestParam(required = false) String message) {
    model.addAttribute("message", message);
    return "websocket";
  }
}