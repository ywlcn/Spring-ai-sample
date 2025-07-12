package com.sample.runner;

import com.sample.tools.DateTimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.DefaultChatOptionsBuilder;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

import java.util.Arrays;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;


/// https://www.youtube.com/watch?v=5zhNfPH-jps
///
///
///
@Configuration
public class ChatWithPicture {

    public static String CONVERSATION_ID_TOOL = "CONVERSATION_ID_TOOL";
    public static String CONVERSATION_ID_NO_TOOL = "CONVERSATION_ID_NO_TOOL";

    Logger logger = LoggerFactory.getLogger(ChatWithPicture.class);
//    @Autowired
//    private List<McpAsyncClient> mcpAsyncClients;  // For async client

    @Autowired
    private SyncMcpToolCallbackProvider toolCallbackProvider;

//    @Bean
    CommandLineRunner cli(ChatClient chatClient) {
        return args -> {
            // llava:latest
//            DefaultChatOptionsBuilder builder = new DefaultChatOptionsBuilder();
//            ChatOptions options = builder.model("llava:latest").build();　　options(options)
            String response = chatClient.prompt().user(
                            u -> u.text("Explain what do you see on this picture?")
                                    .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/images/multimodal.test.png")))
                    .call()
                    .content();
            logger.warn("-----------------------画像からText-----------------------");
            logger.warn(response);
        };
    }

    //@Bean
    CommandLineRunner mcpClient(ChatClient chatClient) {
        return args -> {
            System.out.println("-----------------------mcpClient-----------------------");
            System.out.println(Arrays.toString(toolCallbackProvider.getToolCallbacks()));
            String response = chatClient
                    .prompt("What's the weather of NW in USA")
                    .tools(toolCallbackProvider.getToolCallbacks())
                    .call()
                    .content();

            logger.warn("-----------------------MCPサーバーを利用する場合-----------------------");
            logger.warn(response);
            logger.warn("");
        };
    }


    @Bean
    CommandLineRunner withNoTools(ChatClient chatClient) {
        return args -> {
            String response = chatClient
                    .prompt("What day is tomorrow?")
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, CONVERSATION_ID_NO_TOOL))
                    .call()
                    .content();
            logger.warn("-----------------------ツールを利用しない場合-----------------------");
            logger.warn(response);
            logger.warn("");
        };
    }

    @Bean
    CommandLineRunner withTools(ChatClient chatClient) {
        return args -> {
            String response = chatClient
                    .prompt("What day is tomorrow?")
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, CONVERSATION_ID_TOOL))
                    .tools(new DateTimeTools())
                    .call()
                    .content();
            logger.warn("-----------------------ツールを利用する場合。-----------------------");
            logger.warn(response);
            logger.warn("");

        };
    }


}
