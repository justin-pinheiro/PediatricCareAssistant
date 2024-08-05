package com.example.pediatriccareassistant.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pediatriccareassistant.controller.adapter.ArticleSectionRecyclerViewAdapter;
import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.databinding.FragmentReadArticleBinding;

public class ReadArticleFragment extends Fragment
{
    private static final String ARG_ARTICLE = "article";

    private Article article;

    public static ReadArticleFragment newInstance(Article article) {
        ReadArticleFragment fragment = new ReadArticleFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            article = (Article) getArguments().getSerializable(ARG_ARTICLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentReadArticleBinding binding = FragmentReadArticleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView title = root.findViewById(R.id.read_article_title);
        title.setText(article.getTitle());

        ImageView image = root.findViewById(R.id.read_article_image);
        Glide.with(image.getContext())
                .load(article.getImageUrl())
                .placeholder(R.drawable.placeholder_view) // Optional placeholder
                .into(image);

        ArticleSectionRecyclerViewAdapter adapter = new ArticleSectionRecyclerViewAdapter(root.getContext(), article);

        RecyclerView recyclerView = root.findViewById(R.id.read_article_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        return root;
    }
}