package com.example.pediatriccareassistant.controller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.view.ReadArticleFragment;
import com.example.pediatriccareassistant.model.Article;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of articles in a RecyclerView.
 * Each item in the RecyclerView represents an article with a title, image, and button.
 */
public class ArticlesRecyclerViewAdapter extends RecyclerView.Adapter<ArticlesRecyclerViewAdapter.ArticleHolder> {

    private Context context;
    private ArrayList<Article> articles;

    /**
     * Constructs a new ArticlesRecyclerViewAdapter with default settings.
     */
    public ArticlesRecyclerViewAdapter() {
        // Default constructor
    }

    /**
     * Constructs a new ArticlesRecyclerViewAdapter with the specified context and list of articles.
     *
     * @param context The context for inflating layouts and managing fragments
     * @param articles The list of articles to be displayed
     */
    public ArticlesRecyclerViewAdapter(Context context, ArrayList<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_article_view, parent, false);
        return new ArticleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleHolder holder, int position) {
        Article article = articles.get(position);
        holder.title.setText(article.getTitle());

        // Load the article image using Glide
        Glide.with(holder.image.getContext())
                .load(article.getImageUrl())
                .placeholder(R.drawable.placeholder_view) // Optional placeholder
                .into(holder.image);

        // Set click listener to open ReadArticleFragment with the selected article
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadArticleFragment fragment = ReadArticleFragment.newInstance(article);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.main_fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    /**
     * ArticleHolder for an article item. Contains references to the title, image, and button views.
     */
    public static class ArticleHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;
        ImageButton button;

        /**
         * Constructs a new ArticleHolder.
         *
         * @param itemView The view representing an article item
         */
        public ArticleHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.read_article_title);
            image = itemView.findViewById(R.id.article_image);
            button = itemView.findViewById(R.id.article_button);
        }
    }
}
