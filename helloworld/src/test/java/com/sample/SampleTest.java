package com.sample;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.evaluation.FactCheckingEvaluator;
import org.springframework.ai.evaluation.RelevancyEvaluator;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class SampleTest {

    @Autowired
    ChatClient.Builder builder;


    @Test
    void evaluateRelevancy() {
        String question = "Where does the adventure of Anacletus and Birba take place?";

        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
//                        .vectorStore(pgVectorStore) TODO
                        .build())
                .build();

        ChatResponse chatResponse = builder.build()
                .prompt(question)
                .advisors(ragAdvisor)
                .call()
                .chatResponse();

        EvaluationRequest evaluationRequest = new EvaluationRequest(
                // The original user question
                question,
                // The retrieved context from the RAG flow
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                // The AI model's response
                chatResponse.getResult().getOutput().getText()
        );

        RelevancyEvaluator evaluator = new RelevancyEvaluator(builder);

        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        assertThat(evaluationResponse.isPass()).isTrue();
    }


    @Test
    void testFactChecking() {
        // Set up the Ollama API
//        OllamaApi ollamaApi = new OllamaApi("http://localhost:11434");
//
//        ChatModel chatModel = new OllamaChatModel(ollamaApi,
//                OllamaOptions.builder().model(BESPOKE_MINICHECK).numPredict(2).temperature(0.0d).build())


        // Create the FactCheckingEvaluator
        //var factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));
        var factCheckingEvaluator = new FactCheckingEvaluator(builder);

        // Example context and claim
        String context = "The Earth is the third planet from the Sun and the only astronomical object known to harbor life.";
        String claim = "The Earth is the fourth planet from the Sun.";

        // Create an EvaluationRequest
        EvaluationRequest evaluationRequest = new EvaluationRequest(context, Collections.emptyList(), claim);

        // Perform the evaluation
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        assertFalse(evaluationResponse.isPass(), "The claim should not be supported by the context");

    }
}
