package com.example.pediatriccareassistant.model.callback;

import com.example.pediatriccareassistant.model.Article;

import java.util.ArrayList;

public interface ArticlesCallback
{
    public void onArticlesRetrieved(ArrayList<Article> articles);
    public void onFailure(Exception e);
}
