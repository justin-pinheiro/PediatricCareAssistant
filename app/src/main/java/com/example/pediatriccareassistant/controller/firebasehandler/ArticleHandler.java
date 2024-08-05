package com.example.pediatriccareassistant.controller.firebasehandler;

import androidx.annotation.NonNull;

import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.model.callback.ArticleCallback;
import com.example.pediatriccareassistant.model.callback.ArticlesCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ArticleHandler extends BaseHandler {

    private static final ArticleHandler instance = new ArticleHandler();

    private ArticleHandler() { }

    public static ArticleHandler getInstance() {
        return instance;
    }

    public void retrieveArticlesFromCategory(final String category, ArticlesCallback callback) {
        DatabaseReference database = getDatabaseReference("articles");

        database.get().addOnCompleteListener(task -> {
            ArrayList<Article> articles = new ArrayList<>();

            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = Article.fromSnapshot(dataSnapshot);

                    if (category == null || article.getCategories().contains(category)) {
                        articles.add(article);
                    }
                }

                callback.onArticlesRetrieved(articles);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void retrieveArticleFromPosition(int position, ArticleCallback callback) {
        DatabaseReference database = getDatabaseReference("articles");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Article article = null;
                int i = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (position == i) {
                        article = Article.fromSnapshot(dataSnapshot);
                        callback.onArticleRetrieved(article);
                        break;
                    }
                    i++;
                }

                if (article == null)
                    callback.onNoArticleFound(new NullPointerException());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onNoArticleFound(error.toException());
            }
        });
    }

    public void retrieveArticleFromId(String idArticle, ArticleCallback callback) {
        DatabaseReference database = getDatabaseReference("articles");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Article article = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    article = Article.fromSnapshot(dataSnapshot);

                    if (article.getId().equals(idArticle)) {
                        callback.onArticleRetrieved(article);
                        break;
                    }
                }

                if (article == null)
                    callback.onNoArticleFound(new NullPointerException());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onNoArticleFound(error.toException());
            }
        });
    }
}
