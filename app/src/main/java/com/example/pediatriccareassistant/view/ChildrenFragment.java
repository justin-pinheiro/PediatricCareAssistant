package com.example.pediatriccareassistant.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.controller.AuthenticationController;
import com.example.pediatriccareassistant.controller.firebasehandler.ChildHandler;
import com.example.pediatriccareassistant.model.callback.ChildrenCallback;
import com.example.pediatriccareassistant.controller.adapter.ChildrenRecyclerViewAdapter;
import com.example.pediatriccareassistant.databinding.FragmentChildrenBinding;
import com.example.pediatriccareassistant.model.Child;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ChildrenFragment extends Fragment {

    private FragmentChildrenBinding binding;

    private TextView noChildrenMessage;
    private RecyclerView childrenRecycler;
    private FloatingActionButton addChildButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentChildrenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        noChildrenMessage = root.findViewById(R.id.children_no_children_message);
        childrenRecycler = root.findViewById(R.id.children_recycler_view);
        addChildButton = root.findViewById(R.id.children_add_children_button);

        ChildHandler.getInstance().retrieveChildrenFromUser(AuthenticationController.getInstance().getUserUid(), new ChildrenCallback()
            {
                @Override
                public void onChildrenRetrieved(ArrayList<Child> children)
                {
                    ChildrenRecyclerViewAdapter adapter = new ChildrenRecyclerViewAdapter(root.getContext(), children);
                    childrenRecycler.setAdapter(adapter);

                    if (children.isEmpty()) {
                        noChildrenMessage.setVisibility(View.VISIBLE);
                    }
                    else {
                        noChildrenMessage.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildrenNotFound() {

            }
        });

        return root;
    }
}