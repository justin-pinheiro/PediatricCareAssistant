package com.example.pediatriccareassistant.view;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.pediatriccareassistant.controller.firebasehandler.ArticleHandler;
import com.example.pediatriccareassistant.controller.firebasehandler.CategoryHandler;
import com.example.pediatriccareassistant.model.callback.ArticlesCallback;
import com.example.pediatriccareassistant.controller.adapter.ArticlesRecyclerViewAdapter;
import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.model.callback.DataCallback;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.databinding.FragmentArticlesBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * A fragment that displays a list of articles and a spinner to select categories.
 */
public class ArticlesFragment extends Fragment
{
    private static final Logger log = LoggerFactory.getLogger(ArticlesFragment.class);

    RecyclerView recyclerView;
    Spinner categories_spinner;
    ProgressBar progressBar;

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentArticlesBinding binding = FragmentArticlesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.articles_recycler_view);
        categories_spinner = root.findViewById(R.id.articles_categories_spinner);
        progressBar = root.findViewById(R.id.articles_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        retrieveCategories(root.getContext());
        displayArticlesWhenCategorySelected(root.getContext());

        return root;
    }

    /**
     * Sets up the listener for the category spinner to display articles based on the selected category.
     *
     * @param context The context in which the fragment is running.
     */
    private void displayArticlesWhenCategorySelected(Context context) {
        categories_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String category = categories_spinner.getSelectedItem().toString();

                // Handle the special case for "All categories"
                if(category.equals(getString(R.string.articles_categories_all)))
                    category = null;

                // Retrieve articles for the selected category
                ArticleHandler.getInstance().retrieveArticlesFromCategory(category, new ArticlesCallback() {
                    @Override
                    public void onArticlesRetrieved(ArrayList<Article> articles)
                    {
                        progressBar.setVisibility(View.GONE);
                        onArticlesRetrievedSuccessfully(context, articles);
                    }

                    @Override
                    public void onFailure(Exception e)
                    {
                        progressBar.setVisibility(View.GONE);
                        log.error("An error occurred: ", e);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    /**
     * Retrieves the list of categories from the Firebase Realtime Database.
     *
     * @param context The context in which the fragment is running.
     */
    private void retrieveCategories(Context context) {
        CategoryHandler.getInstance().retrieveCategoriesName(new DataCallback<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> data)
            {
                onCategoriesNamesRetrievedSuccessfully(context, data);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("An error occured:", e);
            }
        });
    }

    /**
     * Handles the successful retrieval of articles from the Firebase Realtime Database.
     *
     * @param context The context in which the fragment is running.
     * @param articles The list of articles retrieved.
     */
    private void onArticlesRetrievedSuccessfully(Context context, ArrayList<Article> articles)
    {
        ArticlesRecyclerViewAdapter articleAdapter = new ArticlesRecyclerViewAdapter(context, articles);
        recyclerView.setAdapter(articleAdapter);
    }

    /**
     * Handles the successful retrieval of category names from the Firebase Realtime Database.
     *
     * @param context The context in which the fragment is running.
     * @param categoriesNames The list of category names retrieved.
     */
    private void onCategoriesNamesRetrievedSuccessfully(Context context, ArrayList<String> categoriesNames)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoriesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories_spinner.setAdapter(adapter);
    }
}