package com.example.pediatriccareassistant.controller.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Article;

/**
 * Adapter for displaying sections of an article in a RecyclerView.
 * Each item in the RecyclerView represents a section of the article with a title and content.
 */
public class ArticleSectionRecyclerViewAdapter extends RecyclerView.Adapter<ArticleSectionRecyclerViewAdapter.ReminderViewHolder> {

    private Context context;
    private Article article;

    /**
     * Constructs a new ArticleSectionRecyclerViewAdapter with default settings.
     */
    public ArticleSectionRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new ArticleSectionRecyclerViewAdapter with the specified context and article.
     *
     * @param context The context for inflating layouts
     * @param article The article whose sections are to be displayed
     */
    public ArticleSectionRecyclerViewAdapter(Context context, Article article) {
        this.context = context;
        this.article = article;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_article_section_view, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        String sectionTitle = article.getSectionsTitle().get(position);
        String sectionContent = article.getSectionsContent().get(position);

        // Set title and content to views, handle null cases
        if (sectionTitle == null) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setText(Html.fromHtml(sectionTitle, Html.FROM_HTML_MODE_LEGACY));
        }
        holder.content.setText(Html.fromHtml(sectionContent, Html.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public int getItemCount() {
        return article.getSectionsTitle().size();
    }

    /**
     * ViewHolder for an article section. Contains references to the title and content TextViews.
     */
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;

        /**
         * Constructs a new ReminderViewHolder.
         *
         * @param itemView The view representing an article section
         */
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_section_title);
            content = itemView.findViewById(R.id.article_section_content);
        }
    }
}
