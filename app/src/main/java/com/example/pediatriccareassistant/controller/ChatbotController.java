package com.example.pediatriccareassistant.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.controller.adapter.MessagesRecyclerViewAdapter;
import com.example.pediatriccareassistant.controller.firebasehandler.ArticleHandler;
import com.example.pediatriccareassistant.controller.firebasehandler.EmbeddingsHandler;
import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.model.callback.ArticleCallback;
import com.example.pediatriccareassistant.model.callback.ChatbotCallback;
import com.example.pediatriccareassistant.model.callback.ChatbotLoadEmbeddingsCallback;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.example.pediatriccareassistant.model.Message;
import com.example.pediatriccareassistant.model.MessagesHistory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatbotController
{
    public static final String AI_ASSISTANT_INTRODUCTORY_MESSAGE =
            "Hello! I am your virtual pediatric care assistant.\n\n" +
            "I can browse through the app's articles to provide advice based on your needs.\n\n" +
            "Additionally, I can assist in diagnosing symptoms you describe.\n\n" +
            "How can I assist you today?";
    public static final String EMBEDDINGS_LOADING_FAILURE_MESSAGE = "Failed to load embeddings";
    public static final String AI_ASSISTANT_REFER_TO_ARTICLE_MESSAGE = "For additional information, refer to the following article:";
    private static final Logger log = LoggerFactory.getLogger(ChatbotController.class);
    public static final String ARTICLES_EMBEDDINGS_PATH = "articles_embeddings";
    public static final String SYMPTOMS_EMBEDDINGS_PATH = "symptoms_embeddings";

    private final ArrayList<Message> messages;
    private final MessagesRecyclerViewAdapter messages_adapter;
    private final RecyclerView messages_recycler_view;
    private LinearLayout typing_layout;

    private MessagesHistory history;
    private String articlesEmbeddingsJson;
    private String symptomsEmbeddingsJson;

    public ChatbotController(Context context, RecyclerView messages_recycler_view, LinearLayout typing_layout, ChatbotLoadEmbeddingsCallback callback)
    {
        messages = new ArrayList<>();
        messages_adapter = new MessagesRecyclerViewAdapter(context, messages);

        this.typing_layout = typing_layout;
        typing_layout.setVisibility(View.GONE);

        this.messages_recycler_view = messages_recycler_view;
        messages_recycler_view.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, true));
        messages_recycler_view.setAdapter(messages_adapter);

        history = new MessagesHistory(6);

        loadEmbeddings(context, callback);
    }

    public void sendMessage(Context context, String userMsg)
    {
        addUserMessageToChat(userMsg);
        typing_layout.setVisibility(View.VISIBLE);

        new GenerateResponseTask(context, history.toString(), articlesEmbeddingsJson, symptomsEmbeddingsJson, new ChatbotCallback<String, String>() {
            @Override
            public void onSuccess(String answer, String id)
            {
                typing_layout.setVisibility(View.GONE);
                addBotMessageToChat(answer);

                if (id != null)
                {
                    ArticleHandler.getInstance().retrieveArticleFromId(id, new ArticleCallback() {
                        @Override
                        public void onArticleRetrieved(Article article) {
                            addBotMessageToChat(AI_ASSISTANT_REFER_TO_ARTICLE_MESSAGE);
                            addArticleMessageToChat(article);
                        }

                        @Override
                        public void onNoArticleFound(Exception e) {
                            addBotMessageToChat("Article " + id + " not found.");
                            log.error("An error occured: ", e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                typing_layout.setVisibility(View.GONE);
                Log.e("SendMessage", "Failed to generate response", e);
            }
        }).execute(userMsg);
    }


    private void loadEmbeddings(Context context, ChatbotLoadEmbeddingsCallback callback) {
        callback.onStart();

        String savedArticlesEmbeddings = getSavedEmbeddings(context, ARTICLES_EMBEDDINGS_PATH);
        String savedSymptomsEmbeddings = getSavedEmbeddings(context, SYMPTOMS_EMBEDDINGS_PATH);

        final AtomicInteger loadCounter = new AtomicInteger(0);
        final int totalTasks = 2;

        DataCallback<String> commonCallback = new DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (loadCounter.incrementAndGet() == totalTasks) {
                    // Call onSuccess only when both embeddings are loaded
                    addBotMessageToChat(AI_ASSISTANT_INTRODUCTORY_MESSAGE);
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Call onFailure immediately if any of the tasks fail
                callback.onFailure();
            }
        };

        if (savedArticlesEmbeddings != null) {
            articlesEmbeddingsJson = savedArticlesEmbeddings;
            loadCounter.incrementAndGet(); // Increment counter if already loaded
        } else {
            EmbeddingsHandler.getInstance().retrieveEmbeddings(ARTICLES_EMBEDDINGS_PATH, new DataCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    articlesEmbeddingsJson = data;
                    saveEmbeddings(context, data, ARTICLES_EMBEDDINGS_PATH);
                    commonCallback.onSuccess(data);
                }

                @Override
                public void onFailure(Exception e) {
                    commonCallback.onFailure(e);
                }
            });
        }

        if (savedSymptomsEmbeddings != null) {
            symptomsEmbeddingsJson = savedSymptomsEmbeddings;
            loadCounter.incrementAndGet(); // Increment counter if already loaded
        } else {
            EmbeddingsHandler.getInstance().retrieveEmbeddings(SYMPTOMS_EMBEDDINGS_PATH, new DataCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    symptomsEmbeddingsJson = data;
                    saveEmbeddings(context, data, SYMPTOMS_EMBEDDINGS_PATH);
                    commonCallback.onSuccess(data);
                }

                @Override
                public void onFailure(Exception e) {
                    commonCallback.onFailure(e);
                }
            });
        }

        // Check if both were already loaded
        if (loadCounter.get() == totalTasks) {
            addBotMessageToChat(AI_ASSISTANT_INTRODUCTORY_MESSAGE);
            callback.onSuccess();
        }
    }

    public void saveEmbeddings(Context context, String embeddingsJson, String embeddingsPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(embeddingsPath, embeddingsJson);
        editor.apply();
    }

    public String getSavedEmbeddings(Context context, String embeddingsPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(embeddingsPath, null);
    }

    private void addMessageToChat(Message.SenderType sender, String content, Article article) {
        Message message = new Message(sender, content, article);
        history.add(message);
        messages.add(0, message);
        messages_adapter.notifyItemInserted(0);
        messages_recycler_view.smoothScrollToPosition(0);
    }

    private void addArticleMessageToChat(Article article) {
        addMessageToChat(Message.SenderType.BOT, null, article);
    }

    private void addBotMessageToChat(String message) {
        addMessageToChat(Message.SenderType.BOT, message, null);
    }

    private void addUserMessageToChat(String message) {
        addMessageToChat(Message.SenderType.USER, message, null);
    }
}
