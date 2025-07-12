package com.sample.controller.chat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatWebController {

    @GetMapping("/")
    public String chat() {
        return "normal-chat";
    }

    @GetMapping("/tool")
    public String toolChat() {
        return "tools-chat";
    }


}
