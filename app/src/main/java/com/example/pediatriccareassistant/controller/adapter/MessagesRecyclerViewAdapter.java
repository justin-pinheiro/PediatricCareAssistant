package com.example.pediatriccareassistant.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.model.Message;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of messages in a RecyclerView.
 * Messages can be from a user, a bot, or an article.
 */
public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Message> messages;

    /**
     * Constructs a new MessagesRecyclerViewAdapter with default settings.
     */
    public MessagesRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new MessagesRecyclerViewAdapter with the specified context and list of messages.
     *
     * @param context The context for inflating layouts
     * @param messages The list of messages to be displayed
     */
    public MessagesRecyclerViewAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (message.getArticle() != null) {
            return 2; // Article message type
        }

        switch (message.getSender()) {
            case USER:
                return 0; // User message type
            case BOT:
                return 1; // Bot message type
            default:
                return -1; // Default type (should not happen)
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(context).inflate(R.layout.sample_user_message_view, parent, false);
                return new UserViewHolder(view);
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.sample_bot_message_view, parent, false);
                return new BotViewHolder(view);
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.sample_article_message_view, parent, false);
                return new ArticleViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type: " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getArticle() != null) {
            // Handle article view type
            ArrayList<Article> articles = new ArrayList<>();
            articles.add(message.getArticle());
            ArticlesRecyclerViewAdapter adapter = new ArticlesRecyclerViewAdapter(context, articles);
            ((ArticleViewHolder) holder).articleRecyclerView.setAdapter(adapter);
            ((ArticleViewHolder) holder).articleRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            // Handle user or bot message types
            switch (message.getSender()) {
                case USER:
                    ((UserViewHolder) holder).content.setText(message.getContent());
                    break;
                case BOT:
                    ((BotViewHolder) holder).content.setText(message.getContent());
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * ViewHolder for a user message item. Contains a reference to the content TextView.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        /**
         * Constructs a new UserViewHolder.
         *
         * @param itemView The view representing a user message item
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.user_message_content);
        }
    }

    /**
     * ViewHolder for a bot message item. Contains a reference to the content TextView.
     */
    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView content;

        /**
         * Constructs a new BotViewHolder.
         *
         * @param itemView The view representing a bot message item
         */
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.bot_message_content);
        }
    }

    /**
     * ViewHolder for an article message item. Contains a reference to the RecyclerView for displaying articles.
     */
    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        RecyclerView articleRecyclerView;

        /**
         * Constructs a new ArticleViewHolder.
         *
         * @param itemView The view representing an article message item
         */
        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleRecyclerView = itemView.findViewById(R.id.article_message_recycler_view);
        }
    }
}
