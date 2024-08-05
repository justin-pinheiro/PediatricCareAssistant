package com.example.pediatriccareassistant.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.adapter.RemindersRecyclerViewAdapter;
import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.firebasehandler.ReminderHandler;
import com.example.pediatriccareassistant.model.Reminder;
import com.example.pediatriccareassistant.databinding.FragmentRemindersBinding;
import com.example.pediatriccareassistant.model.callback.RemindersCallback;

import java.util.ArrayList;

public class RemindersFragment extends Fragment
{
    RecyclerView recyclerView;
    ProgressBar progressBar;
    LinearLayout noRemindersLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FragmentRemindersBinding binding = FragmentRemindersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.reminders_recycle_view);
        progressBar = root.findViewById(R.id.reminders_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        noRemindersLayout = root.findViewById(R.id.reminders_no_reminders_layout);
        noRemindersLayout.setVisibility(View.GONE);

        ReminderHandler.getInstance().retrieveReminders(AuthenticationController.getInstance().getUserUid(), new RemindersCallback() {
            @Override
            public void onRemindersRetrieved(ArrayList<Reminder> reminders) {
                progressBar.setVisibility(View.GONE);

                if (reminders.isEmpty())
                    noRemindersLayout.setVisibility(View.VISIBLE);

                RemindersRecyclerViewAdapter adapter = new RemindersRecyclerViewAdapter(root.getContext(), reminders);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

            }

            @Override
            public void onRemindersNotFound(Exception e) {
                progressBar.setVisibility(View.GONE);

            }
        });

        return root;
    }
}