package com.sample.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatWebController {

    @GetMapping("/")
    public String welcome() {
        return "normal-chat";
    }

}
