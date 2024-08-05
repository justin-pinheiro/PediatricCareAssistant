package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Article;

public interface ArticleCallback {
    void onArticleRetrieved(Article article);
    void onNoArticleFound(Exception e);
}
