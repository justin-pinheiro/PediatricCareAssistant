package com.example.pediatriccareassistant.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pediatriccareassistant.controller.firebasehandler.ArticleHandler;
import com.example.pediatriccareassistant.controller.firebasehandler.ReminderHandler;
import com.example.pediatriccareassistant.model.callback.ArticleCallback;
import com.example.pediatriccareassistant.model.callback.ReminderCallback;
import com.example.pediatriccareassistant.controller.adapter.ArticlesRecyclerViewAdapter;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.HospitalRetriever;
import com.example.pediatriccareassistant.controller.adapter.HospitalsRecyclerViewAdapter;
import com.example.pediatriccareassistant.controller.adapter.RemindersRecyclerViewAdapter;
import com.example.pediatriccareassistant.model.Article;
import com.example.pediatriccareassistant.controller.DateBasedRandom;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.Hospital;
import com.example.pediatriccareassistant.model.Reminder;
import com.example.pediatriccareassistant.databinding.FragmentHomeBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * HomeFragment is responsible for displaying articles, reminders, and nearby hospitals.
 */
public class HomeFragment extends MainMenuBaseFragment
{
    private static final Logger log = LoggerFactory.getLogger(HomeFragment.class);
    private FragmentHomeBinding binding;

    private RecyclerView articlesRecyclerView;
    private RecyclerView remindersRecyclerView;
    private RecyclerView hospitalRecyclerView;

    private static final String SHARED_PREFS_NAME = "ArticlePrefs";
    private static final String KEY_CACHED_ARTICLE_INDEX = "CachedArticleIndex";
    private Map<Integer, Article> articleCache = new HashMap<>();

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerViews
        articlesRecyclerView = root.findViewById(R.id.home_article_recycler_view);
        remindersRecyclerView = root.findViewById(R.id.home_reminders_recycler_view);
        hospitalRecyclerView = root.findViewById(R.id.home_hospitals_recycler_view);

        // Display articles, reminders, and nearby hospitals
        displaySelectedArticle(root.getContext());
        displayNextReminder(root.getContext());
        displayNearbyHospitals(root.getContext());

        return root;
    }

    /**
     * Displays nearby hospitals in the RecyclerView.
     *
     * @param context The context in which the fragment is running.
     */
    private void displayNearbyHospitals(Context context)
    {
        HospitalRetriever hospitalRetriever = new HospitalRetriever(context, getActivity());
        hospitalRetriever.getNearbyHospitals(2, 8000, new HospitalRetriever.HospitalsCallback() {
            @Override
            public void onHospitalsRetrieved(ArrayList<Hospital> hospitals)
            {
                onHospitalsRetrievedSuccessfully(context, hospitals);
            }

            @Override
            public void onFailure(Exception e) {
                log.error("An error occured: ", e);
            }
        });
    }

    /**
     * Handles the successful retrieval of nearby hospitals.
     *
     * @param context The context in which the fragment is running.
     * @param hospitals The list of retrieved hospitals.
     */
    private void onHospitalsRetrievedSuccessfully(Context context, ArrayList<Hospital> hospitals)
    {
        HospitalsRecyclerViewAdapter hospitalsAdapter = new HospitalsRecyclerViewAdapter(context, hospitals);

        hospitalRecyclerView.setAdapter(hospitalsAdapter);
        hospitalRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Notify the adapter that data has changed
        new Handler(Looper.getMainLooper()).post(() -> hospitalsAdapter.notifyDataSetChanged());
    }

    /**
     * Displays the next reminder in the RecyclerView.
     *
     * @param context The context in which the fragment is running.
     */
    private void displayNextReminder(Context context) {
        ReminderHandler.getInstance().retrieveNextReminderFromUserId(AuthenticationController.getInstance().getUserUid(), new ReminderCallback() {
            @Override
            public void onReminderRetrieved(Reminder reminder)
            {
                onReminderRetrievedSuccessfully(context, reminder);
            }

            @Override
            public void onReminderNotFound(Exception e) {
                log.error("An error occured: ", e);
            }
        });
    }

    /**
     * Handles the successful retrieval of the next reminder.
     *
     * @param context The context in which the fragment is running.
     * @param reminder The retrieved reminder.
     */
    private void onReminderRetrievedSuccessfully(Context context, Reminder reminder) {
        ArrayList<Reminder> reminders = new ArrayList<>();
        reminders.add(reminder);

        RemindersRecyclerViewAdapter remindersAdapter = new RemindersRecyclerViewAdapter(context, reminders);
        remindersRecyclerView.setAdapter(remindersAdapter);
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * Displays a randomly selected article in the RecyclerView.
     *
     * @param context The context in which the fragment is running.
     */
    private void displaySelectedArticle(Context context)
    {
        int articleIndex = DateBasedRandom.getRandomNumberByDate(Calendar.getInstance(), 0, 103);

        ArticleHandler.getInstance().retrieveArticleFromPosition(articleIndex, new ArticleCallback() {
            @Override
            public void onArticleRetrieved(Article article) {
                if (article != null) {
                    // Update cache with the new article
                    articleCache.put(articleIndex, article);
                    saveCachedArticleIndex(context, articleIndex);
                    onArticleRetrievedSuccessfully(context, article);
                } else {
                    log.error("Retrieved article is null for index: " + articleIndex);
                }
            }

            @Override
            public void onNoArticleFound(Exception e) {
                log.error("An error occurred: ", e);
            }
        });
    }

    /**
     * Saves the index of the current article to SharedPreferences.
     *
     * @param context The context in which the fragment is running.
     * @param index The index of the article to cache.
     */
    private void saveCachedArticleIndex(Context context, int index) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_CACHED_ARTICLE_INDEX, index);
        editor.apply();
    }

    /**
     * Handles the successful retrieval of the selected article.
     *
     * @param context The context in which the fragment is running.
     * @param article The retrieved article.
     */
    private void onArticleRetrievedSuccessfully(Context context, Article article) {
        ArrayList<Article> articles = new ArrayList<>();
        articles.add(article);

        ArticlesRecyclerViewAdapter articlesAdapter = new ArticlesRecyclerViewAdapter(context, articles);
        articlesRecyclerView.setAdapter(articlesAdapter);
        articlesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * Called when the view created by the fragment is being destroyed.
     * Used to clean up resources.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}