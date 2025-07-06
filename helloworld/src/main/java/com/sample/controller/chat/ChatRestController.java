package com.sample.controller.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST 控制器，用于处理聊天请求。
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // 允许所有来源的跨域请求，在生产环境中应限制为特定的前端域名
public class ChatRestController {

//    private final OllamaChatModel chatModel;
    private final ChatModel chatModel;

    /**
     * 构造函数注入 OllamaChatModel。
     * Spring Boot 会自动配置 OllamaChatModel，前提是 Ollama 服务器正在运行。
     *
     * @param chatModel Ollama 聊天模型实例
     */
//    public ChatRestController(OllamaChatModel chatModel) {
//        this.chatModel = chatModel;
//    }

    public ChatRestController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }


    /**
     * 处理 GET 请求，用于简单的问答。
     * 示例用法: GET http://localhost:8080/api/chat/generate?message=Hello
     *
     * @param message 用户的输入消息
     * @return AI 模型生成的回应文本
     */
    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        // 创建一个包含用户消息的 Prompt
        Prompt prompt = new Prompt(message);
        // 调用 Ollama 模型生成响应
        ChatResponse chatResponse = chatModel.call(prompt);
        // 返回 AI 生成的内容
        return Map.of("response", chatResponse.getResult().getOutput().getText());
    }

    /**
     * 处理 POST 请求，用于更复杂的聊天交互（例如，如果未来需要支持聊天历史）。
     * 示例用法: POST http://localhost:8080/api/chat/send
     * 请求体: {"message": "What is the capital of France?"}
     *
     * @param request 包含用户消息的 Map
     * @return AI 模型生成的回应文本
     */
    @PostMapping("/send1")
    public Map<String, String> sendMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Map.of("error", "Message cannot be empty");
        }
        Prompt prompt = new Prompt(message);
        ChatResponse chatResponse = chatModel.call(prompt);
        return Map.of("response", chatResponse.getResult().getOutput().getText());
    }

    @Autowired
    ChatClient chatClient;

    @PostMapping("/send")
    public Map<String, String> sendMessage2(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return Map.of("error", "Message cannot be empty");
        }
        return Map.of("response", chatClient.prompt(message).call().content());
    }


}