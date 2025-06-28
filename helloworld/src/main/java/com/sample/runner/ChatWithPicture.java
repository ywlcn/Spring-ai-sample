package com.sample.runner;

import com.sample.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.MimeTypeUtils;

import java.nio.file.Paths;

@Configuration
public class ChatWithPicture {

    //@Bean
    CommandLineRunner cli(ChatClient chatClient) {
        return args -> {
            // llava:latest
            DefaultChatOptionsBuilder builder = new DefaultChatOptionsBuilder();
            ChatOptions options = builder.model("llava:latest").build();
            String response = chatClient.prompt().options(options).user(
                            u -> u.text("Explain what do you see on this picture?")
                                    .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/images/multimodal.test.png")))
                    .call()
                    .content();
            System.out.println("-----------------------image + text-----------------------");
            System.out.println(response);
        };
    }

    @Bean
    CommandLineRunner withNoTools(ChatClient chatClient) {
        return args -> {
            String response = chatClient
                    .prompt("What day is tomorrow?")
                    .call()
                    .content();
            System.out.println("-----------------------withNoTools-----------------------");
            System.out.println(response);


        };
    }

    @Bean
    CommandLineRunner withTools(ChatClient chatClient) {
        return args -> {
            String response = chatClient
                    .prompt("What day is tomorrow?")
                    .tools(new DateTimeTools())
                    .call()
                    .content();
            System.out.println("-----------------------withTools-----------------------");
            System.out.println(response);

//            response = chatClient
//                    .prompt("Can you set an alarm 10 minutes from now?")
//                    .tools(new DateTimeTools())
//                    .call()
//                    .content();
//            System.out.println(response);
        };
    }


}
