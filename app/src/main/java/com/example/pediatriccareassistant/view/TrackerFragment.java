package com.example.pediatriccareassistant.view;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.callback.ChildrenCallback;
import com.example.pediatriccareassistant.controller.TrackerFragmentController;
import com.example.pediatriccareassistant.controller.adapter.TrackerViewPagerAdapter;
import com.example.pediatriccareassistant.databinding.FragmentTrackerBinding;
import com.example.pediatriccareassistant.model.Child;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

/**
 * A fragment that displays a tab layout with two pages: Measurements and Charts.
 * Allows users to select a child from a dropdown and view their data in the respective tabs.
 */
public class TrackerFragment extends MainMenuBaseFragment
{
    private FragmentTrackerBinding binding;
    private TrackerFragmentController controller;

    private TextView noChildrenTextView;
    private CardView childrenChoiceCard;
    private TabLayout tabLayout;
    private ViewPager2 fragmentContainer;
    private Spinner childrenSpinner;

    /**
     * Creates and returns the view for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the fragment.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentTrackerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        noChildrenTextView = root.findViewById(R.id.tracker_no_children_text);
        noChildrenTextView.setVisibility(View.GONE);
        childrenChoiceCard = root.findViewById(R.id.tracker_child_choice_card);
        childrenSpinner = root.findViewById(R.id.tracker_child_choice_spinner);
        tabLayout = root.findViewById(R.id.tracker_tab_layout);
        fragmentContainer = root.findViewById(R.id.tracker_fragment_container);

        setManageChildrenLink(root);

        controller = new TrackerFragmentController(getChildFragmentManager(), fragmentContainer);
        TrackerViewPagerAdapter adapter = new TrackerViewPagerAdapter(this, controller::notifyChildDataFragment);
        fragmentContainer.setAdapter(adapter);

        controller.retrieveUserChildren(new ChildrenCallback() {
            @Override
            public void onChildrenRetrieved(ArrayList<Child> children)
            {
                displayUserChildren(root.getContext());

                adapter.setCurrentChild(controller.getSelectedChild());

                setTabLayout();
            }

            @Override
            public void onChildrenNotFound()
            {

            }
        });

        return root;
    }

    private static void setManageChildrenLink(View root) {
        TextView manageChildrenLink = root.findViewById(R.id.tracker_manage_children_link);
        SpannableString content = new SpannableString("Manage children");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        manageChildrenLink.setText(content);
    }

    /**
     * Handles the case when no children are found.
     */
    private void onChildrenEmpty()
    {
        childrenChoiceCard.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.GONE);
        noChildrenTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Displays the list of children in the spinner.
     *
     * @param context  The context to use for creating the ArrayAdapter.
     */
    private void displayUserChildren(Context context)
    {
        if (controller.getChildren().isEmpty()) onChildrenEmpty();
        else
        {
            childrenChoiceCard.setVisibility(View.VISIBLE);
            noChildrenTextView.setVisibility(View.GONE);
            setChildrenSpinner(context, controller.getChildrenNames());
        }
    }

    /**
     * Sets up the spinner for selecting a child.
     *
     * @param context          The context to use for creating the ArrayAdapter.
     * @param childrenNames    The list of children names to display.
     */
    private void setChildrenSpinner(Context context, ArrayList<String> childrenNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                childrenNames
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childrenSpinner.setAdapter(adapter);

        childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                controller.onChildSelected(position);
                controller.notifyChildDataFragment(controller.getSelectedChild());

                // Trigger chart reload if the Charts tab is selected
                if (tabLayout.getSelectedTabPosition() == 1) {
                    ChartsFragment chartsFragment = (ChartsFragment) getChildFragmentManager().findFragmentByTag("f1");
                    if (chartsFragment != null) {
                        chartsFragment.reloadChart();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no child is selected, if needed
            }
        });
    }

    /**
     * Sets up the TabLayout with tabs.
     */
    private void setTabLayout() {
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, fragmentContainer, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0: tab.setText(TrackerFragmentController.MEASUREMENTS_TAB_TEXT); break;
                    case 1: tab.setText(TrackerFragmentController.CHARTS_TAB_TEXT); break;
                }
            }
        });
        tabLayoutMediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) { // If the Charts tab is selected
                    fragmentContainer.post(() -> {
                        Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + tab.getPosition());
                        if (fragment instanceof ChartsFragment) {
                            ((ChartsFragment) fragment).reloadChart();
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) { // If the Charts tab is reselected
                    fragmentContainer.post(() -> {
                        Fragment fragment = getChildFragmentManager().findFragmentByTag("f" + tab.getPosition());
                        if (fragment instanceof ChartsFragment) {
                            ((ChartsFragment) fragment).reloadChart();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
