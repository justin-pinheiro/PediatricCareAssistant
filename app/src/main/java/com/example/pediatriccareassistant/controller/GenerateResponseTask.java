package com.example.pediatriccareassistant.controller;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.RequestType;
import com.example.pediatriccareassistant.model.callback.ChatbotCallback;

import java.util.List;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * AsyncTask for generating responses based on user messages.
 * The responses are derived from various models depending on the type of request.
 */
public class GenerateResponseTask extends AsyncTask<String, Void, String>
{
    private static final String AI_CHATBOT_BASIC_INSTRUCTIONS = "You are a helpful medical robot assistant created to assist parents managing their children's health. ";
    private static final String AI_CHATBOT_OTHER_INSTRUCTIONS = "Answer shortly. Only answer health-related queries.";
    private static final String AI_CHATBOT_ARTICLE_INSTRUCTIONS = "Answer shortly user request based on context.";
    private static final String AI_CHATBOT_SYMPTOM_INSTRUCTIONS = "Answer shortly by indicating in natural language what could be the possible disease based on context dataset. Ask for symptoms elaboration if not sufficient. Redirect user to a professionnal.";
    public static final String AI_INTENTION_DETECTOR = "You are intention detector. Reply ONLY 1 keyword based on request. \"ADVICE\":health advice requested. \"SYMPTOM\":symptom check requested. \"OTHER\":other requests.";
    public static final String AI_SYMPTOM_DETECTOR = "You are symptom retriever. Reply ONLY inline list of symptoms from sentence: ";

    private Context context;
    private ChatbotCallback<String, String> callback; // Updated callback type
    private String articleId;
    private Exception taskException;
    private final String apiKey;
    private final String articlesEmbeddings;
    private final String symptomsEmbeddings;
    private final String messagesHistory;

    /**
     * Constructs a new GenerateResponseTask.
     *
     * @param context The context from which to get resources
     * @param messagesHistory The chat history
     * @param articlesEmbeddings Embeddings for articles
     * @param symptomsEmbeddings Embeddings for symptoms
     * @param callback The callback to handle success or failure
     */
    public GenerateResponseTask(Context context, String messagesHistory, String articlesEmbeddings, String symptomsEmbeddings, ChatbotCallback<String, String> callback) {
        this.context = context;
        this.callback = callback;
        this.articlesEmbeddings = articlesEmbeddings;
        this.symptomsEmbeddings = symptomsEmbeddings;
        this.messagesHistory = messagesHistory;

        apiKey = context.getString(R.string.openai_key);
    }

    /**
     * Determines the type of request based on the user's message.
     *
     * @param request The user's message
     * @return The type of request
     */
    private RequestType getRequestType(String request)
    {
        String response = getModelOutput(AI_INTENTION_DETECTOR + "\nUser Message: " + request);

        switch (response)
        {
            case "ADVICE":
                return RequestType.ADVICE;
            case "SYMPTOM":
                return RequestType.SYMPTOM;
            default:
                return RequestType.OTHER;
        }
    }

    /**
     * Generates model output based on the given input.
     *
     * @param input The input string for the model
     * @return The output string from the model
     */
    public String getModelOutput(String input) {
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);
        String output = model.generate(input);
        System.out.println(output);
        return output;
    }

    /**
     * Retrieves a list of symptoms based on the user's request.
     *
     * @param request The user's request
     * @return A string listing the symptoms
     */
    private String getSymptomsList(String request)
    {
        return getModelOutput(AI_SYMPTOM_DETECTOR + request);
    }

    @Override
    protected String doInBackground(String... params)
    {
        String userMsg = params[0];
        RequestType type = getRequestType(userMsg);
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.withApiKey(apiKey);

        switch (type) {
            case ADVICE:
                return getArticleResponse(embeddingModel, userMsg);
            case SYMPTOM:
                return getSymptomResponse(embeddingModel);
            default:
                return getModelOutput(AI_CHATBOT_BASIC_INSTRUCTIONS + AI_CHATBOT_OTHER_INSTRUCTIONS + "\nChat history: " + messagesHistory);
        }
    }

    /**
     * Generates a response for a symptom check request.
     *
     * @param embeddingModel The embedding model to use
     * @return The response string
     */
    private String getSymptomResponse(OpenAiEmbeddingModel embeddingModel) {
        InMemoryEmbeddingStore<TextSegment> embeddingStore;
        embeddingStore = InMemoryEmbeddingStore.fromJson(symptomsEmbeddings);
        String symptoms = getSymptomsList(messagesHistory);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(5)
            .minScore(0.9)
            .build();

        List<Content> contents = contentRetriever.retrieve(new Query(symptoms));

        StringBuilder context = new StringBuilder();
        context.append("disease-symptoms dataset:").append("\n");
        if (!contents.isEmpty()) {
            for (Content content: contents){
                if (content.textSegment().metadata() != null && content.textSegment().metadata().containsKey("disease_name"))
                {
                    context.append(content.textSegment().metadata().getString("disease_name")).append(": ");
                    context.append(content.textSegment().text()).append("\n");
                }
            }
        }

        return getModelOutput(AI_CHATBOT_SYMPTOM_INSTRUCTIONS + "\nChat history: " + messagesHistory + "\nSymptoms: " + symptoms + "\nContext: " + context);
    }

    /**
     * Generates a response for an article advice request.
     *
     * @param embeddingModel The embedding model to use
     * @param userMsg The user's message
     * @return The response string
     */
    private String getArticleResponse(OpenAiEmbeddingModel embeddingModel, String userMsg) {
        StringBuilder context;
        InMemoryEmbeddingStore<TextSegment> embeddingStore;
        embeddingStore = InMemoryEmbeddingStore.fromJson(articlesEmbeddings);
        ContentRetriever contentRetriever = null;
        if (embeddingStore != null) {
            contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(2)
                    .minScore(0.88)
                    .build();
        }
        List<Content> contents = contentRetriever.retrieve(new Query(userMsg));

        if (!contents.isEmpty()) {
            if (contents.get(0).textSegment().metadata() != null && contents.get(0).textSegment().metadata().containsKey("article_id")) {
                articleId = contents.get(0).textSegment().metadata().getString("article_id");
                System.out.println(articleId);
            }
        }

        context = new StringBuilder();
        for (Content content : contents) {
            context.append(content.textSegment().text()).append("\n");
        }

        return getModelOutput(AI_CHATBOT_BASIC_INSTRUCTIONS + AI_CHATBOT_ARTICLE_INSTRUCTIONS + "\nChat history: " + messagesHistory + "\nContext: " + context);
    }

    @Override
    protected void onPostExecute(String answer) {
        if (taskException != null) {
            callback.onFailure(taskException);
        } else if (answer != null) {
            callback.onSuccess(answer, articleId);
        }
    }
}


